package au.org.ala.names.builder;

import au.org.ala.bayesian.Observable;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Load from a CSV file
 */
public class CSVSource extends Source {
    /** The type of data in the source */
    private Term type;
    /** The source of the CSV data */
    private CSVReader reader;
    /** The header map */
    private Observable[] header;

    /**
     * Construct with a type and reader.
     * <p>
     * The CSV reader follows the standard convertions:
     * Header line for columns, comma-separated fields, newline-separated records, enclosed in quotes for problem fields
     * </p>
     *
     * @param type The type of information in the CSV file
     * @param reader The CSV reader
     *
     * @throws IOException when reading the CSV file
     * @throws CsvValidationException if not a CSV file
     */
    public CSVSource(Term type, Reader reader) throws IOException, CsvValidationException {
        this.type = type;
        this.reader = new CSVReaderBuilder(reader).build();
        this.buildHeader();
    }

    /**
     * Construct with a type and URL.
     * <p>
     * The CSV reader follows the standard convertions:
     * Header line for columns, comma-separated fields, newline-separated records, enclosed in quotes for problem fields
     * </p>
     *
     * @param type The record type
     * @param source The record source
     *
     * @throws IOException if unable to get the data
     * @throws CsvValidationException If the file is invalid
     */
    public CSVSource(Term type, URL source) throws IOException, CsvValidationException {
        this.type = type;
        URLConnection connection = source.openConnection();
        String encoding = connection.getContentEncoding();
        Reader r = new InputStreamReader(connection.getInputStream(), encoding != null ? encoding : "UTF-8");
        this.reader = new CSVReaderBuilder(r).build();
        this.buildHeader();
    }

    /**
     * Construct for a default {@link DwcTerm#Taxon} source type.
     *
     * @param source The source CSV file
     *
     * @throws IOException if uable to get the data
     * @throws CsvValidationException if the data is not a CSV file
     *
     * @see #CSVSource(Term, URL)
     */
    public CSVSource(URL source) throws IOException, CsvValidationException {
        this(DwcTerm.Taxon, source);
    }

    /**
     * Load the data into a store
     *
     * @param store The store
     * @throws BuilderException if unable to load correctly
     */
    @Override
    public void load(LoadStore store) throws BuilderException {
        String[] line;

        try {
            while ((line = this.reader.readNext()) != null) {
                Document document = new Document();
                for (int i = 0; i < this.header.length; i++) {
                    IndexableField field = store.convert(this.header[i], i < line.length ? line[i] : null);
                    if (field != null)
                        document.add(field);
                }
                store.store(document, this.type);
            }
        } catch (Exception ex) {
            throw new BuilderException("Unable to read CSV file", ex);
        }
    }

    /**
     * Close this source after loading
     *
     * @throws IOException If unable to close for some reason
     */
    @Override
    public void close() throws Exception {
        this.reader.close();
    }

    private void buildHeader() throws IOException, CsvValidationException {
        String[] head = this.reader.readNext();

        this.header = new Observable[head.length];
        for (int i = 0; i < head.length; i++) {
            this.header[i] = new Observable(TermFactory.instance().findTerm(head[i]));
        }
    }
}
