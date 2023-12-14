package au.org.ala.names.builder;

import au.org.ala.bayesian.*;

import java.util.Deque;
import java.util.function.Function;

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
    public void generate(Classifier classifier, Analyser analyser) {
    }

    /**
     * Null interpretation
     *
     * @param classifier The classifier
     */
    @Override
    public void interpret(Classifier classifier, Analyser analyser) {
    }

    /**
     * Null inference
     *
     * @param classifier The classifier
     */
    @Override
    public void infer(Classifier classifier, Analyser analyser) {
    }

    /**
     * Null expansion
     *  @param classifier The classifier
     * @param parents  The classifiers parents
     */
    @Override
    public void expand(Classifier classifier, Deque parents, Analyser analyser) {
    }

    /**
     * Null boadener
     *
     * @param document The document to be broadened
     * @param analyser An analyster
     *
     * @return
     * @throws BayesianException
     */
    @Override
    public Function<Classifier, Boolean> getBroadener(Classifier document, Analyser analyser) throws BayesianException {
        return null;
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
