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
public interface Builder {
    /**
     * Get the erasure signature for this builder.
     *
     * @return The erasure signature, null for an erasure-insentitive, top-level builder.
     *
     * @see Inferencer#getSignature()
     */
    public String getSignature();


    /**
     * Generate for a classifier during building.
     * <p>
     * This method generates any derivations that imply a once-off generation
     * of something.
     * </p>
     *
     * @param classifier The document
     *
     * @throws BayesianException if unable to calculate the inference
      */
    public void generate(Classifier classifier) throws BayesianException;

    /**
     * Infer from a classifier during building.
     * <p>
     * This method creates all the derived values from the incoming classifier
     * and the stack of parent documents.
     * </p>
     *
     * @param classifier The document
     *
     * @throws BayesianException if unable to calculate the inference
      */
    public void infer(Classifier classifier) throws BayesianException;

    /**
     * Expand a classifier during building.
     * <p>
     * This method inserts new values from the classifier and the stack of parent classifiers
     * </p>
     *
     * @param document The classifier
     * @param parents The classifier's parents
     *
     * @throws BayesianException if unable to calculate the expansion
    */
    public void expand(Classifier document, Deque<Classifier> parents) throws BayesianException;

    /**
     * Build a signture for a classifier.
     * This gives the inference required by the type of information available in the classifier.
     *
     * @param classifier The classifier to build a signature for
     *
     * @return The classifier signature
     *
     * @see Inferencer#getSignature()
     */
    public String buildSignature(Classifier classifier);

    /**
     * Calculate parameter values for a particular document.
     *
     * @param analyser The parameter analyser
     * @param document The document
     *
     * @return The parameters for this document
     *
     * @throws BayesianException if unable to calculate the parameters
     */
    public Parameters calculate(ParameterAnalyser analyser, Classifier document) throws BayesianException;
}
