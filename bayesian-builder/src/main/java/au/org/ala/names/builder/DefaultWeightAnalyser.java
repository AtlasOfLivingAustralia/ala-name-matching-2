package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;

/**
 * A default weight analyser that retrns a weight of 1 as a default.
 */
public class DefaultWeightAnalyser implements WeightAnalyser {
    /**
     * Compute the base weight of the classifier.
     *
     * @param classifier The classuifier to weight
     * @return A default of 1.0
     */
    @Override
    public double weight(Classifier classifier) {
        return 1.0;
    }

    /**
     * Modify the base weight in the classifier
     *
     * @param classifier The classuifier
     * @param weight     The base weight
     * @return The base weight
     */
    @Override
    public double modify(Classifier classifier, double weight) {
        return weight;
    }
}
