package au.org.ala.names.builder;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.StoreException;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A source of name information.
 * <p>
 * Sources can be used to populate the index builder.
 * </p>
 */
abstract public class Source {
    private Map<Term, Observable> observables;

    /**
     * Construct with a list of known observables
     *
     * @param observables The list of observables (may be null)
     */
    public Source(Collection<Observable> observables) {
        if (observables == null)
            observables = Collections.EMPTY_LIST;
        this.observables = observables.stream().collect(Collectors.toMap(o -> o.getTerm(), o -> o));
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
     * Load this source into an index builder.
     *
     * @param store The store to load into
     *
     * @throws BuilderException If there is an error in loading
     * @throws StoreException If there is an error in storing the retrieved data
     */
    abstract public void load(LoadStore store) throws BuilderException, StoreException;

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
     * @param observables Any known observables
     *
     * @return An appropriate source.
     *
     * @throws Exception for all kinds of reasons
     */
    public static Source create(URL source, Collection<Observable> observables) throws Exception {
        if (source.getFile().endsWith(".csv")) {
            return new CSVSource(DwcTerm.Taxon, source, observables);
        }
        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            if (file.exists() && file.isDirectory())
                return new DwCASource(source, observables);
        }
        if (source.getProtocol().equals("file") || source.getProtocol().equals("jar") || source.getProtocol().equals("http") || source.getProtocol().equals("https"))
            return new DwCASource(source, observables);
        throw new BuilderException("Unable to to deduce source type for " + source);
    }
}
