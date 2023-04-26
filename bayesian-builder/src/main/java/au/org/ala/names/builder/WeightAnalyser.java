package au.org.ala.names.builder;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classifier;

/**
 * Analyse weights for classifiers.
 * <p>
 * The general behaviour pattern used by weight analysers is to first compute a base weight, if one is not specified,
 * and then modify that base weight according to the characteristics of the classifier.
 * </p>
 */
public interface WeightAnalyser {
    /**
     * Compute the base weight of the classifier.
     * <p>
     * This computes the default weight from information in the classifier.
     * If the classifier already contains a weight, this method should not be called.
     * </p>
     *
     * @param classifier The classuifier to weight
     *
     * @return The base weight (must be at least 1.0)
     *
     * @throws BayesianException if unable to compute the weight
     */
    double weight(Classifier classifier) throws BayesianException;

    /**
     * Modify the base weight in the classifier
     *
     * @param classifier The classuifier
     * @param weight The base weight
     *
     * @return The modified weight (must be at least 1.0)
     *
     * @throws BayesianException if unable to compute the weight
     */
    double modify(Classifier classifier, double weight) throws BayesianException;
}
