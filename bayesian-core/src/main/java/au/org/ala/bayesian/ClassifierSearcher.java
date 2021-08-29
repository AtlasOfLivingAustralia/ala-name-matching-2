package au.org.ala.bayesian;

import org.gbif.dwc.terms.Term;

import java.util.List;
import java.util.Optional;

/**
 * A searcher for classifiers that fit a classification.
 * <p>
 * The searcher looks for names and provides a selection of candidates.
 * </p>
 *
 * @param <C> The type of classification used to search
 */
abstract public class ClassifierSearcher<C extends Classifier> {
    /**
     * Search for a classifier by identifier.
     *
     * @param type The type of classifier to get
     * @param identifier The identifier observable
     * @param id The id to search for
     *
     * @return An matching classifier or null for not found
     *
     * @throws InferenceException if unable to infer information about the classifier
     * @throws StoreException if unable to retrieve the classifier
     */
    abstract public C get(Term type, Observable identifier, Object id) throws InferenceException, StoreException;

    /**
     * Search for a set of possible candidate classifiers that match the supplied classification.
     *
     * @param classification The classification
     *
     * @return A list of potential classifiers
     *
     * @throws InferenceException if unable to correctly match the classifiers
     * @throws StoreException if unable to retrieve the results
     */
    abstract public List<C> search(Classification classification) throws InferenceException, StoreException;

    /**
     * Close the searcher.
     *
     * @throws StoreException if unable to close for some reason
     */
    abstract public void close() throws StoreException;
}
