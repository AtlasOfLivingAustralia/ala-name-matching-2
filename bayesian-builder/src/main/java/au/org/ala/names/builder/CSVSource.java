package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Observable;
import au.org.ala.vocab.OptimisationTerm;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;

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
     * @param factory The network factory
     * @param observables Any klnown observables
     *
     *
     * @throws IOException when reading the CSV file
     * @throws CsvValidationException if not a CSV file
     */
    public CSVSource(Term type, Reader reader, NetworkFactory factory, Collection<Observable> observables) throws IOException, CsvValidationException {
        super(factory, observables, Collections.singleton(type));
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
     * @param factory The network factory
     * @param observables Any known observables
     *
     * @throws IOException if unable to get the data
     * @throws CsvValidationException If the file is invalid
     */
    public CSVSource(Term type, URL source, NetworkFactory factory, Collection<Observable> observables) throws IOException, CsvValidationException {
        super(factory, observables, Collections.singleton(type));
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
     * @param factory The network factory
     * @param observables Any known observables
     *
     * @throws IOException if uable to get the data
     * @throws CsvValidationException if the data is not a CSV file
     *
     * @see #CSVSource(Term, URL, NetworkFactory, Collection)
     */
    public CSVSource(URL source,NetworkFactory factory, Collection<Observable> observables) throws IOException, CsvValidationException {
        this(DwcTerm.Taxon, source, factory, observables);
    }

    /**
     * Load the data into a store
     *
     * @param store The store
     * @throws BuilderException if unable to load correctly
     */
    @Override
    public void load(LoadStore store, Collection<Observable> accepted) throws BuilderException {
        String[] line;
        try {
            while ((line = this.reader.readNext()) != null) {
                Classifier classifier = store.newClassifier();
                for (int i = 0; i < this.header.length; i++) {
                    Observable observable = header[i];
                    if (observable.hasProperty(OptimisationTerm.load, false))
                        continue;
                    if (accepted != null && !accepted.contains(header[i]))
                        continue;
                    String value = line[i];
                    Object val = observable.getAnalysis().fromString(value);
                    if (val != null)
                        classifier.add(header[i], val);
                }
                this.infer(classifier);
                store.store(classifier, this.type);
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
            this.header[i] = this.getObservable(TermFactory.instance().findTerm(head[i]));
        }
    }
}
