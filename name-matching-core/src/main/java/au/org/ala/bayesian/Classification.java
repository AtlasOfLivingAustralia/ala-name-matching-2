package au.org.ala.bayesian;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.Collection;

/**
 * The base class for classifications.
 * <p>
 * Classifications hold the vector of evidence that make up a individual piece of data.
 * </p>
 */
abstract public class Classification {
    /**
     * Get the type of the classification
     *
     * @return The sort of thing this classification is supposed to match.
     */
    abstract public Term getType();

    /**
     * Get a list of observations that match this classification.
     * <p>
     * This can be used to query an underlying name matcher for candidiate matches.
     * </p>
     *
     * @return This classification as a list of observations
     */
    abstract public Collection<Observation> toObservations();

    /**
     * Infer empty elements of the classification from the network definition.
     * <p>
     * This method is usually generated to implement any derivations that are
     * specified by {@link Observable#getDerivation()}.
     * The method can be used to perform common derivations without requiring further coding.
     * </p>
     * @throws InferenceException
     */
    abstract public void infer() throws InferenceException;

    /**
     *
     * Populate this classification from a classifier.
     * <p>
     * This allows a classification to be built that matches the classifier.
     * </p>
     * @param classifier The clkassifier that contains the original data
     * @param overwrite Overwrite what is already in the classification
     *
     * @throws InferenceException
     */
    abstract public void populate(Classifier classifier, boolean overwrite) throws InferenceException;
}
