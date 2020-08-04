package au.org.ala.names.lucene;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.ExternalContext;
import au.org.ala.util.TestUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrap a collection of useful lucene bits and pieces
 */
public class LuceneUtils {
    private Path indexDir;
    private IndexReader indexReader;
    private IndexSearcher searcher;
    private Map<String, Observable> observables;

    /**
     * Create a lucene utility connection for a resourced CSV file containing index terms
     *
     * @param clazz The class to load from
     * @param resource The resource to load (CSV file)
     *
     * @throws Exception if unable to load the resource
     */
    public LuceneUtils(Class clazz, String resource, Collection<Observable> observables) throws Exception {
        this.observables = new HashMap<>();
        for (Observable o: observables) {
            this.observables.put(o.getId(), o);
            this.observables.put(o.getExternal(ExternalContext.LUCENE), o);
        }
        this.makeIndex(clazz, resource);
    }

    public Path getIndexDir() {
        return indexDir;
    }

    public IndexReader getIndexReader() {
        return indexReader;
    }

    public IndexSearcher getSearcher() {
        return searcher;
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
        if (this.indexDir != null) {
            TestUtils.deleteAll(this.indexDir.toFile());
            this.indexDir = null;
        }
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
        this.indexDir = Files.createTempDirectory("Lucene");
        QueryUtils queryUtils = new QueryUtils();
        Directory directory = FSDirectory.open(this.indexDir);
        IndexWriterConfig config = new IndexWriterConfig(queryUtils.getAnalyzer());
        IndexWriter writer = new IndexWriter(directory, config);
        Reader r = TestUtils.getResourceReader(clazz, resource);
        CSVReader reader = new CSVReaderBuilder(r).build();
        String[] header = reader.readNext();
        Observable[] headerMap = new Observable[header.length];
        headerMap = Arrays.stream(header).map(h -> this.observables.get(h)).collect(Collectors.toList()).toArray(headerMap);
        for (String[] line: reader) {
            Document doc = new Document();
            for (int i = 0; i < header.length; i++) {
                if (i < line.length && line[i] != null && !line[i].isEmpty()) {
                    String value = line[i];
                    if (value == null || value.isEmpty())
                        continue;
                    Observable observable = headerMap[i];
                    String field = observable != null ? observable.getExternal(ExternalContext.LUCENE) : header[i];
                    Class<?> type = observable != null ? observable.getType() : null;
                    Observable.Style style = observable != null ? observable.getStyle() : Observable.Style.CANONICAL;
                    if (type == Integer.class || (type == null && value.matches("\\d+"))) {
                        int iv = Integer.parseInt(value);
                        doc.add(new StoredField(field, iv));
                    } else if (type == Double.class || (type == null && value.matches("\\d+\\.\\d*"))) {
                        double dv = Double.parseDouble(value);
                        doc.add(new StoredField(field, dv));
                    } else {
                        if (observable != null && observable.getNormaliser() != null)
                            value = observable.getNormaliser().normalise(value);
                        switch (style) {
                            case IDENTIFIER:
                            case CANONICAL:
                                doc.add(new StringField(field, value, Field.Store.YES));
                                break;
                            default:
                                doc.add(new TextField(field, value, Field.Store.YES));
                                break;
                        }
                    }
                }
            }
            writer.addDocument(doc);
        }
        writer.commit();
        writer.close();
        this.indexReader = DirectoryReader.open(directory);
        this.searcher = new IndexSearcher(this.indexReader);
    }

}
