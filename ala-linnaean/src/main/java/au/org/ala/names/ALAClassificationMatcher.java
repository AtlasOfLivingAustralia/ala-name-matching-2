package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.Observable;
import au.org.ala.vocab.TaxonomicStatus;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.extra.jmx.JmxSupport;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.Rank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ALAClassificationMatcher extends ClassificationMatcher<AlaLinnaeanClassification, AlaLinnaeanInferencer, AlaLinnaeanFactory, MatchMeasurement> {
    private static final Logger logger = LoggerFactory.getLogger(ALAClassificationMatcher.class);

    private static final int SPECIES_RANK_ID = RankIDAnalysis.idFromRank(Rank.SPECIES);
    private static final int GENUS_RANK_ID = RankIDAnalysis.idFromRank(Rank.GENUS);
    private static final int FAMILY_RANK_ID = RankIDAnalysis.idFromRank(Rank.FAMILY);
    private static final int ORDER_RANK_ID = RankIDAnalysis.idFromRank(Rank.ORDER);
    private static final int CLASS_RANK_ID = RankIDAnalysis.idFromRank(Rank.CLASS);
    private static final int PHYLUM_RANK_ID = RankIDAnalysis.idFromRank(Rank.PHYLUM);
    private static final int KINGDOM_RANK_ID = RankIDAnalysis.idFromRank(Rank.KINGDOM);

    /** First four characters for valid checks */
    private static final int MIN_VALID_LENGTH = 4;

    /** A match sorter that takes taxonomic status into account if the probabilities are close enough */
    private static final Comparator<Match<AlaLinnaeanClassification, MatchMeasurement>> MATCH_SORTER = new Comparator<Match<AlaLinnaeanClassification, MatchMeasurement>>() {
        private static final double DISTANCE = 0.10; // Point where other considerations take over
        private static final double ACCURACY = 0.00001; // Limit of probability comparison

        @Override
        public int compare(Match<AlaLinnaeanClassification, MatchMeasurement> o1, Match<AlaLinnaeanClassification, MatchMeasurement> o2) {
            double p1 = o1.getProbability().getPosterior();
            double p2 = o2.getProbability().getPosterior();
            AlaLinnaeanClassification m1 = o1.getMatch();
            AlaLinnaeanClassification m2 = o2.getMatch();
            if (Math.abs(p1 - p2) < DISTANCE) {
                TaxonomicStatus t1 = m1.taxonomicStatus != null ? m1.taxonomicStatus : TaxonomicStatus.unknown;
                TaxonomicStatus t2 = m2.taxonomicStatus != null ? m2.taxonomicStatus : TaxonomicStatus.unknown;
                if (t1 != t2)
                    return t1.compareTo(t2);
            }
            if (Math.abs(p1 - p2) < ACCURACY)
                return Double.compare(m2.weight, m1.weight);
            return Double.compare(p2, p1);
        }
    };

    /** Hints for kingdoms */
    private final Cache<KingdomKey, AlaLinnaeanClassification> kingdomCache;
    /** The list of valid localities (by locationId) that allow locality tests */
    private final Set<String> localityScope;

    /**
     * Create with a searcher and inferencer.
     *
     * @param factory  The factory for creating objects for the matcher to work on
     * @param searcher The mechanism for getting candidiates
     * @param config The classification matcher configuration
     * @param analyserConfig The analyser configyuation
     * @param localityScope The locality scope
     */
    public ALAClassificationMatcher(AlaLinnaeanFactory factory, ClassifierSearcher<?> searcher, ClassificationMatcherConfiguration config, AnalyserConfig analyserConfig, Set<String> localityScope) {
        super(factory, searcher, config, analyserConfig);
        Cache2kBuilder<KingdomKey, AlaLinnaeanClassification> builder = Cache2kBuilder.of(KingdomKey.class, AlaLinnaeanClassification.class)
                .entryCapacity(this.getConfig().getSecondaryCacheSize())
                .permitNullValues(true)
                .loader(this::doFindKingdom);
        if (this.getConfig().isEnableJmx())
            builder.enable(JmxSupport.class);
        this.kingdomCache = builder.build();
        this.localityScope = localityScope;
    }

    /**
     * Annotate the result list with additional information and issues.
     *
     * @param results The list of results
     *
     * @return the annotated and otherwise modified list
     *
     * @throws BayesianException     if there is a problem retrieving information or makign inferences
     */
    @Override
    protected List<Match<AlaLinnaeanClassification, MatchMeasurement>> annotate(List<Match<AlaLinnaeanClassification, MatchMeasurement>> results) throws BayesianException {
        List<Match<AlaLinnaeanClassification, MatchMeasurement>> results1 = super.annotate(results);
        return results1.stream()
                .map(m -> this.detectMisapplied(m, results1))
                .map(m -> this.detectExcluded(m, results1))
                .map(m -> this.detectPartialMisapplied(m, results1))
                .map(m -> this.detectPartialExcluded(m, results1))
                .map(m -> this.detectParentChildSynonym(m, results1))
                .collect(Collectors.toList());
    }

    /**
     * Mark any results with misapplied taxonomic status
     *
     * @param results The list of candidate matches
     */
    protected Match<AlaLinnaeanClassification, MatchMeasurement> detectMisapplied(final Match<AlaLinnaeanClassification, MatchMeasurement> match, final List<Match<AlaLinnaeanClassification, MatchMeasurement>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        if (m.taxonomicStatus != null && m.taxonomicStatus.isMisappliedFlag())
            return match.with(AlaLinnaeanFactory.MISAPPLIED_NAME);
        return match;
    }


    /**
     * Mark any results with excluded taxonomic status
     *
     * @param results The list of candidate matches
     */
    protected Match<AlaLinnaeanClassification, MatchMeasurement> detectExcluded(final Match<AlaLinnaeanClassification, MatchMeasurement> match, final List<Match<AlaLinnaeanClassification, MatchMeasurement>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        if (m.taxonomicStatus != null && m.taxonomicStatus.isExcludedFlag())
            return match.with(AlaLinnaeanFactory.EXCLUDED_NAME);
        return match;
    }

    /**
     * Mark any accepted/synonym results which also have misapplied values.
     *
     * @param results The list of candidate matches
     */
    protected Match<AlaLinnaeanClassification, MatchMeasurement> detectPartialMisapplied(final Match<AlaLinnaeanClassification, MatchMeasurement> match, final List<Match<AlaLinnaeanClassification, MatchMeasurement>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        if (m.taxonomicStatus != null && m.scientificName != null && m.taxonomicStatus.isPlaced()) {
            for (Match<AlaLinnaeanClassification, MatchMeasurement> match2 : results) {
                AlaLinnaeanClassification m2 = match2.getMatch();
                if (match2 != match && m.scientificName.equalsIgnoreCase(m2.scientificName) && m2.taxonomicStatus != null && m2.taxonomicStatus.isMisappliedFlag())
                    return match.with(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME);
            }
        }
        return match;
    }

    /**
     * Mark any results that also have excluded names,
     *
     * @param results The list of candidate matches
     */
    protected Match<AlaLinnaeanClassification, MatchMeasurement> detectPartialExcluded(final Match<AlaLinnaeanClassification, MatchMeasurement> match, final List<Match<AlaLinnaeanClassification, MatchMeasurement>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        if (m.taxonomicStatus != null && m.scientificName != null && m.taxonomicStatus.isPlaced()) {
            for (Match<AlaLinnaeanClassification, MatchMeasurement> match2 : results) {
                AlaLinnaeanClassification m2 = match2.getMatch();
                if (match2 != match && m.scientificName.equalsIgnoreCase(m2.scientificName) && m2.taxonomicStatus != null && m2.taxonomicStatus.isExcludedFlag())
                    return match.with(AlaLinnaeanFactory.PARTIALLY_EXCLUDED_NAME);
            }
        }
        return match;
    }

    /**
     * Detect a parent-child synonym in the possible matches.
     *
     * @param match The match to test
     * @param results Other possible results
     *
     * @return If there is a possible parent/child synonym, a match flagged with the synoym
     */
    protected Match<AlaLinnaeanClassification, MatchMeasurement> detectParentChildSynonym(final Match<AlaLinnaeanClassification, MatchMeasurement> match, final List<Match<AlaLinnaeanClassification, MatchMeasurement>> results) {
        final String taxonId = match.getMatch().taxonId;
        final String scientificName = match.getMatch().scientificName;

        if (taxonId == null || scientificName == null)
            return match;
        if (results.stream().anyMatch(m ->
                m != match &&
                m.getMatch() != m.getAccepted() &&
                m.getAccepted().parentNameUsageId != null &&
                taxonId.equals(m.getAccepted().parentNameUsageId) &&
                scientificName.equalsIgnoreCase(m.getMatch().scientificName)
          ))
            return match.with(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM);
        return match;
    }

    /**
     * Find a single match that is acceptable.
     *
     * @param results The list of results
     * @return A single acceptable result, or null
     */
    @Override
    protected Match<AlaLinnaeanClassification, MatchMeasurement> findSingle(AlaLinnaeanClassification classification, List<Match<AlaLinnaeanClassification, MatchMeasurement>> results, MatchMeasurement measurement) throws BayesianException {
        results = results.stream().filter(m -> !this.isBadEvidence(m)).collect(Collectors.toList());
        if (results.isEmpty())
            return null;
        // Choose a trial candidate
        final Match<AlaLinnaeanClassification, MatchMeasurement> trial = results.get(0);
        final AlaLinnaeanClassification tc = trial.getMatch();
        final String tn = tc.scientificName;
        final TaxonomicStatus tts = tc.taxonomicStatus != null ? tc.taxonomicStatus : TaxonomicStatus.unknown;
        if (results.size() == 1)
            return trial;
        // From now on, there should be more than one result
        // See if we have an unresolvable homonym
        Match<AlaLinnaeanClassification, MatchMeasurement> unresolvedHomonym = this.detectUnresolvableHomonym(classification, results);
        if (unresolvedHomonym != null)
            return unresolvedHomonym;
        // If we have not miscellaneous/rest miscellaneous
        if (!this.isMiscellaneous(trial) && results.stream().allMatch(m -> m == trial || this.isMiscellaneous(m)))
            return trial.boost(results).with(AlaLinnaeanFactory.MULTIPLE_MATCHES);
        // If we have a vouchered name and the other names are not vouchered
        if (trial.getMatch().voucher != null && results.stream().allMatch(m -> m == trial || m.getMatch().voucher == null))
            return trial.boost(results).with(AlaLinnaeanFactory.MULTIPLE_MATCHES);
        // See if we have a single accepted/variety of synonyms
        if (tts.isAcceptedFlag() && results.stream().allMatch(m -> m == trial || m.getMatch().taxonomicStatus != null && m.getMatch().taxonomicStatus.isSynonymLike() && m.getCandidate().matchClean(tn, AlaLinnaeanFactory.scientificName)))
            return trial.boost(results).with(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM);
        // See if we have collection of misapplied results
        if (tts.isMisappliedFlag() && results.stream().allMatch(m -> m == trial || m.getMatch().taxonomicStatus != null && m.getMatch().taxonomicStatus.isMisappliedFlag() &&  m.getCandidate().matchClean(tn, AlaLinnaeanFactory.scientificName)))
            return trial.boost(results).with(AlaLinnaeanFactory.MISAPPLIED_NAME);
        List<Match<AlaLinnaeanClassification, MatchMeasurement>> acceptable = results.stream().filter(m -> this.isAcceptableMatch(m)).collect(Collectors.toList());
        if (!acceptable.isEmpty()) {
            if (acceptable.size() == 1)
                return acceptable.get(0);
            final Match<AlaLinnaeanClassification, MatchMeasurement> best = acceptable.get(0);
            final AlaLinnaeanClassification bc = best.getMatch();
            final TaxonomicStatus bts = bc.taxonomicStatus != null ? bc.taxonomicStatus : TaxonomicStatus.unknown;
            if (bts.isPlaced()) {
                // See if all the rest are misapplied/excluded or whatever
                if (acceptable.stream().allMatch(m -> m == best || m.getMatch().taxonomicStatus == null || !m.getMatch().taxonomicStatus.isPlaced()))
                    return best;
            }
            // See if all the rest have the same acceptedNameId as the best one
            if (bc.acceptedNameUsageId != null) {
                if (acceptable.stream().allMatch(m -> bc.acceptedNameUsageId.equals(m.getMatch().acceptedNameUsageId)))
                    return best;
            }
            // See if we have an accepted/synonym mix
            if (bts.isAcceptedFlag() && acceptable.stream().allMatch(m -> m == best || (m.getMatch().taxonomicStatus != null && m.getMatch().taxonomicStatus.isSynonymLike())))
                return best.boost(acceptable).with(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM);
            // See if the rest have the same name
            // First try actual name
            final String bestScientificName = best.getAccepted().scientificName;
            final Integer bestRankID = best.getAccepted().rankId;
            if (bestScientificName != null) {
                if (acceptable.stream().allMatch(m ->
                        m == best ||
                                this.hasMatchingValue(bestScientificName, m, AlaLinnaeanFactory.scientificName) &&
                                        this.hasMatchingValue(bestRankID, m, AlaLinnaeanFactory.rankId))
                )
                    return best.with(AlaLinnaeanFactory.MULTIPLE_MATCHES);
            }
            // Then try weak name matching
            final String bestSoundexScientificName = best.getAccepted().soundexScientificName;
            if (bestSoundexScientificName != null) {
                if (acceptable.stream().allMatch(m ->
                        m == best ||
                                this.hasMatchingValue(bestSoundexScientificName, m, AlaLinnaeanFactory.soundexScientificName) &&
                                        this.hasMatchingValue(bestRankID, m, AlaLinnaeanFactory.rankId))
                )
                    return best.with(Issues.of(AlaLinnaeanFactory.MULTIPLE_MATCHES, AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME));
            }
        }
        // Look for a common parent if all synonym-like objects
        if (results.stream().allMatch(r -> r.getMatch() != r.getAccepted())) {
            int limitRankID = this.limitRankID(classification);
            final Match<AlaLinnaeanClassification, MatchMeasurement> lub = this.lub(results);
            if (lub != null && lub.getAccepted().rankId != null && lub.getAccepted().rankId > limitRankID)
                return lub.with(AlaLinnaeanFactory.SYNTHETIC_MATCH);
        }
        // If all accepted and all the same name and nomenclatural code, then choose the most likely
        if (results.stream().allMatch(r -> r.getMatch().taxonomicStatus.isAcceptedFlag() && r.getCandidate().matchClean(tn, AlaLinnaeanFactory.scientificName))) {
            return results.get(0).with(AlaLinnaeanFactory.UNRESOLVED_HOMONYM);
        }
        // If all doubtful and they have the same name and nomenclaural code, then choose the most likely
        if (results.stream().allMatch(r -> r.getMatch().taxonomicStatus.isDoubtfulFlag() && r.getCandidate().matchClean(tn, AlaLinnaeanFactory.scientificName))) {
            return results.get(0).with(AlaLinnaeanFactory.MULTIPLE_MATCHES);
        }
        return null;
    }

    /**
     * Test for a matching value
     *
     * @param value The match value
     * @param match The potential match
     * @param observables The observables to match against
     *
     * @return True if positively a match
     */
    protected <T> boolean hasMatchingValue(T value, Match<AlaLinnaeanClassification, MatchMeasurement> match, Observable... observables) {
        try {
            Boolean same = match.getAcceptedCandidate().match(value, observables);
            return same != null && same;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Test for results with the same name but different nomenclatural codes.
     * <p>
     * We test against the accepted name.
     * If it's a synonym, then an accepted name should take precedence in terms of a match.
     * </p>
     *
     * @param results The usable results
     * @return An unresolved homonym match if there is one.
     */
    protected Match<AlaLinnaeanClassification, MatchMeasurement> detectUnresolvableHomonym(AlaLinnaeanClassification classification, List<Match<AlaLinnaeanClassification, MatchMeasurement>> results) {
        if (results.size() < 1)
            return null;
        final String scientificName = classification.scientificName;
        if (scientificName == null)
            return null;
        Set<NomenclaturalCode> codes = results.stream()
                .filter(m -> scientificName.equalsIgnoreCase(m.getAccepted().scientificName))
                .map(m -> m.getAccepted().nomenclaturalCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (codes.size() > 1)
            return (Match<AlaLinnaeanClassification, MatchMeasurement>) Match.invalidMatch().with(AlaLinnaeanFactory.UNRESOLVED_HOMONYM);
        return null;
    }

    /**
     * Find the limit rank ID.
     * <p>
     * This is the id of the next highest Linnaean rank that the classification will find.
     * At the momeent, this is limited to class, since if we can't even agree on class, then things are
     * pretty bad.
     * </p>
     * @param classification The template classification
     *
     * @return The id of the limit.
     */
    protected int limitRankID(AlaLinnaeanClassification classification) {
        return CLASS_RANK_ID;
    }

    /**
     * Get the sorting method for a list of matches
     *
     * @return The sort to use
     */
    @Override
    protected Comparator<Match<AlaLinnaeanClassification, MatchMeasurement>> getMatchSorter() {
        return MATCH_SORTER;
    }

    /**
     * Prepare a classification for matching.
     * <p>
     * If we do not have a kingdom or nomenclatural code, see if we can find one based on the other
     * evidence in the classification.
     * </p>
     *
     * @param classification The classification
     * @return The prepared classification
     * @throws BayesianException if unable to prepare the classification
     */
    @Override
    protected AlaLinnaeanClassification prepareForMatching(AlaLinnaeanClassification classification) throws BayesianException {
        this.estimateKingdom(classification);
        if (this.localityScope != null && !this.localityScope.isEmpty() && classification.locationId != null && !classification.locationId.isEmpty()) {
            if (!classification.locationId.stream().anyMatch(id -> this.localityScope.contains(id))) {
                classification = classification.clone();
                classification.locationId = null;
                classification.addIssue(AlaLinnaeanFactory.LOCATION_OUT_OF_SCOPE);
            }
        }
        return classification;
    }

    /**
     * If the classification does not have a kingdom, see if we can find one.
     * <p>
     * Work from the most speciific (genus) to the least (phylum)
     * </p>
     *
     * @param classification The classification
     */
    protected void estimateKingdom(AlaLinnaeanClassification classification) throws BayesianException {
        if (classification.kingdom != null && classification.soundexKingdom != null)
            return;
        if (this.findKingdom(classification.genus, Rank.GENUS, classification))
            return;
        if (this.findKingdom(classification.family, Rank.FAMILY, classification))
            return;
        if (this.findKingdom(classification.order, Rank.ORDER, classification))
            return;
        if (this.findKingdom(classification.class_, Rank.CLASS, classification))
            return;
        if (this.findKingdom(classification.phylum, Rank.PHYLUM, classification))
            return;
    }

    /**
     * See if we can find a kingdom for a particular higher-order name.
     *
     * @param scientificName The name of the taxon
     * @param taxonRank The taxon rank
     * @param classification The classification to modify
     *
     * @return True if a kingdom has been found
     */
    protected boolean findKingdom(String scientificName, Rank taxonRank, AlaLinnaeanClassification classification) throws BayesianException {
        if (scientificName == null || taxonRank == null)
            return false;
        AlaLinnaeanClassification match = this.kingdomCache.get(new KingdomKey(scientificName, taxonRank));
        if (match == null || match.kingdom == null)
            return false;
        classification.addHint(AlaLinnaeanFactory.kingdom, match.kingdom);
        return true;
    }

    /**
     * See if we can find a kingdom for a particular higher-order name.
     *
     * @param key The name of the taxon
     *
     * @return True if a kingdom has been found
     */
    protected AlaLinnaeanClassification doFindKingdom(KingdomKey key) {
        try {
            AlaLinnaeanClassification finder = new AlaLinnaeanClassification();
            finder.scientificName = key.getScientificName();
            finder.taxonRank = key.getTaxonRank();
            finder.inferForSearch(this.getAnalyser(), MatchOptions.NONE);
            Match<AlaLinnaeanClassification, MatchMeasurement> match = this.findSource(finder, MatchOptions.NONE, null, Optional.empty());
            if (match != null && match.isValid())
                return match.getAccepted();
            return null;
        } catch (BayesianException ex) {
            logger.error("Unable to search for kingdom key " + key, ex);
            return null;
        }
    }

    /**
     * Is this match a "miscellaneous" match?
     * <p>
     * Generally, this means something from miscellaneous literature.
     * If we have this and more accepted values, then we drop the extras.
     * </p>
     *
     * @param match The match to check
     *
     * @return True if the match is miscellaneous
     */
    protected boolean isMiscellaneous(Match<AlaLinnaeanClassification, MatchMeasurement> match) {
        return match.getMatch().taxonomicStatus == TaxonomicStatus.miscellaneousLiterature;
    }

    /**
     * Is this a possible match.
     * <p>
     * Include synonyms with a low threashold, so they can be included in various flags.
     * </p>
     *
     * @param classification The source classification
     * @param candidate      The candidate classifier
     * @param inference      The match probability
     * @return True if this is a possible match
     */
    @Override
    protected boolean isPossible(AlaLinnaeanClassification classification, Classifier candidate, Inference inference) {
        TaxonomicStatus taxonomicStatus = candidate.get(AlaLinnaeanFactory.taxonomicStatus);
        double threshold = taxonomicStatus.isSynonymLike() ? POSSIBLE_THRESHOLD / 10.0 : POSSIBLE_THRESHOLD;
        return inference.getPosterior() >= threshold && inference.getBoost() >= 1.0;

    }

    /**
     * Close the matcher
     *
     * @throws StoreException if unable to close for some reason.
     */
    @Override
    public void close() throws Exception {
        super.close();
        if (this.kingdomCache != null)
            this.kingdomCache.close();
    }

    /**
     * A holder for name/rank searches for possible kingdoms
     */
    @Value
    @EqualsAndHashCode
    public static class KingdomKey {
        private String scientificName;
        private Rank taxonRank;
    }
}
