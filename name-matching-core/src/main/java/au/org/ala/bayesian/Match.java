package au.org.ala.bayesian;

import lombok.Value;

/**
 * A classification match.
 */
@Value
public class Match<C extends Classification> {
    private C match;
    private Classifier candidate;
    private double probability;

    /**
     * Construct for a match and a probability
     *
     * @param match The match
     * @param candidate The candidate classifier
     * @param probability The match probability
     */
    public Match(C match, Classifier candidate, double probability) {
        this.match = match;
        this.candidate = candidate;
        this.probability = probability;
    }
}
