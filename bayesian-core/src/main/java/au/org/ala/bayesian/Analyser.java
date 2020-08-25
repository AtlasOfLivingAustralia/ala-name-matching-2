package au.org.ala.bayesian;

/**
 * Provides application-specific analysis and extension of evidence supplied.
 * <p>
 * Subclasses can be used to fill out inferred evidence that doesn't
 * fit nicely into a network.
 * For example, it can be used to work out implied rank from a scientific name,
 * if one has not been specified.
 * </p>
 *
 */
public interface Analyser<C extends Classification> {
    /**
     * Analyse the information in a classification and extend the classification
     * as required.
     *
     * @param classification The classification
     *
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException if an error occurs updating the classifier
     */
    public void analyse(C classification) throws InferenceException, StoreException;
}
