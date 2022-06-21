package au.org.ala.bayesian;

import org.gbif.dwc.terms.Term;

import java.util.*;
import java.util.stream.Collectors;

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
     *
     * @param <T> The type of value returned
     *
     * @return The retrieved value or the default if not found
     */
    <T> T get(Observable<T> observable);

    /**
     * Get a vavlue with a default if not found.
     *
     * @param observable The observable
     * @param dflt The default value
     *
     * @param <T> The type of value returned
     *
     * @return The retrieved value or the default if not found
     */
    default <T> T getOrDefault(Observable<T> observable, T dflt) {
        T result = this.get(observable);
        return result == null ? dflt : result;
    }

    /**
     * Get all the possible values, including variants, for some observables.
     * <p>
     * The preferred name <em>must</em> be the first element that appears in iteration.
     * This is essential to allow copy derivations to use the correct reference value.
     * </p>
     *
     * @param observables The observable
     *
     * @return The a set of all present values
     */
    <T> LinkedHashSet<T> getAll(Observable<T>... observables);

    /**
     * Does this classifier contain any (non-variant) information about this observable?
     *
     * @param observable The observable to test
     *
     * @return True if there are (not variant only) values in the classifier
     */
    boolean has(Observable observable);

    /**
     * Does this classifier contain any (variant or non-variant) information about this observable?
     *
     * @param observable The observable to test
     *
     * @return True if there are (not variant only) values in the classifier
     */
    boolean hasAny(Observable observable);

    /**
     * Does this classifier have a matching term for an observable?
     * <p>
     * If the observable has variants, also match the variants.
     * </p>
     * <p>
     * If the observable has any combination of a type, normaliser and style,
     * the match should respect the appropriate rules.
     * </p>
     *
     * @param observables The possible observables to match against
     * @param value The value to match against (may be null)
     *
     * @return Null for nothing to match against (ie null value), or true for a match/false for a non-match
     *
     * @throws StoreException if there was a problem retrieving a value from the classifier or matching the result
     */
    <T> Boolean match(T value, Observable<T>... observables) throws StoreException;


    /**
     * Match definitively.
     * <p>
     * Exceptions and null results are treated as false.
     * </p>
     *
     * @param observables The possible observables to match against
     * @param value The value to match against (may be null)
     *
     * @return True for a match/false for a non-match
     *
     * @see #match(Object, Observable...)
     */
    default <T> boolean matchClean(T value, Observable<T>... observables) {
        try {
            Boolean match = this.match(value, observables);
            if (match == null)
                return false;
            return match.booleanValue();
        } catch (StoreException ex) {
            return false;
        }
    }

    /**
     * Add a value to the classifier.
     * <p>
     * The first value added represents the "actual" value of a classifier and
     * are intended to fill out a classification as well as being searchable/matchable.
     * Other values are treated as variants.
     * If the observable does not allow variants, a {@link StoreException} is thrown.
     * </p>
     * <p>
     * Note, the classifier needs to parse and normalise any values before
     * adding them to the classifier.
     * A value should not be added twice.
     * </p>
     *
     * @param observable The observable to store
     * @param value The value to store
     * @param variant Load this value as a variant only
     * @param replace Replace the non-variant value with this value
     *
     * @throws StoreException if the value is already present or unable to add this variable to the classifier
     */
    <T> void add(Observable<T> observable, T value, boolean variant, boolean replace) throws StoreException;

    /**
     * Copy all values from another classifier
     * <p>
     * Parsing and normalisation has assumed to have already taken place.
     * Values should not be added twice.
     * Things which are variants should stay as variants; only explicitly non-varant values should be
     * added as non-variants.
     * </p>
     *
     * @param observable The observable for the values
     * @param classifier The classifier to copy from
     *
     * @throws StoreException if unable to add this variable to the classifier
     */
    <T> void addAll(Observable<T> observable, Classifier classifier) throws StoreException;

    /**
     * Clear any values in the classifier
     * <p>
     * Removes any existing values from the classifier
     * </p>
     *
     * @param observable The observable to remove
     *
     * @throws StoreException if unable to add this variable to the classifier
     */
    void clear(Observable observable) throws StoreException;

    /**
     * Get the identifier for a classifier.
     *
     * @return The identifier, or null for no identifier found.
     */
    String getIdentifier();

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
    String identify() throws StoreException;

    /**
     * Get the classifier's type
     *
     * @return The document type
     *
     * @throws StoreException If unable to create a type for some reason.
     */
    Term getType() throws StoreException;

    /**
     * Set the classifier's type
     *
     * @param type The new type
     *
     * @throws StoreException If unable to create a type for some reason.
     */
    void setType(Term type) throws StoreException;


    /**
     * Turn a term into a storable string for annotation.
     * <p>
     * By default, this is the qualified name of the term
     * </p>
     * @param term The term
     *
     * @return The annotation value
     */
    static String getAnnotationValue(Term term) {
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
    boolean hasAnnotation(Term annotation) throws StoreException;

    /**
     * Add an annotation to a classifier.
     *
     * @param annotation The annotation to add
     *
     * @throws StoreException If unable to annotate for some reason.
     */
    void annotate(Term annotation) throws StoreException;

    /**
     * Get the parameters associated with this classifier
     *
     * @return A pre-loaded set of parameters or null for not loaded
     *
     * @see #loadParameters(Parameters)
     */
    Parameters getCachedParameters();

    /**
     * Load the inference parameters in a classifier.
     * <p>
     * This loads the parameters and, if they can be accessed via {@link #getCachedParameters()}
     * </p>
     *
     * @throws StoreException if unable to retrieve the parameters
     */
    void loadParameters(Parameters parameters) throws StoreException;

    /**
     * Store the inference parameters in a classifier.
     *
     * @param parameters The inferred parameters
     *
     * @throws StoreException if unable to store the parameters in the document
     */
    void storeParameters(Parameters parameters) throws StoreException;


    /**
     * Get the index values for the classifier.
     *
     * @return An array of [left, right] index integers
     *
     * @throws StoreException if unable to retrieve the index
     */
    int[] getIndex() throws StoreException;

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
    void setIndex(int left, int right) throws StoreException;

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
    Collection<String> getNames() throws StoreException;

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
    void setNames(Collection<String> names) throws StoreException;

    /**
     * Get the signature of the classifier, indicating which erasure groups are in and which are out.
     *
     * @return The signature.
     *
     * @see Inferencer#getSignature()
     */
    String getSignature();

    /**
     * Set the signature for tha classifier.
     *
     * @param signature The classifier signature
     *
     * @see Inferencer#getSignature()
     */
    void setSignature(String signature);

    /**
     * Get the trail of identifiers used in hierarchical classifiers.
     * <p>
     * The trail represents the heirarchy of possible identifiers, from most general to least general (this classifier).
     * </p>
     *
     * @return The trail
     */
    List<String> getTrail();

    /**
     * Set the trail of identifiers used in the classifier hierarchy.
     *
     * @param trail The new trail
     */
    void setTrail(List<String> trail);

    /**
     * Get a list of all the values set in the classifier.
     * <p>
     * This can be used to dump the classifer during debugging.
     * </p>
     *
     * @return The values in the form of key -> value pairs, with the keys a useful internal representation
     */
    Collection<String[]> getAllValues();

    /**
     * Get a summary of the classifier for use when recording traces and the like.
     *
     * @param factory The network factory to produce the summary for.
     *
     * @return A summary map for the classifier
     */
    default String getLabel(NetworkFactory<?, ?, ?> factory) {
        return factory.getIdentifier().map(i -> this.get(i)).filter(Objects::nonNull).orElse(this.getIdentifier());
    }

    /**
     * Get a summary of the classifier for use when recording traces and the like.
     *
     * @param factory The network factory to produce the summary for.
     *
     * @return A summary map for the classifier
     */
    default Map<String, Object> getSummaryDescription(NetworkFactory<?, ?, ?> factory) {
        Map<String, Object> result = new LinkedHashMap<>();
        String id = factory.getIdentifier().map(i -> this.get(i)).filter(Objects::nonNull).orElse(this.getIdentifier());
        String name = factory.getName().map(n -> this.get(n)).orElse(null);
        String type = null;
        try {
            type = this.getType().prefixedName();
        } catch (StoreException ex) {
        }
        result.put("type", type);
        result.put("id", id);
        result.put("name", name);
        return result;
    }

    /**
     * Get a full description of the classifier for use when recording traces and the like.
     *
     * @param factory The network factory to produce the summary for.
     *
     * @return A full map for the classifier
     */
    default Map<String, Object> getFullDescription(NetworkFactory<?, ?, ?> factory) {
        Map<String, Object> result = this.getSummaryDescription(factory);
        for (Observable<?> observable: factory.getObservables()) {
            List<String> values = this.getAll(observable).stream()
                    .filter(Objects::nonNull)
                    .map(v -> v.toString())
                    .collect(Collectors.toList());
            if (!values.isEmpty())
                result.put(observable.getId(), values.size() == 1 ? values.get(0) : values);
        }
        return result;
    }

}
