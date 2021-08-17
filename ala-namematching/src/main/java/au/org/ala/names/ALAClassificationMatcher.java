package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.api.vocabulary.NomenclaturalCode;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ALAClassificationMatcher extends ClassificationMatcher<AlaLinnaeanClassification, AlaLinnaeanInferencer, AlaLinnaeanFactory> {
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
     * @throws StoreException     if there is a problem retrieving information
     * @throws InferenceException if there is a problem making inferences about the data
     *
     * @return the annotated and otherwise modified list
     */
    @Override
    protected List<Match<AlaLinnaeanClassification>> annotate(List<Match<AlaLinnaeanClassification>> results) throws StoreException, InferenceException {
        List<Match<AlaLinnaeanClassification>> results1 = super.annotate(results);
        return results1.stream()
            .map(m -> this.detectMisapplied(m, results1))
            .map(m -> this.detectPartialMisapplied(m, results1))
            .map(m -> this.detectHomonym(m, results1))
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
     * Mark any accepted/synonym results which also have misapplied values.
     *
     * @param results The list of candidate matches
     */
    protected Match<AlaLinnaeanClassification> detectPartialMisapplied(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        if (m.taxonomicStatus != null && m.scientificName != null && m.taxonomicStatus.isPlaced()) {
            for (Match<AlaLinnaeanClassification> match2: results) {
                AlaLinnaeanClassification m2 = match2.getMatch();
                if (match2 != match && m.scientificName.equalsIgnoreCase(m2.scientificName) && m.taxonomicStatus != null && m2.taxonomicStatus.isMisappliedFlag())
                    return match.with(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME);
             }
        }
        return match;
    }


    /**
     * Mark any accepted/synonym results which also have misapplied values.
     *
     * @param results The list of candidate matches
     */
    protected Match<AlaLinnaeanClassification> detectHomonym(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        String referenceName = m.scientificName;
        NomenclaturalCode referenceCode = m.nomenclaturalCode;
        if (m.taxonomicStatus != null && referenceName != null && referenceCode != null && m.taxonomicStatus.isPlaced()) {
            for (Match<AlaLinnaeanClassification> match2: results) {
                AlaLinnaeanClassification m2 = match2.getMatch();
                if (match2 != match && referenceName.equalsIgnoreCase(m2.scientificName) && referenceCode != m.nomenclaturalCode && m2.taxonomicStatus != null && m2.taxonomicStatus.isPlaced())
                    return match.with(AlaLinnaeanFactory.UNRESOLVED_HOMONYM);
            }
        }
        return match;
    }


    /**
     * Mark any accepted/synonym results which also have misapplied values.
     *
     * @param results The list of candidate matches
     */
    protected Match<AlaLinnaeanClassification> detectAcceptedSynonymPair(final Match<AlaLinnaeanClassification> match, final List<Match<AlaLinnaeanClassification>> results) {
        AlaLinnaeanClassification m = match.getMatch();
        String referenceName = m.scientificName;
        NomenclaturalCode referenceCode = m.nomenclaturalCode;
        if (m.taxonomicStatus != null && referenceName != null && referenceCode != null && m.taxonomicStatus.isPlaced()) {
            for (Match<AlaLinnaeanClassification> match2: results) {
                AlaLinnaeanClassification m2 = match2.getMatch();
                if (match2 != match && referenceName.equalsIgnoreCase(m2.scientificName) && referenceCode != m.nomenclaturalCode && m2.taxonomicStatus != null && m2.taxonomicStatus.isPlaced())
                    return match.with(AlaLinnaeanFactory.UNRESOLVED_HOMONYM);
            }
        }
        return match;
    }

    /**
     * Find a single match that is acceptable.
     *
     * @param results The list of results
     * @return A single acceptable result, or null
     */
    @Override
    protected Match<AlaLinnaeanClassification> findSingle(List<Match<AlaLinnaeanClassification>> results) {
       if (results.isEmpty())
           return null;
       if (results.size() == 1 && !this.isBadEvidence(results.get(0)))
           return results.get(0);
        List<Match<AlaLinnaeanClassification>> acceptable = results.stream().filter(m -> this.isAcceptableMatch(m) && !this.isBadEvidence(m)).collect(Collectors.toList());
        if (acceptable.isEmpty())
            return null;
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
        // See if we have a single accepted/variety of synonyms
        if (bts.isAcceptedFlag() && acceptable.stream().allMatch(m -> m == best || (m.getMatch().taxonomicStatus != null && m.getMatch().taxonomicStatus.isSynonymLike())))
            return best.with(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM);
        // See if the rest have the same name
        final String soundexScientificName = bc.soundexScientificName;
        if (soundexScientificName != null) {
            if (acceptable.stream().allMatch(m -> m == best || soundexScientificName.equals(m.getMatch().soundexScientificName)))
                return best;
        }
        return null;
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
}
