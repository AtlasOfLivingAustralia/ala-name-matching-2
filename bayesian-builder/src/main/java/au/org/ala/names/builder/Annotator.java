package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;

/**
 * An annotator annotaters a document with additional information
 * derived from
 */
public interface Annotator {
    /**
     * A generic, null annotator.
     */
    public static Annotator NULL = new Annotator() {
        @Override
        public void annotate(Classifier classifier) throws InferenceException, StoreException {
        }
    };

    /**
     * Annotate a classifier with additional information.
     * <p>
     * This does not include the type or identifier, which are added separately
     * but includes any terms that should be added to the annotationas list.
     * </p>
     *
     * @param classifier The classifier
     *
     * @throws InferenceException If unable to analyse this classifier properly
     * @throws StoreException If unable to create an annotation for some reason.
     */
    public void annotate(Classifier classifier) throws InferenceException, StoreException;
}
