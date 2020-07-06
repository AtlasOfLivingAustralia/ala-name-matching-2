package au.org.ala.bayesian;

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;

/**
 * A classification match.
 */
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

    /**
     * Get the matching classification
     *
     * @return The classification
     */
    public C getMatch() {
        return match;
    }

    /**
     * Get the probability of the match
     *
     * @return The match probability
     */
    public double getProbability() {
        return probability;
    }
}
