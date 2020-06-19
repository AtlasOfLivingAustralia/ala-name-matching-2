package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.names.model.ExternalContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.search.Query;
import org.gbif.dwc.terms.Term;

import javax.print.Doc;
import java.net.URI;
import java.util.List;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A store of loaded but un-structured name data.
 * <p>
 *     Stores use lucene {@link Document}s as the base storage form.
 *     Mostly because they have the functionality desired and it's not rebuilding a model.
 * </p>
 */
abstract public class LoadStore {
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
     * Store an entry in the store
     *
     * @param document The data document
     * @param type The document type
     *
     * @throws StoreException if unable to store the entry
     */
    abstract public void store(Document document, Term type) throws StoreException;

    /**
     * Update an existing entry in the store
     *
     * @param document The data document
     *
     * @throws StoreException if unable to store the entry
     */
    abstract public void update(Document document) throws StoreException;

    /**
     * Get a parameter analyser for this store.
     *
     * @param network The underlying network
     * @param weight The weight observable
     * @param defaultWeight The default weight
     *
     * @return The parameter analyser
     */
    abstract public ParameterAnalyser<Document> getParameterAnalyser(Network network, Observable weight, double defaultWeight) throws InferenceException, StoreException;

    /**
     * Get a store entry by unique name/value.
     *
     * @param type The type of entry to get
     * @param observable The observable to compare
     * @param value The value the term has. If null, then choose entries with no value.
     *
     * @return A matching document for this entry
     *
     * @throws StoreException if there is an error with the underlying store
     */
    abstract public Document get(Term type, Observable observable, String value) throws StoreException;

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
    abstract public Iterable<Document> getAll(Term type, Observation... values) throws StoreException;

    /**
     * Get a list of all terms that match a particular value
     *
     * @param type The type of entry to get
     * @param values The list of conditions that must be met
     *
     * @return The matching values as a collection
     *
     * @throws StoreException if unable to read the store
     */
    public List<Document> getAllDocs(Term type, Observation... values) throws StoreException {
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

    /**
     * Convert a observable/value pair into a lucene field
     *
     * @param observable The term
     * @param value The object value
     *
     * @return A corresponding field
     *
     * @throws StoreException if unable to store the data
     */
    public Field convert(Observable observable, Object value) throws StoreException {
        if (value == null || ((value instanceof String) && ((String) value).isEmpty()))
            return null;
        String field = observable.getField();
        if (value instanceof Integer)
            return new StoredField(field, ((Number) value).intValue());
        else if (value instanceof Number)
            return new StoredField(field, ((Number) value).doubleValue());
        else
            return new StringField(field, value.toString(), Field.Store.YES);
    }
}
