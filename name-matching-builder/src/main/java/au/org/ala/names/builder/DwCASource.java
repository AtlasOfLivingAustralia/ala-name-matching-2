package au.org.ala.names.builder;

import au.org.ala.bayesian.Observable;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.gbif.dwc.Archive;
import org.gbif.dwc.ArchiveFile;
import org.gbif.dwc.DwcFiles;
import org.gbif.dwc.UnsupportedArchiveException;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
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
    private List<Path> cleanup;
    private Archive archive;

    /**
     * Construct with DwCA directory
     *
     * @param source The location of the DwCA
     *
     * @throws IOException when attempting to open the archive
     * @throws UnsupportedArchiveException if the archive is invalid
     */
    public DwCASource(URL source) throws IOException, UnsupportedArchiveException {
        this.cleanup = new ArrayList<>();
        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            if (!file.exists())
                throw new FileNotFoundException("DwCA dnot found " + file);
            if (file.isDirectory()) {
                this.archive = DwcFiles.fromLocation(file.toPath());
            } else {
                Path tmpDir = Files.createTempDirectory("dwca");
                this.cleanup.add(tmpDir);
                this.archive = DwcFiles.fromCompressed(file.toPath(), tmpDir);
            }
        } else {
            Path download = Files.createTempFile("dwca", "download.zip");
            this.cleanup.add(download);
            FileUtils.copyURLToFile(source, download.toFile());
            Path tmpDir = Files.createTempDirectory("dwca");
            this.cleanup.add(tmpDir);
            this.archive = DwcFiles.fromCompressed(download, tmpDir);
        }
    }

    /**
     * Load the data into a store
     *
     * @param store The store
     *
     * @throws BuilderException if unable to
     */
    @Override
    public void load(LoadStore store) throws BuilderException {
        this.loadArchiveFile(this.archive.getCore(), store);
        for (ArchiveFile ext: this.archive.getExtensions())
            this.loadArchiveFile(ext, store);
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
     *
     * @throws StoreException if unable to store the resulting document
     */
    protected void loadArchiveFile(ArchiveFile file, LoadStore store) throws StoreException {
        Term type = file.getRowType();
        Set<Observable> terms = file.getTerms().stream().map(t -> new Observable(t)).collect(Collectors.toSet());
        for (Record record: file) {
            Document document = new Document();
            for (Observable term: terms) {
                IndexableField field = store.convert(term, record.value(term.getTerm()));
                if (field != null)
                    document.add(field);
            }
            store.store(document, type);
        }
    }
}
