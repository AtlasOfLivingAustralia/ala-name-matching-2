package au.org.ala.bayesian;

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;

import java.util.List;

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
