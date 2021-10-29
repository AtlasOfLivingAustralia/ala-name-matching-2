package au.org.ala.names.builder;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.util.Counter;
import au.org.ala.vocab.BayesianTerm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.gbif.dwc.Archive;
import org.gbif.dwc.ArchiveFile;
import org.gbif.dwc.DwcFiles;
import org.gbif.dwc.UnsupportedArchiveException;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.record.StarRecord;
import org.gbif.dwc.terms.Term;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Load from a Darwin Core Archive.
 * <p>
 * The archive is assumed to be expanded and in a directory.
 * </p>
 */
@Slf4j
public class DwCASource extends Source {
    private List<Path> cleanup;
    private Archive archive;
    private Counter counter;

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
    public DwCASource(URL source, NetworkFactory factory, Collection<Observable> observables, Collection<Term> types) throws IOException, UnsupportedArchiveException {
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
        this.counter = new Counter("Loaded {0} records, {2,number,0.0}/s", log, 10000, -1);
    }

    /**
     * Load the data into a store
     *
     * @param store The store
     * @param accepted The accepted observables (null for all)
     *
     * @throws BayesianException if unable to load into the store
     */
    @Override
    public void load(LoadStore store, Collection<Observable> accepted) throws BayesianException {
        this.counter.start();
        Map<Term, Map<Observable, BiFunction<Record, StarRecord, Object>>> accessors = this.buildAccessors(accepted);
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
        this.counter.stop();
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
     */
    protected Map<Term, Map<Observable, BiFunction<Record, StarRecord, Object>>> buildAccessors(Collection<Observable> accepted) {
        Map<Term, Map<Observable, BiFunction<Record, StarRecord, Object>>> accessors = new HashMap<>();
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
     */
    protected void buildAccessors(ArchiveFile file, Collection<Observable> accepted, Map<Term, Map<Observable, BiFunction<Record, StarRecord, Object>>> accessors) {
        if (!isLoadable(file.getRowType()))
            return;
        Map<Observable, BiFunction<Record, StarRecord, Object>> map = new HashMap<>();
        for (Term term: file.getTerms()) {
            Observable observable = this.getObservable(term);
            if (accepted != null && !accepted.contains(observable))
                continue;
            map.put(observable, this.buildAccessor(file, observable));
        }
        for (Observable observable: this.getObservables()) {
            if (map.containsKey(observable))
                continue;;
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
    protected BiFunction<Record, StarRecord, Object> buildAccessor(final ArchiveFile file, final Observable observable) {
        // Direct access from loaded record (unless this is a link term, in which case prefer the core record)
        if (file.getTerms().contains(observable.getTerm()) && !observable.hasProperty(BayesianTerm.link, true)) {
            return (r, sr) -> {
                String value = null;
                try {
                    value = r.value(observable.getTerm());
                    return observable.getAnalysis().fromString(value);
                } catch (StoreException ex) {
                    throw new IllegalStateException("Unable to read " + value + " for " + observable, ex);
                }
            };
        }
        // Access from core record
        if (this.archive.getCore().getTerms().contains(observable.getTerm())) {
            return (r, sr) -> {
                String value = null;
                try {
                    value = sr.core().value(observable.getTerm());
                    return observable.getAnalysis().fromString(value);
                } catch (StoreException ex) {
                    throw new IllegalStateException("Unable to read " + value + " for " + observable, ex);
                }
            };
        }
        // Access from extended record
        for (ArchiveFile ext: this.archive.getExtensions()) {
            if (ext == file)
                continue;
            if (ext.getTerms().contains(observable.getTerm())) {
                final Term extType = ext.getRowType();
                return (r, sr) -> {
                    String value = null;
                    try {
                        for (Record er: sr.extension(extType)) {
                            value = sr.core().value(observable.getTerm());
                            if (value != null)
                                return observable.getAnalysis().fromString(value);
                        }
                        return null;
                    } catch (StoreException ex) {
                        throw new IllegalStateException("Unable to read " + value + " for " + observable, ex);
                    }
                };
            }
        }
        return (r, sr) -> null;
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
    protected void loadRecord(Record record, StarRecord star, LoadStore store, Map<Term, Map<Observable, BiFunction<Record, StarRecord, Object>>> accessors) throws BayesianException {
        Term type = record.rowType();
        Classifier classifier = store.newClassifier();
        for (Map.Entry<Observable, BiFunction<Record, StarRecord, Object>> accessor: accessors.get(type).entrySet()) {
            Object value = accessor.getValue().apply(record, star);
            if (value != null)
                classifier.add(accessor.getKey(), value);
        }
        try {
            this.infer(classifier);
            store.store(classifier, type);
        } catch (Exception ex) {
            log.error("Skipping invalid classifier: " + ex.getMessage());
            log.info("Classifier " + classifier.getAllValues().stream().map(s -> s[0] + ": " + s[1]).sorted().collect(Collectors.joining(", ")));
        }
        this.counter.increment(classifier.getIdentifier());
    }
}
