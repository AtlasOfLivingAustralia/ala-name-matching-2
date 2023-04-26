package au.org.ala.bayesian;

import java.text.DecimalFormat;

public interface Inferencer<C extends Classification> {
    DecimalFormat PLAIN_FORMAT = new DecimalFormat("0.00");
    DecimalFormat EXPONENTIAL_FORMAT = new DecimalFormat("0.00E0");

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
     * @param trace Any trace required to record inference calculations. Null if not required
     *
     * @return The probability of a match
     *
     * @throws BayesianException if unable to calculate the probability
      */
    Inference probability(C classification, Classifier classifier, Trace trace) throws BayesianException;

    default String formatDouble(double v) {
        if (Double.isFinite(v)) {
            if (v > 0.01)
                return PLAIN_FORMAT.format(v);
            else
                return EXPONENTIAL_FORMAT.format(v);
        }
        return Double.toString(v);
    }
}
