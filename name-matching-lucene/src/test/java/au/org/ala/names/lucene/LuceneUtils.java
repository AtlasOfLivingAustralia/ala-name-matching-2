package au.org.ala.names.lucene;

import au.org.ala.util.TestUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Wrap a collection of useful lucene bits and pieces
 */
public class LuceneUtils {
    private Path indexDir;
    private IndexReader indexReader;
    private IndexSearcher searcher;

    /**
     * Create a lucene utility connection for a resourced CSV file containing index terms
     *
     * @param clazz The class to load from
     * @param resource The resource to load (CSV file)
     *
     * @throws Exception if unable to load the resource
     */
    public LuceneUtils(Class clazz, String resource) throws Exception {
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
        Directory directory = FSDirectory.open(this.indexDir);
        IndexWriterConfig config = new IndexWriterConfig();
        IndexWriter writer = new IndexWriter(directory, config);
        Reader r = TestUtils.getResourceReader(clazz, resource);
        CSVReader reader = new CSVReaderBuilder(r).build();
        String[] header = reader.readNext();
        for (String[] line: reader) {
            Document doc = new Document();
            for (int i = 0; i < header.length; i++) {
                if (i < line.length && line[i] != null && !line[i].isEmpty()) {
                    if (line[i].matches("\\d+")) {
                        int value = Integer.parseInt(line[i]);
                        doc.add(new StoredField(header[i], value));
                    } else if (line[i].matches("\\d+\\.\\d*")) {
                        double value = Double.parseDouble(line[i]);
                        doc.add(new StoredField(header[i], value));
                    } else {
                        doc.add(new StringField(header[i], line[i], Field.Store.YES));
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
