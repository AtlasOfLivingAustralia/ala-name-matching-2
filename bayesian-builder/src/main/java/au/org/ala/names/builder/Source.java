package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.Observable;
import lombok.NonNull;
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
abstract public class Source {
    private final NetworkFactory factory;
    private final Map<Term, Observable> observables;
    private final Set<Term> types;

    /**
     * Construct with a list of known observables
     *
     * @param observables The list of observables (may be null)
     * @param types The list of record types to load
     */
    public Source(NetworkFactory factory, Collection<Observable> observables, Collection<Term> types) {
        if (observables == null)
            observables = Collections.emptyList();
        this.factory = factory;
        this.observables = observables.stream().collect(Collectors.toMap(o -> o.getTerm(), o -> o));
        this.types = new HashSet<>(types);
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
        return this.observables.computeIfAbsent(term, t -> new Observable(t));
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
     * Create a new, empty classification
     *
     * @return The classification
     */
    @NonNull
    public Classification createClassification() {
        return this.factory.createClassification();
    }

    /**
     * Perform an inference step on a classifier.
     * <p>
     * To do this, we convert the classifier data into a classification,
     * do the inference step and then write it back into the classifier.
     * </p>
     *
     * @param classifier The classifier to expand.
     */
    public void infer(Classifier classifier) throws StoreException, InferenceException {
        Classification classification = this.createClassification();
        classification.read(classifier, true);
        classification.infer(false);
        classification.write(classifier, true);
    }

    /**
     * Load this source into an index builder.
     *
     * @param store The store to load into
     * @param accepted The accepted observations, all if null
     *
     * @throws BuilderException If there is an error in loading
     * @throws InferenceException if there is an error in annotating a record
     * @throws StoreException If there is an error in storing the retrieved data
     */
    abstract public void load(LoadStore store, Collection<Observable> accepted) throws BuilderException, InferenceException, StoreException;

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
    public static Source create(URL source, NetworkFactory factory, Collection<Observable> observables, Collection<Term> types) throws Exception {
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
