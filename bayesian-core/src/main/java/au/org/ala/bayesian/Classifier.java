package au.org.ala.bayesian;

import lombok.extern.slf4j.Slf4j;
import org.gbif.dwc.terms.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An object that holds information that would allow classification of a particular case.
 * <p>
 * Implementations can implement classifiers in various ways.
 * For example, for a lucene index a classifier is just a wrapper around a lucene document.
 * </p>
 * <p>
 * Classifiers <em>must</em> adhere to the principle that fields added in a specific order are
 * also retrived in the same order.
 * This is necessary so that {@link Classification}s can be filled out with the correct
 * information.
 * </p>
 */
public interface Classifier {
    /**
     * Get the value for an observable.
     *
     * @param observable The observable
     */
    public <T> T get(Observable observable);

    /**
     * Get the all values for some observables.
     *
     * @param observables The observable
     *
     * @return The a set of all present values
     */
    public <T> Set<T> getAll(Observable... observables);

    /**
     * Does this classifier contain any information about this observable?
     *
     * @param observable The observable to test
     *
     * @return True if there are values in the classifier
     */
    public boolean has(Observable observable);

    /**
     * Does this classifier have a matching term for an observable?
     * <p>
     * If the observable has any combination of a type, normaliser and style,
     * the match should respect the appropriate rules.
     * </p>
     *
     * @param observable The observable to match
     * @param value The value to match against (may be null)
     *
     * @return Null for nothing to match against (ie null value), or true for a match/false for a non-match
     *
     * @throws StoreException if there was a problem retrieving a value from the classifier
     * @throws InferenceException if there was a problem matching the result
     */
    public <T> Boolean match(Observable observable, T value) throws StoreException, InferenceException;

    /**
     * Add a value to the classifier.
     * <p>
     * Note, the classifier needs to parse and normalise any values before
     * adding them to the classifier.
     * A value should not be added twice.
     * </p>
     *
     * @param observable The observable to store
     * @param value The value to store
     *
     * @throws StoreException if unable to add this variable to the classifier
     * @throws InferenceException if unable to check to see if the value has been added twice
     */
    public <T> void add(Observable observable, T value) throws StoreException;


    /**
     * Copy all values from another classifier
     * <p>
     * Parsing and normalisation has assumed to have already taken place.
     * Values should not be added twice.
     * </p>
     *
     * @param observable The observable for the values
     * @param classifier The classifier to copy from
     *
     * @throws StoreException if unable to add this variable to the classifier
     */
    public void addAll(Observable observable, Classifier classifier) throws StoreException;

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
    public <T> void replace(Observable observable, T value) throws StoreException;

    /**
     * Get the identifier for a classifier.
     *
     * @return The identifier, or null for no identifier found.
     */
    public String getIdentifier();

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
    public String identify() throws StoreException;

    /**
     * Get the classifier's type
     *
     * @return The document type
     *
     * @throws StoreException If unable to create a type for some reason.
     */
    public Term getType() throws StoreException;

    /**
     * Set the classifier's type
     *
     * @param type The new type
     *
     * @throws StoreException If unable to create a type for some reason.
     */
    public void setType(Term type) throws StoreException;


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
    public boolean hasAnnotation(Term annotation) throws StoreException;

    /**
     * Add an annotation to a classifier.
     *
     * @param annotation The annotation to add
     *
     * @throws StoreException If unable to annotate for some reason.
     */
    public void annotate(Term annotation) throws StoreException;

    /**
     * Load the inference parameters in a classifier
     *
     * @throws StoreException if unable to retrieve the parameters
     */
    public void loadParameters(Parameters parameters) throws StoreException;

    /**
     * Store the inference parameters in a classifier.
     *
     * @param parameters The inferred parameters
     *
     * @throws StoreException if unable to store the parameters in the document
     */
    public void storeParameters(Parameters parameters) throws StoreException;


    /**
     * Get the index values for the classifier.
     *
     * @return An array of [left, right] index integers
     *
     * @throws StoreException if unable to retrieve the index
     */
    public int[] getIndex() throws StoreException;

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
    public void setIndex(int left, int right) throws StoreException;

    /**
     * Get the names for the classifier.
     * <p>
     * The names contains the primary list of names that will identify this classifier
     * </p>
     *
     * @return The names list
     *
     * @throws StoreException if unable to get the names list
     */
    public Collection<String> getNames() throws StoreException;

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
    public void setNames(Collection<String> names) throws StoreException;

    /**
     * Get the signature of the classifier, indicating which erasure groups are in and which are out.
     *
     * @return The signature.
     *
     * @see Inferencer#getSignature()
     */
    public String getSignature();

    /**
     * Set the signature for tha classifier.
     *
     * @param signature The classifier signature
     *
     * @see Inferencer#getSignature()
     */
    public void setSignature(String signature);

    /**
     * Get a list of all the values set in the classifier.
     * <p>
     * This can be used to dump the classifer during debugging.
     * </p>
     *
     * @return The values in the form of key -> value pairs, with the keys a useful internal representation
     */
    public Collection<String[]> getAllValues();

}
