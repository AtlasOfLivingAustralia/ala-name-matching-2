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
 *
 */
public interface Analyser<C extends Classification> {
    /**
     * Analyse the information in a classifier and extend the classifier
     * as required.
     *
     * @param classifier The classifier
     * @param issues A store of issues associated with analysis and matching
     *
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException if an error occurs updating the classifier
     */
    public void analyse(Classifier classifier, Issues issues) throws InferenceException, StoreException;

    /**
     * Analyse the information in a classification and extend the classification
     * as required.
     * <p>
     * A default version simply converts the classification into a classifier, analyses
     * that and returns the result. One
     * </p>
     *
     * @param classification The classification
     * @param issues A store of issues associated with analysis and matching
     *
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException if an error occurs updating the classifier
     */
    public void analyse(C classification, Issues issues) throws InferenceException, StoreException;
}
