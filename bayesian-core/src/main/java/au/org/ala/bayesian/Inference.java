package au.org.ala.bayesian;

import lombok.Value;

/**
 * An inference based on the bayseian formula.
 */
@Value
public class Inference {
    /** The minimum probability we can get to. This is defined so that #MAXIMUM_PROBABILITY does not evaluate to 1 */
    public static final double MINIMUM_PROBABILITY = 1.0e-9;
    /** The maximum probability we can get to, 1 - #MINIMUM_PROABILITY This must be (just) less than 1. */
    public static final double MAXIMUM_PROBABILITY = 1.0 - MINIMUM_PROBABILITY;

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
        return this.evidence > MINIMUM_PROBABILITY ? this.conditional / this.evidence : 0.0;
    }

    /**
     * Calculate the disjunction of both probabilties.
     * <p>
     * Essentially p = 1 - (1 - p1) * (1 - p2) but we need to get the other bits in line, too.
     * The prior should be the or of the two priors.
     * The evidence should be roughtly equal, since it's expected to be a probability based
     * on the same evidence, so take the maximum.
     * The conditional follows from the other three.
     * </p>
     *
     * @param other The other probability
     *
     * @return A probability where the posterior is the or of the two posteriors.
     */
    public Inference or(Inference other) {
        double p = this.posterior + other.posterior - this.posterior * other.posterior;
        double h = this.prior + other.prior - this.prior * other.prior;
        double e = Math.max(this.evidence, other.evidence);
        double c = h < MINIMUM_PROBABILITY ? 0.0 : p * e / h;
        return new Inference(h, e, c, p);
    }

    /**
     * Calculate the conjunction of both probabilties.
     * <p>
     * Start with p = p1 * p2.
     * The prior should be the and of the two priors.
     * The evidence should be roughtly equal, since it's expected to be a probability based
     * on the same evidence, so take the maximum.
     * The conditional follows from the other three.
     * </p>
     * @param other
     * @return
     */
    public Inference and(Inference other) {
        double p = this.posterior * other.posterior;
        double h = this.prior > MINIMUM_PROBABILITY && other.prior > MINIMUM_PROBABILITY ? Math.max(this.prior * other.prior, MINIMUM_PROBABILITY) : 0.0;
        double e = Math.max(this.evidence, other.evidence);
        double c = h < MINIMUM_PROBABILITY ? 0.0 : p * e / h;
        while (c > 1.0 && h < 0.1) {
            c /= 10.0;
            h *= 10.0;
        }
        while (c > 1.0 && e > MINIMUM_PROBABILITY * 10.0) {
            c /= 10.0;
            e /= 10.0;
        }
        return new Inference(h, e, c, p);
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
        double posterior = evidence < MINIMUM_PROBABILITY ? 0.0 : prior * conditional / evidence;
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
        double posterior = evidence < MINIMUM_PROBABILITY ? 0.0 : hypothesis / evidence;
        double conditional = prior < MINIMUM_PROBABILITY ? 0.0 : hypothesis / prior;
        return new Inference(prior, evidence, conditional, posterior);
    }
}
