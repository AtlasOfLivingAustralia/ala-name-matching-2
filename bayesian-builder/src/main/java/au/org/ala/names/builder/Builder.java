package au.org.ala.names.builder;

import au.org.ala.bayesian.*;

import java.util.Deque;

/**
 * A class that can be used to derive all the additional, inferred
 * information from an inference network.
 * <p>
 * Subclasses are generated based on the network configuration.
 * </p>
 */
public interface Builder<P extends Parameters> {
    /**
     * Infer from a classifier during building.
     * <p>
     * This method creates all the derived values from the incoming classifier
     * and the stack of parent documents.
     * </p>
     *
     * @param classifier The document
     *
     * @throws InferenceException if unable to calculate the inference
     * @throws StoreException if unable to retrieve inference data
     */
    public void infer(Classifier classifier) throws InferenceException, StoreException;

    /**
     * Expand a classifier during building.
     * <p>
     * This method inserts new values from the classifier and the stack of parent classifiers
     * </p>
     *
     * @param document The classifier
     * @param parents The classifier's parents
     *
     * @throws InferenceException if unable to calculate the expansion
     * @throws StoreException if unable to retrieve expansion data
    */
    public void expand(Classifier document, Deque<Classifier> parents) throws InferenceException, StoreException;

    /**
     * Calculate parameter values for a particular document.
     *
     * @param parameters The parameters to fill out
     * @param analyser The parameter analyser
     * @param document The document
     *
     * @throws InferenceException if unable to calculate the parameters
     * @throws StoreException if unable to retrieve parameter data
     */
    public void calculate(P parameters, ParameterAnalyser analyser, Classifier document) throws InferenceException, StoreException;
}
