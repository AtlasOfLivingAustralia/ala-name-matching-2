package au.org.ala.names.builder;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import org.apache.lucene.document.Document;

import java.util.Deque;

/**
 * A null builder that does nothing in particular.
 */
public class EmptyBuilder extends Builder<EmptyBuilder.EmptyParameters> {
    /**
     * Default constructor
     */
    public EmptyBuilder() {
    }

    /**
     * Null inference
     *
     * @param document The document
     */
    @Override
    public void infer(Document document) {
    }

    /**
     * Null expansion
     *
     * @param document The document
     * @param parents  The documents parents
     */
    @Override
    public void expand(Document document, Deque<Document> parents) {
    }

    /**
     * Create an empty parameter set to be filled.
     *
     * @return A new parameter instance
     */
    @Override
    public EmptyParameters createParameters() {
        return new EmptyParameters();
    }

    /**
     * Null parameter calculation
     *
     * @param parameters The parameters to fill out
     * @param analyser   The parameter analyser
     * @param document   The document
     * @throws InferenceException if unable to calculate the parameters
     */
    @Override
    public void calculate(EmptyParameters parameters, ParameterAnalyser<Document> analyser, Document document) throws InferenceException {
    }

    public static class EmptyParameters extends Parameters {
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
