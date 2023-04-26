package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.util.Metadata;
import lombok.Getter;
import lombok.Setter;
import org.gbif.dwc.terms.Term;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A store of loaded but un-structured name data.
 *
 * @param <C> The type of classifier to use with this store
 */
abstract public class LoadStore<C extends Classifier> implements LoadStoreMXBean {
    /** The default cache size */
    private static int DEFAULT_CACHE_SIZE = 2000000;

    /** The name of the store */
    @Getter
    private final String name;
    /** The cache size of the store */
    @Getter
    private final int cacheSize;
    /** The number of classifiers that have been created */
    private final AtomicInteger created = new AtomicInteger();
    /** The number of classifiers that have been written to the store */
    private final AtomicInteger written = new AtomicInteger();
    /** The number of new classifiers that have been added to the store */
    private final AtomicInteger added = new AtomicInteger();
    /** The number of classifiers that have been updated */
    private final AtomicInteger updated = new AtomicInteger();
    /** The number of gets from the store */
    private final AtomicInteger gets = new AtomicInteger();
    /** The number of queries from the store */
    private final AtomicInteger queries = new AtomicInteger();

    /**
     * Create a store for a network.
     *
     * @param name The store name
     * @param cacheSize The cache size (0 for default)
     *
     * @throws StoreException if unable to create the store
     */
    public LoadStore(String name, int cacheSize) throws StoreException {
        this.name = name;
        this.cacheSize = cacheSize > 0 ? cacheSize : DEFAULT_CACHE_SIZE;
    }

    /**
     * Get the number of classifiers that have been created.
     *
     * @return The created count
     */
    @Override
    public int getCreated() {
        return this.created.get();
    }

    /**
     * Get the number of classifiers that have been written to the store.
     *
     * @return The written count
     */
    @Override
    public int getWritten() {
        return this.written.get();
    }

    /**
     * Get the number of classifiers that have been added as new elements to the store.
     *
     * @return The added count
     */
    @Override
    public int getAdded() {
        return this.added.get();
    }

    /**
     * Get the number of classifiers that have been updated in the store.
     *
     * @return The updated count
     */
    @Override
    public int getUpdated() {
        return this.updated.get();
    }

    /**
     * Get the number of classifiers that have been retrieved from the store.
     *
     * @return The get count
     */
    @Override
    public int getGets() {
        return this.gets.get();
    }

    /**
     * Get the number of queries made to the store
     *
     * @return The get count
     */
    @Override
    public int getQueries() {
        return this.queries.get();
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
    public C newClassifier() {
        this.created.incrementAndGet();
        return this.doNewClassifier();
    }

    /**
     * Create a new, empty classifier
     *
     * @return The new classifier
     */
    abstract protected C doNewClassifier();


    /**
     * Store a fully annotated and identified entry in the store.
     *
     * @param classifier The classifier
     *
     * @throws BayesianException if unable to add annotations
      */
    public void store(C classifier) throws BayesianException {
         this.written.incrementAndGet();
         this.doStore(classifier);
    }


    /**
     * Store a fully annotated and identified entry in the store.
     *
     * @param classifier The classifier
     *
     * @throws BayesianException if unable to add annotations
     */
    abstract protected void doStore(C classifier) throws BayesianException;

    /**
     * Store an entry in the store.
     * <p>
     * This assumes that the classifier has not already been in a store.
     * </p>
     *
     * @param classifier The classifier
     * @param type The classifier type. If null the classifier has already had a type set.
     *
     * @throws BayesianException if unable to add annotations
     *
     * @see #store(Classifier)
     */
    public void store(C classifier, Term type) throws BayesianException {
        this.written.incrementAndGet();
        this.added.incrementAndGet();
        this.doStore(classifier, type);
    }

    /**
     * Store an entry in the store.
     * <p>
     * This assumes that the classifier has not already been in a store.
     * </p>
     *
     * @param classifier The classifier
     * @param type The classifier type. If null the classifier has already had a type set.
     *
     * @throws BayesianException if unable to add annotations
     *
     * @see #store(Classifier)
     */
    abstract protected void doStore(C classifier, Term type) throws BayesianException;


    /**
     * Update an existing entry in the store
     *
     * @param classifier The data classifier
     *
     * @throws StoreException if unable to store the entry
     */
    public void update(C classifier) throws StoreException {
        this.updated.incrementAndGet();
        this.doUpdate(classifier);
    }

    /**
     * Update an existing entry in the store
     *
     * @param classifier The data classifier
     *
     * @throws StoreException if unable to store the entry
     */
    abstract protected void doUpdate(C classifier) throws StoreException;

    /**
     * Store a metadata statement.
     *
     * @param metadata The metadata
     */
    abstract public void store(Metadata metadata) throws StoreException;

    /**
     * Store a network description.
     *
     * @param network The network
     */
    abstract public void store(Network network) throws StoreException;

    /**
     * Get a parameter analyser for  this store.
     *
     * @param network The underlying network
     * @param weight The weight observable
     * @param defaultWeight The default weight
     *
     * @return The parameter analyser
     *
     * @throws BayesianException if unable to build a parameter analyser
     */
    abstract public ParameterAnalyser getParameterAnalyser(Network network, Observable weight, double defaultWeight) throws BayesianException;

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
    public C get(Term type, Observable observable, String value) throws StoreException {
        this.gets.incrementAndGet();
        return this.doGet(type, observable, value);
    }


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
    abstract protected C doGet(Term type, Observable observable, String value) throws StoreException;

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
    public Iterable<C> getAll(Term type, Observation... values) throws StoreException {
        this.queries.incrementAndGet();
        return this.doGetAll(type, values);
    }

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
    abstract protected Iterable<C> doGetAll(Term type, Observation... values) throws StoreException;

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
     * Provide a count of classifiers that will be returned by a {@link #getAll(Term, Observation...)} query.
     *
     * @param type The type of document
     * @param values The conditions for the count
     *
     * @return The number of matching classifiers
     *
     * @throws StoreException if there is an error retrieving the count
     */
    abstract public int count(Term type, Observation ... values) throws StoreException;

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
