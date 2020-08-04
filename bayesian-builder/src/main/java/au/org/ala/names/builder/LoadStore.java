package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import org.gbif.dwc.terms.Term;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A store of loaded but un-structured name data.
 *
 * @param <C> The type of classifier to use with this store
 */
abstract public class LoadStore<C extends Classifier> {
    /** The network this store is for */
    protected Annotator annotator;

    /**
     * Create a store for a network.
     *
     * @param annotator The store annotator
     *
     * @throws StoreException if unable to create the store
     */
    public LoadStore(Annotator annotator) throws StoreException {
        this.annotator = annotator;
    }

    /**
     * Get the observation that allows us to find annotations.
     *
     * @return The annotation observable
     */
    abstract public Observation getAnnotationObservation(Term annotation);

    /**
     * Create a new, empty classifier
     *
     * @return The new classifier
     */
    abstract public C newClassifier();

    /**
     * Store an entry in the store
     *
     * @param classifier The classifier
     * @param type The document type
     *
     * @throws InferenceException if unable to add annotations
     * @throws StoreException if unable to store the entry
     */
    abstract public void store(C classifier, Term type) throws InferenceException, StoreException;

    /**
     * Update an existing entry in the store
     *
     * @param classifier The data classifier
     *
     * @throws StoreException if unable to store the entry
     */
    abstract public void update(C classifier) throws StoreException;

    /**
     * Get a parameter analyser for this store.
     *
     * @param network The underlying network
     * @param weight The weight observable
     * @param defaultWeight The default weight
     *
     * @return The parameter analyser
     */
    abstract public ParameterAnalyser getParameterAnalyser(Network network, Observable weight, double defaultWeight) throws InferenceException, StoreException;

    /**
     * Get a store entry by unique name/value.
     *
     * @param type The type of entry to get
     * @param observable The observable to compare
     * @param value The value the term has. If null, then choose entries with no value.
     *
     * @return A matching classifier for this entry
     *
     * @throws StoreException if there is an error with the underlying store
     */
    abstract public C get(Term type, Observable observable, String value) throws StoreException;

    /**
     * Get a stream of all terms that match a particular value
     *
     * @param type The type of entry to get
     * @param values The list of conditions that must be met
     *
     * @return The matching values in an iterable form.
     *
     * @throws StoreException if unable to read the store
     */
    abstract public Iterable<C> getAll(Term type, Observation... values) throws StoreException;

    /**
     * Get a list of all classifiers that match a particular value
     *
     * @param type The type of entry to get
     * @param values The list of conditions that must be met
     *
     * @return The matching values as a collection
     *
     * @throws StoreException if unable to read the store
     */
    public List<C> getAllClassifiers(Term type, Observation... values) throws StoreException {
        return StreamSupport.stream(this.getAll(type, values).spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Commit any currently pending stores.
     *
     * @throws StoreException If unable to commit.
     */
    abstract public void commit() throws StoreException;

    /**
     * Close the store
     */
    abstract public void close() throws StoreException;
}
