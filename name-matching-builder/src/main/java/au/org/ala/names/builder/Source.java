package au.org.ala.names.builder;

import com.opencsv.exceptions.CsvValidationException;
import org.gbif.dwc.terms.DwcTerm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * A source of name information.
 * <p>
 * Sources can be used to populate the index builder.
 * </p>
 */
abstract public class Source {
    /**
     * Load this source into an index builder.
     *
     * @param store The store to load into
     *
     * @throws BuilderException If there is an error in loading
     */
    abstract public void load(LoadStore store) throws BuilderException;

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
     *
     * @return An appropriate source.
     *
     * @throws Exception for all kinds of reasons
     */
    public static Source create(URL source) throws Exception {
        if (source.getFile().endsWith(".csv")) {
            return new CSVSource(DwcTerm.Taxon, source);
        }
        if (source.getProtocol().equals("file")) {
            File file = new File(source.getPath());
            if (file.exists() && file.isDirectory())
                return new DwCASource(source);
        }
        throw new BuilderException("Unable to to deduce source type for " + source);
    }
}
