package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;

/**
 * Empty inferencer for testing.
 */
public class EmptyInferencer implements Inferencer<EmptyClassification, EmptyParameters> {
    @Override
    public Inference probability(EmptyClassification classification, Classifier classifier, EmptyParameters parameters) {
        return Inference.forPEC(0.0, 0.0, 0.0);
    }
}
