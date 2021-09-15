package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.api.vocabulary.NomenclaturalCode;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ALAVernacularClassificationMatcher extends ClassificationMatcher<AlaVernacularClassification, AlaVernacularInferencer, AlaVernacularFactory> {
    /** The default immediately acceptable threshold for something to be regarded as accepted. @see #isImmediateMatch */
    public static double IMMEDIATE_THRESHOLD = 0.90;
    /** The default acceptable threshold for something. @see #isAcceptableMatch */
    public static double ACCEPTABLE_THRESHOLD = 0.70;

    /**
     * Create with a searcher and inferencer.
     *
     * @param factory  The factory for creating objects for the matcher to work on
     * @param searcher The mechanism for getting candidiates
     */
    public ALAVernacularClassificationMatcher(AlaVernacularFactory factory, ClassifierSearcher<?> searcher) {
        super(factory, searcher);
    }

    /**
     * Find a single match that is acceptable.
     *
     * @param results The list of results
     * @return A single acceptable result, or null
     */
    @Override
    protected Match<AlaVernacularClassification> findSingle(List<Match<AlaVernacularClassification>> results) {
        if (results.isEmpty())
            return null;
        if (results.size() == 1 && !this.isBadEvidence(results.get(0)))
            return results.get(0);
        List<Match<AlaVernacularClassification>> usable = results.stream().filter(m -> !this.isBadEvidence(m)).collect(Collectors.toList());
        if (usable.isEmpty())
            return null;
        if (usable.size() == 1)
            return usable.get(0);
        final Match<AlaVernacularClassification> best = usable.get(0);
        final AlaVernacularClassification bc = best.getMatch();
        // Check to see if we have a number that are all pointing to the same thing
        final String bid = bc.taxonId;
        final List<Match<AlaVernacularClassification>> matching = usable.stream().filter(m -> m != best && bid.equals(m.getMatch().taxonId)).collect(Collectors.toList());
        if (!matching.isEmpty()) {
            Match<AlaVernacularClassification> better = best.boost(matching);
            if (this.isAcceptableMatch(better))
                return better;
        }
        return null;
    }

    /**
     * Since we're working off vernacular names, be relaxed
     *
     * @param match The potential match
     *
     * @return True if this is a possible match
     */
    protected boolean isImmediateMatch(Match<AlaVernacularClassification> match) {
        Inference p = match.getProbability();
        return p.getPosterior() >= IMMEDIATE_THRESHOLD && p.getConditional() >= IMMEDIATE_THRESHOLD;
    }

    /**
     * Since we're working off vernacular names, be relaxed
     *
     * @param match The candidate match
     *
     * @return True if this is an acceptable match
     */
    protected boolean isAcceptableMatch(Match<AlaVernacularClassification> match) {
        Inference p = match.getProbability();
        return p.getPosterior() >= ACCEPTABLE_THRESHOLD && p.getEvidence() >= EVIDENCE_THRESHOLD && p.getConditional() >= POSSIBLE_THRESHOLD;
    }

}
