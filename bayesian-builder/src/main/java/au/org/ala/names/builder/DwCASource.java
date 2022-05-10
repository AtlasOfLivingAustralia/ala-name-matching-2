package au.org.ala.names.builder;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.vocab.BayesianTerm;
import au.org.ala.vocab.OptimisationTerm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.gbif.dwc.Archive;
import org.gbif.dwc.ArchiveFile;
import org.gbif.dwc.DwcFiles;
import org.gbif.dwc.UnsupportedArchiveException;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.StarRecord;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Load from a Darwin Core Archive.
 * <p>
 * The archive is assumed to be expanded and in a directory.
 * </p>
 */
@Slf4j
public class DwCASource extends Source {
    private final List<Path> cleanup;
    @Getter
    private Archive archive;

    /**
     * Construct with DwCA directory
     *
     * @param source The location of the DwCA
     * @param factory The network factory
     * @param observables The list of observables expected
     *
     * @throws IOException when attempting to open the archive
     * @throws UnsupportedArchiveException if the archive is invalid
     */
    public DwCASource(URL source, NetworkFactory factory, Collection<Observable<?>> observables, Collection<Term> types) throws IOException, UnsupportedArchiveException {
        super(factory, observables, types);
        this.cleanup = new ArrayList<>();
        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            log.info("Getting source " + source + " as file " + file);
            if (!file.exists())
                throw new FileNotFoundException("DwCA dnot found " + file);
            if (file.isDirectory()) {
                this.archive = DwcFiles.fromLocation(file.toPath());
            } else {
                Path tmpDir = Files.createTempDirectory("dwca");
                log.info("Extracting " + file + " info " + tmpDir);
                this.cleanup.add(tmpDir);
                this.archive = DwcFiles.fromCompressed(file.toPath(), tmpDir);
            }
        } else {
            Path download = Files.createTempFile("dwca", "download.zip");
            log.info("Getting " + source + " as " + download);
            this.cleanup.add(download);
            FileUtils.copyURLToFile(source, download.toFile());
            Path tmpDir = Files.createTempDirectory("dwca");
            log.info("Extracting " + download + " info " + tmpDir);
            this.cleanup.add(tmpDir);
            this.archive = DwcFiles.fromCompressed(download, tmpDir);
        }
    }

    /**
     * Load the data into a store
     *
     * @param store The store
     * @param accepted The accepted observables (null for all not set with {@link OptimisationTerm#load} = false)
     *
     * @throws BayesianException if unable to load into the store
     */
    @Override
    public void load(LoadStore store, Collection<Observable> accepted) throws BayesianException {
        this.getCounter().start();
        Map<Term, Map<Observable, BiFunction<Record, StarRecord, Set<Object>>>> accessors = this.buildAccessors(accepted);
        for (StarRecord star: this.archive) {
            if (this.isLoadable(star.core().rowType()))
                this.loadRecord(star.core(), star, store, accessors);
            for (Map.Entry<Term, List<Record>> ext: star.extensions().entrySet()) {
                if (this.isLoadable(ext.getKey())) {
                    for (Record er: ext.getValue()) {
                        this.loadRecord(er, star, store, accessors);
                    }
                }
            }
        }
        this.getCounter().stop();
    }

    /**
     * Close this source after loading
     *
     * @throws IOException If unable to close for some reason
     */
    @Override
    public void close() throws Exception {
        this.archive = null;
        for (Path path: this.cleanup) {
            File file = path.toFile();
            if (file.exists()) {
                if (file.isDirectory())
                    FileUtils.deleteDirectory(file);
                else
                    file.delete();
            }
        }
    }

    /**
     * Build an accessor map for all files in the archive
     *
     * @param accepted The accepted terms (null for all)
     *
     * @return The accessor map.
     *
     * @throws BayesianException if unable to buld the accessors
     */
    protected Map<Term, Map<Observable, BiFunction<Record, StarRecord, Set<Object>>>> buildAccessors(Collection<Observable> accepted) throws BayesianException {
        Map<Term, Map<Observable, BiFunction<Record, StarRecord, Set<Object>>>> accessors = new HashMap<>();
        this.buildAccessors(this.archive.getCore(), accepted, accessors);
        for (ArchiveFile ext: this.archive.getExtensions())
            this.buildAccessors(ext, accepted, accessors);
        return accessors;
    }

    /**
     * Build an accessor map for a particular type of record.
     *
     * @param file The archive file
     * @param accepted The list of accepted observables (null for all)
     * @param accessors The accessor map
     *
     * @throws BayesianException if unable to build the accessor
     */
    protected void buildAccessors(ArchiveFile file, Collection<Observable> accepted, Map<Term, Map<Observable, BiFunction<Record, StarRecord, Set<Object>>>> accessors) throws BayesianException {
        if (!isLoadable(file.getRowType()))
            return;
        Map<Observable, BiFunction<Record, StarRecord, Set<Object>>> map = new HashMap<>();
        for (Term term: file.getTerms()) {
            Observable observable = this.getObservable(term);
            if (observable.hasProperty(OptimisationTerm.load, false))
                continue;
            if (accepted != null && !accepted.contains(observable))
                continue;
            map.put(observable, this.buildAccessor(file, observable));
        }
        for (Observable observable: this.getObservables()) {
            if (map.containsKey(observable))
                continue;
            if (observable.hasProperty(OptimisationTerm.load, false))
                continue;
            if (accepted != null && !accepted.contains(observable))
                continue;
            map.put(observable, this.buildAccessor(file, observable));
        }
        accessors.put(file.getRowType(), map);
    }

    /**
     * Simple retrieval of an observable from a record.
     *
     * @param observable The observable
     *
     * @return A function that will retrieve the record
     */
    protected BiFunction<Record, StarRecord, Set<Object>> buildAccessor(final ArchiveFile file, final Observable observable) throws BayesianException {
        // Get the terms to use
        final Set<Term> terms = observable.getProperties(OptimisationTerm.loadFromTerm, observable.getTerm());
        final Set<Term> classes = observable.getProperties(OptimisationTerm.loadFromClass, file.getRowType().qualifiedName()).stream()
                .map(t -> TermFactory.instance().findTerm(t))
                .collect(Collectors.toSet());
        final String aggregate = observable.getProperty(OptimisationTerm.aggregate, "all").toLowerCase();
        final Predicate<Record> filter = this.buildFilter(observable);
        final Comparator<Record> order = this.buildOrder(observable, aggregate);
        final Analysis<?, ?, ?> analysis = observable.getAnalysis();
        if (terms.isEmpty())
            return (r, sr) -> Collections.emptySet();
        return (r, sr) -> {
            final LinkedHashSet<Object> values = new LinkedHashSet<>(); // Keep order of values
            for (Term term : terms) {
                // Direct access from loaded record (unless this is a link term, in which case prefer the core record)
                if (classes.contains(file.getRowType()) && file.getTerms().contains(term) && !observable.hasProperty(BayesianTerm.link, true)) {
                    String value = null;
                    try {
                        value = r.value(term);
                        Object a = analysis.fromString(value);
                        if (a != null)
                            values.add(a);
                    } catch (StoreException ex) {
                        throw new IllegalStateException("Unable to read " + value + " for " + observable, ex);
                    }
                }
                // Access from core record
                if (this.archive.getCore().getTerms().contains(term) && (!file.getTerms().contains(term) || observable.hasProperty(BayesianTerm.link, true))) {
                    String value = null;
                    try {
                        value = sr.core().value(term);
                        Object a = analysis.fromString(value);
                        if (a != null)
                            values.add(a);
                    } catch (StoreException ex) {
                        throw new IllegalStateException("Unable to read " + value + " for " + observable, ex);
                    }
                }
                // Access from extended record
                for (ArchiveFile ext : this.archive.getExtensions()) {
                    if (ext == file || !classes.contains(ext.getRowType()) || !ext.getTerms().contains(term))
                        continue;
                    if (ext.getTerms().contains(observable.getTerm())) {
                        final Term extType = ext.getRowType();
                        Stream<Record> es = sr.extension(extType).stream();
                        if (filter != null)
                            es = es.filter(filter);
                        if (order != null)
                            es = es.sorted(order);
                        Stream<Object> os = es.map(er -> er.value(term)).filter(Objects::nonNull).map(v -> {
                            try {
                                return analysis.fromString(v);
                            } catch (StoreException ex) {
                                throw new IllegalStateException("Unable to read " + v + " for " + observable, ex);
                            }
                        });
                        switch (aggregate) {
                            case "all":
                                os.forEach(v -> values.add(v));
                                break;
                            case "first":
                            case "max":
                            case "min":
                                os.findFirst().ifPresent(v -> values.add(v));
                                break;
                        }
                    }
                }
            }
            return values;
        };
    }

    /**
     * Build an order for the observable.
     * <p>
     * If there is a specific {@link OptimisationTerm#dwcaOrder} specified, then used that.
     * Otherwise, use an order based on the type of the observable.
     * </p>
     *
     * @param observable The observable
     * @param aggregate The aggregate type
     *
     * @return A suitable comparator or null for no ordering
     *
     * @throws BayesianException if unable to build the order
     */
    protected Comparator<Record> buildOrder(final Observable observable, final String aggregate) throws BayesianException {
        if (aggregate.equals("all") || aggregate.equals("first"))
            return null;
        final Term term = observable.getTerm();
        Comparator<Record> comparator = null;
        String orderClass = observable.getProperty(OptimisationTerm.dwcaOrder);
        if (orderClass != null) {
            try {
                comparator = (Comparator<Record>) Class.forName(orderClass).newInstance();
            } catch (Exception ex) {
                throw new BuilderException("Can't create order " + orderClass + " on " + observable, ex);
            }
        }
        if (aggregate.equals("all") || aggregate.equals("first"))
            return comparator;
        if (comparator == null && Comparable.class.isAssignableFrom(observable.getType())) {
            comparator = (r1, r2) -> {
                try {
                    Comparable v1 = (Comparable) observable.getAnalysis().fromString(r1.value(term));
                    Comparable v2 = (Comparable) observable.getAnalysis().fromString(r2.value(term));
                    if (v1 == null && v2 == null)
                        return 0;
                    if (v1 == null)
                        return Integer.MIN_VALUE;
                    if (v2 == null)
                        return Integer.MAX_VALUE;
                    return v1.compareTo(v2);
                } catch (StoreException ex) {
                    throw new IllegalStateException("Unable to read value for " + observable, ex);
                }
            };
        }
        if (comparator == null) {
            comparator = (r1, r2) -> {
                String v1 = r1.value(term);
                String v2 = r2.value(term);
                if (v1 == null && v2 == null)
                    return 0;
                if (v1 == null)
                    return Integer.MIN_VALUE;
                if (v2 == null)
                    return Integer.MAX_VALUE;
                return v1.compareTo(v2);
            };
        }
        if (aggregate.equals("max"))
            return comparator.reversed();
        return comparator;
    }


    /**
     * Build an filter for the observable.
     * <p>
     * If there is a specific {@link OptimisationTerm#dwcaFilter} specified, then used that.
     * Otherwise, accept all records.
     * </p>
     *
     * @param observable The observable
     *
     * @return A sutiable predicate
     *
     * @throws BayesianException if unable to build the order
     */
    protected Predicate<Record> buildFilter(final Observable observable) throws BayesianException {
        final Term term = observable.getTerm();
        String filterClass = observable.getProperty(OptimisationTerm.dwcaFilter);
        if (filterClass != null) {
            try {
                return (Predicate<Record>) Class.forName(filterClass).newInstance();
            } catch (Exception ex) {
                throw new BuilderException("Can't create order " + filterClass + " on " + observable, ex);
            }
        }
        return r1 -> true;
    }

    /**
     * Load a sub-archive (core or extension).
     *
     * @param record The main record
     * @param star The star record with core and all extensions
     * @param store The store to load to
     * @param accessors The lobservables to load and how to access them.
     *
     * @throws BayesianException if unable to store the resulting document
     */
    protected void loadRecord(Record record, StarRecord star, LoadStore store, Map<Term, Map<Observable, BiFunction<Record, StarRecord, Set<Object>>>> accessors) throws BayesianException {
        Term type = record.rowType();
        Classifier classifier = store.newClassifier();
        for (Map.Entry<Observable, BiFunction<Record, StarRecord, Set<Object>>> accessor: accessors.get(type).entrySet()) {
            Set<Object> values = accessor.getValue().apply(record, star);
            Observable observable = accessor.getKey();
            if (values != null) {
                for (Object value: values) {
                    if (value !=null)
                        classifier.add(observable, value, observable.hasProperty(OptimisationTerm.loadAsVariant, true), false);
                }
            }
        }
        try {
            store.store(classifier, type);
        } catch (Exception ex) {
            log.error("Skipping invalid classifier: " + ex.getMessage(), ex);
            log.info("Classifier " + classifier.getAllValues().stream().map(s -> s[0] + ": " + s[1]).sorted().collect(Collectors.joining(", ")));
        }
        this.getCounter().increment(classifier.getIdentifier());
    }
}
