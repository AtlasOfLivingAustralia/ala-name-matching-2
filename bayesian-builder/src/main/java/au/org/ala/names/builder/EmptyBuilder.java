package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;

import java.util.Deque;

/**
 * A null builder that does nothing in particular.
 */
public class EmptyBuilder implements Builder {
    /**
     * Default constructor
     */
    public EmptyBuilder() {
    }

    @Override
    public String getSignature() {
        return null;
    }

    /**
     * Null generator
     *
     * @param classifier The classifier
     */
    @Override
    public void generate(Classifier classifier) {
    }

    /**
     * Null inference
     *
     * @param classifier The classifier
     */
    @Override
    public void infer(Classifier classifier) {
    }

    /**
     * Null expansion
     *
     * @param classifier The classifier
     * @param parents  The classifiers parents
     */
    @Override
    public void expand(Classifier classifier, Deque<Classifier> parents) {
    }

    /**
     * Null signature.
     */
    @Override
    public String buildSignature(Classifier classifier) {
        return null;
    }

    /**
     * Null parameter calculation
     *
     * @param analyser   The parameter analyser
     * @param classifier   The classifier
     * @throws InferenceException if unable to calculate the parameters
     */
    @Override
    public Parameters calculate(ParameterAnalyser analyser, Classifier classifier) throws InferenceException {
        return new EmptyParameters();
    }

    public static class EmptyParameters implements Parameters {
        double prior;

        @Override
        public void load(double[] vector) {
            this.prior = vector[0];
        }

         @Override
        public double[] store() {
            return new double[] { this.prior };
        }
    }
}
