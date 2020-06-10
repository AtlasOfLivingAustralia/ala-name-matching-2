package au.org.ala.names.builder;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import org.apache.lucene.document.Document;

import java.util.Deque;

/**
 * A class that can be used to derive all the additional, inferred
 * information from an inference network.
 * <p>
 * Subclasses are generated based on the network configuration.
 * </p>
 */
abstract public class Builder<P extends Parameters> {
    /**
     * Infer from a document during building.
     * <p>
     * This method creates all the derived values from the incoming document
     * and the stack of parent documents.
     * </p>
     *
     * @param document The document
     */
    abstract public void infer(Document document);

    /**
     * Expand a document during building.
     * <p>
     * This method inserts new values from the document and the stack of parent documents
     * </p>
     *
     * @param document The document
     * @param parents The documents parents
     */
    abstract public void expand(Document document, Deque<Document> parents);

    /**
     * Create an empty parameter set to be filled.
     *
     * @return A new parameter instance
     */
    abstract public P createParameters();

    /**
     * Calculate parameter values for a particular document.
     *
     * @param parameters The parameters to fill out
     * @param analyser The parameter analyser
     * @param document The document
     *
     * @throws InferenceException if unable to calculate the parameters
     */
    abstract public void calculate(P parameters, ParameterAnalyser<Document> analyser, Document document) throws InferenceException;
}
