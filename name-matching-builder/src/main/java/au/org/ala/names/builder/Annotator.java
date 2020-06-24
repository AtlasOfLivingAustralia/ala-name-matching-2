package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Parameters;
import au.org.ala.bayesian.StoreException;
import org.apache.lucene.document.*;
import org.apache.lucene.util.BytesRef;
import org.gbif.dwc.terms.Term;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

/**
 * An annotator annotaters a document with additional information
 * derived from
 */
public interface Annotator {

    /**
     * Annotate a classifier with additional information.
     * <p>
     * This does not include the type or identifier, which are added separately
     * but includes any terms that should be added to the annotationas list.
     * </p>
     *
     * @param classifier The classifier
     *
     * @throws StoreException If unable to create an annotation for some reason.
     */
    public void annotate(Classifier classifier) throws StoreException;
}
