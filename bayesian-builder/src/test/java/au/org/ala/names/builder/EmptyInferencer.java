package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.Trace;

/**
 * Empty inferencer for testing.
 */
public class EmptyInferencer implements Inferencer<EmptyClassification> {
    @Override
    public String getSignature() {
        return "";
    }

    @Override
    public Inference probability(EmptyClassification classification, Classifier classifier, Trace trace) {
        return Inference.forPEC(0.0, 0.0, 0.0);
    }
}
