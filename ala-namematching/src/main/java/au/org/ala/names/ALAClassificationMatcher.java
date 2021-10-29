package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.Rank;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ALAClassificationMatcher extends ClassificationMatcher<AlaLinnaeanClassification, AlaLinnaeanInferencer, AlaLinnaeanFactory> {
    private static final int SPECIES_RANK_ID = RankIDAnalysis.idFromRank(Rank.SPECIES);
    private static final int GENUS_RANK_ID = RankIDAnalysis.idFromRank(Rank.GENUS);
    private static final int FAMILY_RANK_ID = RankIDAnalysis.idFromRank(Rank.FAMILY);
    private static final int ORDER_RANK_ID = RankIDAnalysis.idFromRank(Rank.ORDER);
    private static final int CLASS_RANK_ID = RankIDAnalysis.idFromRank(Rank.CLASS);
    private static final int PHYLUM_RANK_ID = RankIDAnalysis.idFromRank(Rank.PHYLUM);
    private static final int KINGDOM_RANK_ID = RankIDAnalysis.idFromRank(Rank.KINGDOM);

    /** First four characters for valid checks */
    private static final int MIN_VALID_LENGTH = 4;

    private static final Comparator<Match<AlaLinnaeanClassification>> MATCH_SORTER = new Comparator<Match<AlaLinnaeanClassification>>() {
        private static final double DISTANCE = 0.05;

        @Override
        public int compare(Match<AlaLinnaeanClassification> o1, Match<AlaLinnaeanClassification> o2) {
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
            return Double.compare(p2, p1);
        }
    };

    /**
     * Create with a searcher and inferencer.
     *
     * @param factory  The factory for creating objects for the matcher to work on
     * @param searcher The mechanism for getting candidiates
     */
    public ALAClassificationMatcher(AlaLinnaeanFactory factory, ClassifierSearcher<?> searcher) {
        super(factory, searcher);
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
    protected List<Match<AlaLinnaeanClassification>> annotate(List<Match<AlaLinnaeanClassification>> results) throws BayesianException {
        List<Match<AlaLinnaeanClassification>> results1 = super.annotate(results);
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
    protected Match<AlaLinnaeanClassification> detectMisapplied(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
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
    protected Match<AlaLinnaeanClassification> detectExcluded(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
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
    protected Match<AlaLinnaeanClassification> detectPartialMisapplied(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        if (m.taxonomicStatus != null && m.scientificName != null && m.taxonomicStatus.isPlaced()) {
            for (Match<AlaLinnaeanClassification> match2 : results) {
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
    protected Match<AlaLinnaeanClassification> detectPartialExcluded(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        if (m.taxonomicStatus != null && m.scientificName != null && m.taxonomicStatus.isPlaced()) {
            for (Match<AlaLinnaeanClassification> match2 : results) {
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
    protected Match<AlaLinnaeanClassification> detectParentChildSynonym(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
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
    protected Match<AlaLinnaeanClassification> findSingle(AlaLinnaeanClassification classification, List<Match<AlaLinnaeanClassification>> results) throws BayesianException {
        results = results.stream().filter(m -> !this.isBadEvidence(m)).collect(Collectors.toList());
        if (results.isEmpty())
            return null;
        if (results.size() == 1)
            return results.get(0);
        // See if we have an unresolvable homonym
        Match<AlaLinnaeanClassification> unresolvedHomonym = this.detectUnresolvableHomonym(classification, results);
        if (unresolvedHomonym != null)
            return unresolvedHomonym;
        final Match<AlaLinnaeanClassification> trial = results.get(0);
        final AlaLinnaeanClassification tc = trial.getMatch();
        final String tn = tc.scientificName;
        final TaxonomicStatus tts = tc.taxonomicStatus != null ? tc.taxonomicStatus : TaxonomicStatus.unknown;
        // See if we have a single accepted/variety of synonyms
        if (tts.isAcceptedFlag() && results.stream().allMatch(m -> m == trial || (tn.equalsIgnoreCase(m.getMatch().scientificName) && m.getMatch().taxonomicStatus != null && m.getMatch().taxonomicStatus.isSynonymLike())))
            return trial.boost(results).with(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM);
        // See if we have collection of misapplied results
        if (tts.isMisappliedFlag() && results.stream().allMatch(m -> m == trial || (tn.equalsIgnoreCase(m.getMatch().scientificName) && m.getMatch().taxonomicStatus != null && m.getMatch().taxonomicStatus.isMisappliedFlag())))
            return trial.boost(results).with(AlaLinnaeanFactory.MISAPPLIED_NAME);
        List<Match<AlaLinnaeanClassification>> acceptable = results.stream().filter(m -> this.isAcceptableMatch(m)).collect(Collectors.toList());
        if (!acceptable.isEmpty()) {
            if (acceptable.size() == 1)
                return acceptable.get(0);
            final Match<AlaLinnaeanClassification> best = acceptable.get(0);
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
        // Look for a common parent if all synonyms
        if (results.stream().allMatch(r -> r.getMatch().taxonomicStatus.isSynonymLike() && r.getMatch() != r.getAccepted())) {
            int limitRankID = this.limitRankID(classification);
            Match<AlaLinnaeanClassification> lub = this.lub(results);
            if (lub != null && lub.getAccepted().rankId != null && lub.getAccepted().rankId > limitRankID)
                return lub.with(AlaLinnaeanFactory.SYNTHETIC_MATCH);
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
    protected <T> boolean hasMatchingValue(T value, Match<AlaLinnaeanClassification> match, Observable... observables) {
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
    protected Match<AlaLinnaeanClassification> detectUnresolvableHomonym(AlaLinnaeanClassification classification, List<Match<AlaLinnaeanClassification>> results) {
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
            return (Match<AlaLinnaeanClassification>) Match.invalidMatch().with(AlaLinnaeanFactory.UNRESOLVED_HOMONYM);
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
    protected Comparator<Match<AlaLinnaeanClassification>> getMatchSorter() {
        return MATCH_SORTER;
    }

    /**
     * Check to see if this has the faintest chance of being a match before doing more onerous matching.
     *
     * @param classification The template classification
     * @param candidate The candidate classifier
     *
     * @return True if the first four alphabetic characters in the soundex name match.
     *
     * @throws StoreException if unable to retrieve the soundex
     */
    @Override
    protected boolean isValidCandidate(AlaLinnaeanClassification classification, Classifier candidate) throws StoreException {
        if (classification.soundexScientificName == null)
            return false;
        final String soundexScientificName = classification.soundexScientificName;
        for (Object possible: candidate.getAll(AlaLinnaeanFactory.soundexScientificName)) {
            if (soundexScientificName.regionMatches(0, possible.toString(), 0, MIN_VALID_LENGTH))
                return true;
        }
        return false;
    }
}
