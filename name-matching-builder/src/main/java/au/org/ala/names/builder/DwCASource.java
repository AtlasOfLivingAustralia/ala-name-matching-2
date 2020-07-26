package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.StoreException;
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
public class DwCASource extends Source {
    private static final Logger LOGGER = LoggerFactory.getLogger(DwCASource.class);

    private List<Path> cleanup;
    private Archive archive;

    /**
     * Construct with DwCA directory
     *
     * @param source The location of the DwCA
     * @param observables The list of observables expected
     *
     * @throws IOException when attempting to open the archive
     * @throws UnsupportedArchiveException if the archive is invalid
     */
    public DwCASource(URL source, Collection<Observable> observables) throws IOException, UnsupportedArchiveException {
        super(observables);
        this.cleanup = new ArrayList<>();
        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            LOGGER.info("Getting source " + source + " as file " + file);
            if (!file.exists())
                throw new FileNotFoundException("DwCA dnot found " + file);
            if (file.isDirectory()) {
                this.archive = DwcFiles.fromLocation(file.toPath());
            } else {
                Path tmpDir = Files.createTempDirectory("dwca");
                LOGGER.info("Extracting " + file + " info " + tmpDir);
                this.cleanup.add(tmpDir);
                this.archive = DwcFiles.fromCompressed(file.toPath(), tmpDir);
            }
        } else {
            Path download = Files.createTempFile("dwca", "download.zip");
            LOGGER.info("Getting " + source + " as " + download);
            this.cleanup.add(download);
            FileUtils.copyURLToFile(source, download.toFile());
            Path tmpDir = Files.createTempDirectory("dwca");
            LOGGER.info("Extracting " + download + " info " + tmpDir);
            this.cleanup.add(tmpDir);
            this.archive = DwcFiles.fromCompressed(download, tmpDir);
        }
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
        this.loadArchiveFile(this.archive.getCore(), store, accepted);
        for (ArchiveFile ext: this.archive.getExtensions())
            this.loadArchiveFile(ext, store, accepted);
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
     *
     * @throws StoreException if unable to store the resulting document
     */
    protected void loadArchiveFile(ArchiveFile file, LoadStore store, Collection<Observable> accepted) throws InferenceException, StoreException {
        Term type = file.getRowType();
        Set<Observable> terms = file.getTerms().stream().map(t -> this.getObservable(t)).collect(Collectors.toSet());
        for (Record record: file) {
            Classifier classifier = store.newClassifier();
            for (Observable term: terms) {
                if (accepted != null && !accepted.contains(term))
                    continue;
                String value = record.value(term.getTerm());
                if (value != null) {
                    value = value.trim();
                    if (!value.isEmpty())
                        classifier.add(term, value);
                }
            }
            store.store(classifier, type);
        }
    }
}
