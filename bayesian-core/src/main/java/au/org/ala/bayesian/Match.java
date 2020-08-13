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
    private Issues issues;

    /**
     * Construct for a match and a probability
     *
     * @param match The match
     * @param candidate The candidate classifier
     * @param probability The match probability
     * @param issues The issues list
     */
    public Match(C match, Classifier candidate, double probability, Issues issues) {
        this.match = match;
        this.candidate = candidate;
        this.probability = probability;
        this.issues = issues;
    }
}
