package au.org.ala.bayesian;

import java.util.Set;

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

    /**
     * Build a collection of base names for the classification.
     * <p>
     * If a classification can be referred to in multiple ways, this method
     * builds the various ways of referring to the classification.
     * </p>
     *
     * @param classifier The classification
     *
     * @return All the names that refer to the classification
     *
     * @throws InferenceException if unable to analyuse the names
     * @throws StoreException if unable to modifiy the classifier
     */
    public Set<String> analyseNames(Classifier classifier) throws InferenceException, StoreException;
}
