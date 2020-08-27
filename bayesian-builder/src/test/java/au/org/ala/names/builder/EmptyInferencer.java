package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inferencer;

/**
 * Empty inferencer for testing.
 */
public class EmptyInferencer implements Inferencer<EmptyClassification, EmptyParameters> {
    @Override
    public double probability(EmptyClassification classification, Classifier classifier, EmptyParameters parameters) {
        return 0.0;
    }
}
