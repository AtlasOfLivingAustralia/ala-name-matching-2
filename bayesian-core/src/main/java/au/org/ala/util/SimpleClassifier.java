package au.org.ala.util;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import org.gbif.dwc.terms.Term;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A simple classifier structured as a dictionary with a few special case elements.
 *
 * Generally useful when converting classifications into classifiers.
 */
public class SimpleClassifier implements Classifier {
    private String identifier;
    private String signature;
    private Term type;
    private Set<Term> annotations;
    private double[] parameters;
    private int left;
    private int right;
    private Set<String> names;
    private Map<Observable, Object> values;
    private List<String> trail;

    public SimpleClassifier() {
        this.annotations = new HashSet<>();
        this.names = new HashSet<>();
        this.values = new HashMap<>();
    }

    /**
     * Get the value for an observable.
     *
     * @param observable The observable
     * @return The associated value or null for not present
     */
    @Override
    public <T> T get(Observable observable) {
        return (T) this.values.get(observable);
    }

    /**
     * Get the all values for observables.
     *
     * @param observables The observables
     * @return The a set of all present values
     */
    @Override
    public <T> LinkedHashSet<T> getAll(Observable... observables) {
        LinkedHashSet<T> values = new LinkedHashSet<>(observables.length);
        for (Observable observable: observables) {
            values.add(this.get(observable));
        }
        return values;
    }

    /**
     * Does this classifier contain any information about this observable?
     *
     * @param observable The observable to test
     * @return True if there are values in the classifier
     */
    @Override
    public boolean has(Observable observable) {
        return this.values.containsKey(observable);
    }

    /**
     * Does this classifier have a matching term for an observable?
     * <p>
     * If the observable has any combination of a type, normaliser and style,
     * the match should respect the appropriate rules.
     * </p>
     *
     * @param observables The observables to match
     * @param value      The value to match against (may be null)
     * @return Null for nothing to match against (ie null value), or true for a match/false for a non-match
     * @throws StoreException if there was a problem matching the result
     */
    @Override
    public <T> Boolean match(T value, Observable... observables) throws StoreException {
        try {
            for (Observable observable: observables) {
                Object val = this.values.get(observable);
                Boolean match = observable.getAnalysis().equivalent(val, value);
                if (match != null)
                    return match;
            }
            return null;
        } catch (InferenceException ex) {
            throw new StoreException(ex);
        }
    }

    /**
     * Add a value to the classifier.
     * <p>
     * Only one value is permitted for a simple classifier
     * </p>
     *
     * @param observable The observable to store
     * @param value      The value to store
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public <T> void add(Observable observable, T value) throws StoreException {
        if (this.values.containsKey(observable))
            throw new StoreException("Observable " + observable + " already has a value");
        Normaliser normaliser = observable.getNormaliser();
        if (normaliser != null && value != null && value instanceof String)
            value = (T) normaliser.normalise((String) value);
        if (value == null)
            this.values.remove(observable);
        else
            this.values.put(observable, value);
    }

    /**
     * Copy all values from another classifier
     * <p>
     * Parsing and normalisation has assumed to have already taken place.
     * </p>
     *
     * @param observable The observable for the values
     * @param classifier The classifier to copy from
     *
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public void addAll(Observable observable, Classifier classifier) throws StoreException {
        if (!(classifier instanceof SimpleClassifier))
            throw new IllegalArgumentException("Expecting a simple classifier");
        SimpleClassifier sc = (SimpleClassifier) classifier;
        if (sc.values.containsKey(observable))
            this.values.put(observable, sc.values.get(observable));
    }

    /**
     * Set a value in the classifier.
     * <p>
     * Replaces any existing values
     * </p>
     *
     * @param observable The observable to store
     * @param value      The value to store
     * @throws StoreException if unable to add this variable to the classifier
     */
    @Override
    public <T> void replace(Observable observable, T value) throws StoreException {
        Normaliser normaliser = observable.getNormaliser();
        if (normaliser != null && value != null && value instanceof String)
            value = (T) normaliser.normalise((String) value);
        if (value == null)
            this.values.remove(observable);
        else
            this.values.put(observable, value);
    }

    /**
     * Get the identifier for a classifier.
     *
     * @return The identifier, or null for no identifier found.
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Label a classifier with a unique identifier.
     * <p>
     * Add the identifier to the document.
     * If the classifier is already identified, then return that identifier.
     * </p>
     *
     * @return The identifier.
     * @throws StoreException If unable to create an identifier for some reason.
     */
    @Override
    public String identify() throws StoreException {
        if (this.identifier == null)
            this.identifier = UUID.randomUUID().toString();
        return this.identifier;
    }

    /**
     * Get the classifier's type
     *
     * @return The document type
     * @throws StoreException If unable to create a type for some reason.
     */
    @Override
    public Term getType() throws StoreException {
        return this.type;
    }

    /**
     * Set the classifier's type
     *
     * @param type The new type
     * @throws StoreException If unable to create a type for some reason.
     */
    @Override
    public void setType(Term type) throws StoreException {
        this.type = type;
    }

    /**
     * Is this document annotated with a specific annotation (flag)?
     * <p>
     * By default, the URI of the annotation is searched for.
     * </p>
     *
     * @param annotation The annotation to test for
     * @return True if the document has the annotation, false otherwise.
     * @throws StoreException if unable to test for the annotation
     */
    @Override
    public boolean hasAnnotation(Term annotation) throws StoreException {
        return this.annotations.contains(annotation);
    }

    /**
     * Add an annotation to a classifier.
     *
     * @param annotation The annotation to add
     * @throws StoreException If unable to annotate for some reason.
     */
    @Override
    public void annotate(Term annotation) throws StoreException {
        this.annotations.add(annotation);
    }

    /**
     * Load the inference parameters in a classifier
     *
     * @param parameters
     * @throws StoreException if unable to retrieve the parameters
     */
    @Override
    public void loadParameters(Parameters parameters) throws StoreException {
        parameters.load(this.parameters);
    }

    /**
     * Store the inference parameters in a classifier.
     *
     * @param parameters The inferred parameters
     * @throws StoreException if unable to store the parameters in the document
     */
    @Override
    public void storeParameters(Parameters parameters) throws StoreException {
        this.parameters = parameters.store();
    }

    /**
     * Get the index values for the classifier.
     *
     * @return An array of [left, right] index integers
     * @throws StoreException if unable to retrieve the index
     */
    @Override
    public int[] getIndex() throws StoreException {
        return new int[] { this.left, this.right };
    }

    /**
     * Set the index values for a classifier.
     * <p>
     * Index values place the classifier within a tree heirarchy
     * </p>
     *
     * @param left  The left-most index value
     * @param right The right-most index value
     * @throws StoreException If unable to store the index
     */
    @Override
    public void setIndex(int left, int right) throws StoreException {
        this.left = left;
        this.right = right;
    }

    /**
     * Get the names for the classifier.
     * <p>
     * The names contains the primary list of names that will identify this classifier
     * </p>
     *
     * @return The names list
     * @throws StoreException if unable to get the names list
     */
    @Override
    public Collection<String> getNames() throws StoreException {
        return this.names;
    }

    /**
     * Set the names for a classifier.
     * <p>
     * The names contain the searchable list of names the classifier might be found under
     * </p>
     *
     * @param names The names for this classifier
     * @throws StoreException if unable to store the names in the classifier
     */
    @Override
    public void setNames(Collection<String> names) throws StoreException {
        this.names = new HashSet<>(names);
    }

    /**
     * Get the signature of the classifier, indicating which erasure groups are in and which are out.
     *
     * @return The signature.
     * @see Inferencer#getSignature()
     */
    @Override
    public String getSignature() {
        return this.signature;
    }

    /**
     * Set the signature for tha classifier.
     *
     * @param signature The classifier signature
     * @see Inferencer#getSignature()
     */
    @Override
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * Get the trail of identifiers used in hierarchical classifiers.
     * <p>
     * The trail represents the heirarchy of possible identifiers, from most general to least general (this classifier).
     * </p>
     *
     * @return The trail
     */
    @Override
    public List<String> getTrail() {
        return this.trail;
    }

    /**
     * Set the trail of identifiers used in the classifier hierarchy.
     *
     * @param trail The new trail
     */
    @Override
    public void setTrail(List<String> trail) {
        this.trail = trail;
    }

    /**
     * Get a list of all the values set in the classifier.
     * <p>
     * This can be used to dump the classifer during debugging.
     * </p>
     *
     * @return The values in the form of key -> value pairs, with the keys a useful internal representation
     */
    @Override
    public Collection<String[]> getAllValues() {
        return this.values.entrySet().stream().map(e -> new String[] { e.getKey().getId(), e.getValue().toString() }).collect(Collectors.toList());
    }
}
