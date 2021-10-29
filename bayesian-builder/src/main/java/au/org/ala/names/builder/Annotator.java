package au.org.ala.names.builder;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classifier;

/**
 * An annotator annotates a document with additional information
 * derived from the chanracteristics of the document.
 */
public interface Annotator {
    /**
     * A generic, null annotator.
     */
    public static Annotator NULL = new Annotator() {
        @Override
        public void annotate(Classifier classifier) throws BayesianException {
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
     * @throws BayesianException If unable to analyse this classifier properly
      */
    public void annotate(Classifier classifier) throws BayesianException;
    ;
}
