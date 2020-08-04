package au.org.ala.bayesian;

/**
 * Provides application-specific analysis and extension of evidence supplied.
 * <p>
 * Subclasses can be used to fill out inferred evidence that doesn't
 * fit nicely into a network.
 * For example, it can be used to work out implied rank from a scientific name,
 * if one has not been specified.
 * </p>
 * <p>
 * Note that the classifier and classification analysis should be identical.
 * You can do a quick'n'dirty analysis by using {@link Classification#translate(Classifier)}
 * and {@link Classification#populate(Classifier, boolean)} to ensure consistency.
 * However, there may be quite a performance hit.
 * </p>
 */
public interface EvidenceAnalyser<C extends Classification> {
    /**
     * Analyse the information in a classifier and extend the classifier
     * as required.
     *
     * @param classifier The classifier
     *
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException if an error occurs updating the classifier
     */
    public void analyse(Classifier classifier) throws InferenceException, StoreException;

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
