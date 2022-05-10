package au.org.ala.bayesian;

public interface Inferencer<C extends Classification> {
    /**
     * Get the signature of the inference, indicating which erasure groups are in and which are out.
     * <p>
     * The returned signature is a string "TFTF..." indicating whether an erasure group has data (T) or
     * is absent (F).
     * If the inference has no signature (it's a top-level inferencer for all possible signatures) it returns null.
     * </p>
     *
     * @return The signature.
     */
    String getSignature();

    /**
     * Calculate the probability of a match between a classification and a classifier.
     *
     * @param classification The classification
     * @param classifier The classifier to match
     *
     * @return The probability of a match
     *
     * @throws BayesianException if unable to calculate the probability
      */
    Inference probability(C classification, Classifier classifier) throws BayesianException;
}
