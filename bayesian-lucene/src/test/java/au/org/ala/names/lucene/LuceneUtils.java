package au.org.ala.names.lucene;

import au.org.ala.bayesian.ExternalContext;
import au.org.ala.bayesian.Observable;
import au.org.ala.util.TestUtils;
import au.org.ala.vocab.BayesianTerm;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.Getter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.TermFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrap a collection of useful lucene bits and pieces
 */
public class LuceneUtils {
    @Getter
    private Path indexDir;
    @Getter
    private IndexWriter indexWriter;
    @Getter
    private IndexReader indexReader;
    @Getter
    private IndexSearcher searcher;
    @Getter
    private Map<String, Observable> observables;

    /**
     * Create a lucene utility connection for a resourced CSV file containing index terms
     *
     * @param clazz The class to load from
     * @param resource The resource to load (CSV file)
     * @param observables The list of observables to convert from/to
     *
     * @throws Exception if unable to load the resource
     */
    public LuceneUtils(Class clazz, String resource, Collection<Observable<?>> observables) throws Exception {
        this.buildObservables(observables);
        this.buildIndexDir();
        this.openWriter();
        this.makeIndex(clazz, resource);
        this.openSearcher();
    }


    /**
     * Create a lucene utility connection for an empty index
     *
     * @param observables The list of observables to convert from/to
     *
     * @throws Exception if unable to load the resource
     */
    public LuceneUtils(Collection<Observable<?>> observables) throws Exception {
        this.buildObservables(observables);
        this.buildIndexDir();
        this.openWriter();
        this.indexWriter.commit();
        this.openSearcher();
    }

    /**
     * Close the connection.
     *
     * @throws IOException if unable to close for some reason.
     */
    public void close() throws IOException {
        this.searcher = null;
        if (this.indexReader != null) {
            this.indexReader.close();
            this.indexReader = null;
        }
        if (this.indexWriter != null) {
            this.indexWriter.close();
            this.indexWriter = null;
        }
        if (this.indexDir != null) {
            TestUtils.deleteAll(this.indexDir.toFile());
            this.indexDir = null;
        }
    }

    /**
     * Add a document to the index.
     * <p>
     * Immediately commits and re-opens the index reader/searcher
     * </p>
     * @param document The document to add
     * @return The document identifier
     * @throws Exception
     */
    public String add(Document document) throws Exception {
        String id = UUID.randomUUID().toString();
        document.removeFields("id");
        document.add(new StringField("id", id, Field.Store.YES));
        this.indexWriter.addDocument(document);
        this.indexWriter.commit();
        this.indexReader.close();
        this.openSearcher();
        return id;
    }

    /**
     * Get an identified document
     *
     * @param id The ID
     *
     * @return The document
     *
     * @throws IllegalArgumentException if the document is not found
     * @throws IOException if unable to read the index
     */
    public Document get(String id) throws IllegalArgumentException, IOException {
        TopDocs docs = this.searcher.search(new TermQuery(new Term("id", id)), 1);
        if (docs.totalHits.value < 1)
            throw new IllegalArgumentException("Unable to find document for " + id);
        return this.searcher.doc(docs.scoreDocs[0].doc);
    }

    private void buildObservables(Collection<Observable<?>> observables) {
        this.observables = new HashMap<>();
        Observable<Double> weight = new Observable<>(BayesianTerm.weight, Double.class, Observable.Style.IDENTIFIER, null, null, Observable.Multiplicity.OPTIONAL);
        this.observables.put("weight", weight);
        for (Observable o: observables) {
            this.observables.put(o.getId(), o);
            this.observables.put(o.getExternal(ExternalContext.LUCENE), o);
        }
    }

    /**
     * Create an index directory to work with
     *
     * @throws Exception if unable to create the directory
     */
    private void buildIndexDir() throws Exception {
        this.indexDir = Files.createTempDirectory("Lucene");
    }

    /**
     * Open an index writer to the index directory
     *
     * @throws Exception if unable to open the writer
     */
    private void openWriter() throws Exception {
        QueryUtils queryUtils = new QueryUtils();
        Directory directory = FSDirectory.open(this.indexDir);
        IndexWriterConfig config = new IndexWriterConfig(queryUtils.getAnalyzer());
        this.indexWriter = new IndexWriter(directory, config);
    }

    /**
     * Create an index reader and search to the index directory
     *
     * @throws Exception if unable to open the reader/searcher
     */
    private void openSearcher() throws Exception {Directory directory = FSDirectory.open(this.indexDir);
        this.indexReader = DirectoryReader.open(directory);
        this.searcher = new IndexSearcher(this.indexReader);
    }

    /**
     * Make a lucene index to play with from a CSV file
     *
     * @param clazz The class to load the resource for
     * @param resource The resource path to the CSV fiel
     *
     * @throws Exception
     */
    private void makeIndex(Class clazz, String resource) throws Exception {
        Reader r = TestUtils.getResourceReader(clazz, resource);
        CSVReader reader = new CSVReaderBuilder(r).build();
        String[] header = reader.readNext();
        Observable[] headerMap = new Observable[header.length];
        headerMap = Arrays.stream(header)
                .map(h -> this.observables.computeIfAbsent(h, o -> Observable.string(TermFactory.instance().findTerm(o))))
                .collect(Collectors.toList())
                .toArray(headerMap);
        for (String[] line: reader) {
            LuceneClassifier classifier = new LuceneClassifier();
            for (int i = 0; i < header.length; i++) {
                if (i < line.length && line[i] != null && !line[i].isEmpty()) {
                    String value = line[i];
                    if (value == null || value.isEmpty())
                        continue;
                    Observable observable = headerMap[i];
                    Object v = observable.getAnalysis().fromString(value);
                    classifier.add(observable, v, false, false);
                  }
            }
            classifier.setType(DwcTerm.Taxon);
            this.indexWriter.addDocument(classifier.getDocument());
        }
        this.indexWriter.commit();
    }

}
