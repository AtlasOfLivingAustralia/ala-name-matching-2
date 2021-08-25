package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.util.Counter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.gbif.dwc.Archive;
import org.gbif.dwc.ArchiveFile;
import org.gbif.dwc.DwcFiles;
import org.gbif.dwc.UnsupportedArchiveException;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
     * @throws BuilderException if unable to
     */
    @Override
    public void load(LoadStore store, Collection<Observable> accepted) throws BuilderException, InferenceException, StoreException {
        this.counter.start();
        this.loadArchiveFile(this.archive.getCore(), store, accepted, true);
        for (ArchiveFile ext: this.archive.getExtensions())
            this.loadArchiveFile(ext, store, accepted, false);
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
     * Load a sub-archive (core or extension).
     *
     * @param file The archive file
     * @param store The store to load to
     * @param accepted The list of accepted observables
     * @param core This is the core type of element (inference, based on the factory, is only done to core elements)
     *
     * @throws StoreException if unable to store the resulting document
     */
    protected void loadArchiveFile(ArchiveFile file, LoadStore store, Collection<Observable> accepted, boolean core) throws InferenceException, StoreException {
        Term type = file.getRowType();
        if (!this.isLoadable(type))
            return;
        Set<Observable> terms = file.getTerms().stream().map(t -> this.getObservable(t)).collect(Collectors.toSet());
        for (Record record: file) {
            Classifier classifier = store.newClassifier();
            for (Observable term: terms) {
                if (accepted != null && !accepted.contains(term))
                    continue;
                String value = record.value(term.getTerm());
                Object val = term.getAnalysis().fromString(value);
                if (value != null) {
                    classifier.add(term, val);
                }
            }
            try {
                if (core)
                    this.infer(classifier);
                store.store(classifier, type);
            } catch (Exception ex) {
                log.error("Skipping invalid classifier: " + ex.getMessage());
                log.info("Classifier " + classifier.getAllValues().stream().map(s -> s[0] + ": " + s[1]).sorted().collect(Collectors.joining(", ")));
            }
            this.counter.increment(classifier.getIdentifier());
        }
    }
}
