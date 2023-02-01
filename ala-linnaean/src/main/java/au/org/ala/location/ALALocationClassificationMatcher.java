package au.org.ala.location;

import au.org.ala.bayesian.*;

import java.util.List;
import java.util.stream.Collectors;

public class ALALocationClassificationMatcher extends ClassificationMatcher<AlaLocationClassification, AlaLocationInferencer, AlaLocationFactory, MatchMeasurement> {
    /**
     * Create with a searcher and inferencer.
     *
     * @param factory  The factory for creating objects for the matcher to work on
     * @param searcher The mechanism for getting candidiates
     * @param config The classificatio configuration
     */
    public ALALocationClassificationMatcher(AlaLocationFactory factory, ClassifierSearcher<?> searcher, ClassificationMatcherConfiguration config) {
        super(factory, searcher, config);
    }

    /**
     * Find a single match that is acceptable.
     *
     * @param results The list of results
     * @return A single acceptable result, or null
     */
    @Override
    protected Match<AlaLocationClassification, MatchMeasurement> findSingle(final AlaLocationClassification classification, final List<Match<AlaLocationClassification, MatchMeasurement>> results, MatchMeasurement measurement) {
        if (results.isEmpty())
            return null;
        if (results.size() == 1 && !this.isBadEvidence(results.get(0)))
            return results.get(0);
        List<Match<AlaLocationClassification, MatchMeasurement>> usable = results.stream().filter(m -> !this.isBadEvidence(m)).collect(Collectors.toList());
        if (usable.isEmpty())
            return null;
        return usable.get(0);
    }

    /**
     * Is this a possible match.
     * <p>
     * We'll accept anything that vaguely looks right according to the soundex match, if there is one.
     * </p>
     *
     * @param classification The source classification
     * @param candidate The candidate classifier
     * @param inference The match probability
     *
     * @return True if this is a possible match
     */
    @Override
    protected boolean isPossible(AlaLocationClassification classification, Classifier candidate, Inference inference) {
        try {
            if (classification.soundexLocality != null)
                return candidate.match(classification.soundexLocality, AlaLocationFactory.soundexLocality);
            else
                return super.isPossible(classification, candidate, inference);
        } catch (Exception ignored) {
            return false;
        }
    }
}
