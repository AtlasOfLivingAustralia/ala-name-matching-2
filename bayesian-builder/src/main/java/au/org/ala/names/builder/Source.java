package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.Observable;
import au.org.ala.util.Counter;
import au.org.ala.util.Metadata;
import au.org.ala.vocab.OptimisationTerm;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A source of name information.
 * <p>
 * Sources can be used to populate the index builder.
 * </p>
 */
@Slf4j
abstract public class Source {
    private final NetworkFactory factory;
    private final Map<Term, Observable<?>> observables;
    private final Set<Term> types;
    @Getter
    @Setter
    private Metadata metadata;
    @Getter
    private final Counter counter;
    @Getter
    private final Counter rejected;
    @Getter
    private final Set<String> keys;


    /**
     * Construct with a list of known observables
     *
     * @param factory The network factory
     * @param observables The list of observables (may be null, in which case the factory list is used)
     * @param types The list of record types to load
     */
    public Source(NetworkFactory factory, Collection<Observable<?>> observables, Collection<Term> types) {
        if (observables == null)
            observables = factory.getObservables();
        this.factory = factory;
        this.observables = observables.stream().collect(Collectors.toMap(o -> o.getTerm(), o -> o));
        this.types = new HashSet<>(types);
        this.metadata = Metadata.builder().identifier(UUID.randomUUID().toString()).build();
        this.counter = new Counter("Loaded {0} records, {2,number,0.0}/s", log, 10000, -1);
        this.rejected = new Counter("Rejected {0} records due to key collisions", log, 10000, -1);
        this.keys = new HashSet<String>(10000);
    }

    /**
     * Get an observable matching a term.
     * <p>
     * If a term is absent, a new default observable is constructed for the term.
     * </p>
     *
     * @param term The term to look for
     *
     * @return An observable matching the term
     */
    public @NotNull Observable getObservable(@NotNull Term term) {
        return this.observables.computeIfAbsent(term, t -> Observable.string(t));
    }

    /**
     * Get the possible observables
     *
     * @return The list of observables
     */
    public Set<Observable> getObservables() {
        return this.observables.values().stream().collect(Collectors.toSet());
    }

    /**
     * Check to see whether a record type should be loaded.
     *
     * @param type The record type
     *
     * @return True if this record type should be loaded
     */
    public boolean isLoadable(Term type) {
        return this.types.contains(type);
    }

    /**
     * Check to see whether a classifier should be loaded.
     * <p>
     * By default, this keeps track of keys and does not load a record with a duplicate key
     * </p>
     *
     * @param classifier The classifier
     *
     * @return True if this record should be loaded
     *
     * @throws StoreException if unable to get the type of the classifier
     */
    synchronized public boolean isLoadable(Classifier classifier, Term type) throws StoreException {
        if (type != this.factory.getConcept())
            return true;
        String key = this.buildKey(classifier);
        if (key == null)
            return true;
        if (this.keys.contains(key)) {
            this.rejected.increment(classifier.getIdentifier());
            return false;
        }
        this.keys.add(key);
        return true;
    }

    /**
     * Create a new, empty classification
     *
     * @return The classification
     */
    @NonNull
    public Classification createClassification() {
        return this.factory.createClassification();
    }

    /**
     * Build a key for a classifier
     *
     * @param classifier The classifier
     * @return The computed key, or null for no key
     */
    public String buildKey(Classifier classifier) {
        List<Observable<?>> key = this.factory.getKey();
        if (key == null)
            return null;
        return key.stream()
                .map(o -> this.keyValue(classifier, (Observable<?>) o))
                .collect(Collectors.joining("|"));
    }

    /**
     * Convert the value in a classifier into a suitable string
     *
     * @param classifier The classifier
     * @param observable The observale to get the key from
     *
     * @return The string representation of that value (an empty string if none)
     */
    private String keyValue(Classifier classifier, Observable<?> observable) {
        Object v = classifier.get(observable);
        return v == null ? "" : v.toString();
    }

    /**
     * Load this source into an index builder.
     *
     * @param store The store to load into
     * @param accepted The accepted observables (null for all not set with {@link OptimisationTerm#load} = false)
     *
     * @throws BayesianException If there is an error in loading
     */
    abstract public void load(LoadStore store, Collection<Observable> accepted) throws BayesianException;

    /**
     * Close this source after loading
     *
     * @throws IOException If unable to close for some reason
     */
    abstract public void close() throws Exception;

    /**
     * Create a source from a file.
     * <p>
     * The file is examined and an appropriate record source built.
     * </p>
     *
     * @param source The source URL
     * @param factory The network factory
     * @param observables Any known observables
     *
     * @return An appropriate source.
     *
     * @throws Exception for all kinds of reasons
     */
    public static Source create(URL source, NetworkFactory factory, Collection<Observable<?>> observables, Collection<Term> types) throws Exception {
        if (source.getFile().endsWith(".csv")) {
            return new CSVSource(types.isEmpty() ? DwcTerm.Taxon : types.iterator().next(), source, factory, observables);
        }
        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            if (file.exists() && file.isDirectory())
                return new DwCASource(source, factory, observables, types);
        }
        if (source.getProtocol().equals("file") || source.getProtocol().equals("jar") || source.getProtocol().equals("http") || source.getProtocol().equals("https"))
            return new DwCASource(source, factory, observables, types);
        throw new BuilderException("Unable to to deduce source type for " + source);
    }
}
