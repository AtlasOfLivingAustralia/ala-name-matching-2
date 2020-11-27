package au.org.ala.bayesian;

import lombok.Value;

/**
 * An inference based on the bayseian formula.
 */
@Value
public class Inference {
    /** The prior probability p(H) */
    private double prior;
    /** The probability of the evidence p(E) */
    private double evidence;
    /** The probability of the evidence given the hypothesis p(E | H) */
    private double conditional;
    /** The posterior probability p(H | E) = p(H)p(E | H)/p(E) */
    private double posterior;

    /** Internal constructor */
    private Inference(double prior, double evidence, double conditional, double posterior) {
        this.prior = prior;
        this.evidence = evidence;
        this.conditional = conditional;
        this.posterior = posterior;
    }

    /**
     * Get the boost that this evidence gives.
     * <p>
     * The boost is p(E | H) / p(E). If it's greater than one,
     * then the supplied evidence increases the probability of p(H).
     * </p>
     *
     * @return The boost
     */
    public double getBoost() {
        return this.evidence > 0 ? this.conditional / this.evidence : 0.0;
    }

    /**
     * Construct for prior, evidence and conditional probabilities.
     * <p>
     * This corresponds to the normal way of constructing Bayes theorem
     * p(H | E) = p(H) p(E | H) / p(E)
     * </p>
     *
     * @param prior The probability of the hypothesis p(H)
     * @param evidence The probability of the evidence p(E)
     * @param conditional The probability of the evidence given the prior p(E | H)
     *
     * @return An inference with the correct values pre-calculated
     */
    public static Inference forPEC(double prior, double evidence, double conditional) {
        double posterior = evidence == 0.0 ? 0.0 : prior * conditional / evidence;
        return new Inference(prior, evidence, conditional, posterior);
    }

    /**
     * Construct for prior, evidence and posterior probabilities.
     * <p>
     * This corresponds to constructing Bayes theorem out of
     * positive and negative probabilities
     * p(H | E) = p(H) p(E | H) / (p(E | H) p(H) + p(E |!H) p(!H))
     * where you end up with a value of p(E) and p(E | H) p(H)
     * </p>
     *
     * @param prior The probability of the prior p(H)
     * @param evidence The probability of the total evidence p(E)
     * @param hypothesis The probability of the evidence and hypothesis p(H | E) p(H)
     *
     * @return An inference with the correct values pre-calculated
     */
    public static Inference forPEH(double prior, double evidence, double hypothesis) {
        double posterior = evidence == 0.0 ? 0.0 : hypothesis / evidence;
        double conditional = prior == 0.0 ? 0.0 : hypothesis / prior;
        return new Inference(prior, evidence, conditional, posterior);
    }
}
