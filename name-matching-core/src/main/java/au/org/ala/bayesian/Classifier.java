package au.org.ala.bayesian;

import org.gbif.dwc.terms.Term;

import java.util.Collection;
import java.util.Set;

/**
 * An object that holds information that would allow classification of a particular case.
 * <p>
 * Implementations can implement classifiers in various ways.
 * For example, for a lucene index a classifier is just a wrapper around a lucene document.
 * </p>
 */
abstract public class Classifier<C extends Classifier> {
    /**
     * Construct an empty classifier.
     */
    protected Classifier() {
    }

    /**
     * Get the value for an observable.
     *
     * @param observable The observable
     *
     * @return The associated value or null for not present
     */
    abstract public String get(Observable observable);

    /**
     * Get the all values for an observable.
     *
     * @param observable The observable
     *
     * @return The a set of all present values
     */
    abstract public Set<String> getAll(Observable observable);

    /**
     * Does this classifier contain any information about this observable?
     *
     * @param observable The observable to test
     *
     * @return True if there are values in the classifier
     */
    abstract public boolean has(Observable observable);

    /**
     * Add a value to the classifier.
     *
     * @param observable The observable to store
     * @param value The value to store
     *
     * @throws StoreException if unable to add this variable to the classifier
     */
    abstract public void add(Observable observable, Object value) throws StoreException;


    /**
     * Copy all values from another classifier
     *
     * @param observable The observable for the values
     * @param classifier The classifier to copy from
     *
     * @throws StoreException if unable to add this variable to the classifier
     */
    abstract public void addAll(Observable observable, C classifier) throws StoreException;

    /**
     * Set a value in the classifier.
     * <p>
     * Replaces any existing values
     * </p>
     *
     * @param observable The observable to store
     * @param value The value to store
     *
     * @throws StoreException if unable to add this variable to the classifier
     */
    abstract public void replace(Observable observable, Object value) throws StoreException;

    /**
     * Get the identifier for a classifier.
     *
     * @return The identifier, or null for no identifier found.
     */
    abstract public String getIdentifier();

    /**
     * Label a classifier with a unique identifier.
     * <p>
     * Add the identifier to the document.
     * If the classifier is already identified, then return that identifier.
     * </p>
     *
     * @return The identifier.
     *
     * @throws StoreException If unable to create an identifier for some reason.
     */
    abstract public String identify() throws StoreException;

    /**
     * Get the classifier's type
     *
     * @return The document type
     *
     * @throws StoreException If unable to create a type for some reason.
     */
    abstract public Term getType() throws StoreException;

    /**
     * Set the classifier's type
     *
     * @param type The new type
     *
     * @return The document type
     *
     * @throws StoreException If unable to create a type for some reason.
     */
    abstract public void setType(Term type) throws StoreException;


    /**
     * Turn a term into a storable string for annotation.
     * <p>
     * By default, this is the qualified name of the term
     * </p>
     * @param term The term
     *
     * @return The annotation value
     */
    public static String getAnnotationValue(Term term) {
        return term.qualifiedName();
    }

    /**
     * Is this document annotated with a specific annotation (flag)?
     * <p>
     * By default, the URI of the annotation is searched for.
     * </p>
     *
     * @param annotation The annotation to test for
     *
     * @return True if the document has the annotation, false otherwise.
     *
     * @throws StoreException if unable to test for the annotation
     */
    abstract public boolean hasAnnotation(Term annotation) throws StoreException;

    /**
     * Add an annotation to a classifier.
     *
     * @param annotation The annotation to add
     *
     * @throws StoreException If unable to annotate for some reason.
     */
    abstract public void annotate(Term annotation) throws StoreException;

    /**
     * Load the inference parameters in a classifier
     *
     * @return The parameters
     *
     * @throws StoreException if unable to retrieve the parameters
     */
    abstract public void loadParameters(Parameters parameters) throws StoreException;

    /**
     * Store the inference parameters in a classifier.
     *
     * @param parameters The inferred parameters
     *
     * @throws StoreException if unable to store the parameters in the document
     */
    abstract public void storeParameters(Parameters parameters) throws StoreException;


    /**
     * Get the index values for the classifier.
     *
     * @return An array of [left, right] index integers
     *
     * @throws StoreException if unable to retrieve the index
     */
    abstract public int[] getIndex() throws StoreException;
    /**
     * Set the index values for a classifier.
     * <p>
     * Index values place the classifier within a tree heirarchy
     * </p>
     *
     * @param left The left-most index value
     * @param right The right-most index value
     *
     * @throws StoreException If unable to store the index
     */
    abstract public void setIndex(int left, int right) throws StoreException;

    /**
     * Get the names for the classifier.
     * <p>
     * The names contains the primary list of names that will identify this classifier
     * </p>
     *
     * @return The names list
     *
     * @throws StoreException
     */
    abstract public Collection<String> getNames() throws StoreException;

    /**
     * Set the names for a classifier.
     * <p>
     * The names contain the searchable list of names the classifier might be found under
     * </p>
     *
     * @param names The names for this classifier
     *
     * @throws StoreException if unable to store the names in the classifier
     */
    abstract public void setNames(Collection<String> names) throws StoreException;
}
