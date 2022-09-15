package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.names.builder.LoadStore;
import au.org.ala.util.JsonUtils;
import au.org.ala.util.Metadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.gbif.dwc.terms.Term;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class LuceneLoadStore extends LoadStore<LuceneClassifier> {
    /** The default batch size for getting results. Assumes a smallish size */
    public static final int DEFAULT_BATCH_SIZE = 256;
    /** The name of the network description file */
    public static final String NETWORK_FILE = "network.json";

    /** The query utilities */
    private final QueryUtils queryUtils;
    /** The temporary directory for the loading index */
    private final Path dir;
    /** The load writer */
    private final IndexWriter writer;
    /** The load reader */
    private IndexReader reader;
    /** The load searcher */
    private IndexSearcher searcher;
    /** The batch size for gind all queries */
    @Getter
    @Setter
    private int batchSize = DEFAULT_BATCH_SIZE;
    /** Delete this store on closing */
    @Getter
    private final boolean temporary;
    /** The annotation observable */
    private final Observable annotationObservable;


    /**
     * Construct a load store.
     * <p>
     * This creates a store in a directory.
     * </p>
     *
     * @param name The name for this store
     * @param dir The store directory (if temporary is true, this is a work directory under which a temoporray directory is created, null for the standard temporary work area)
     * @param temporary Delete the store on closing (for temporary stores)
     * @param memory Prefer an in-memory store using {@Link MMapDirectory}
     * @param cacheSize The cache size (0 for default)
     */
    public LuceneLoadStore(String name, File dir, boolean temporary, boolean memory, int cacheSize) throws StoreException {
        super(name, cacheSize);
        this.temporary = temporary;
        this.queryUtils = new QueryUtils();
        FSDirectory directory = null;
        if (!temporary && dir ==  null)
            throw new IllegalArgumentException("Non-temporary store must have a directory supplied.");
        try {
            if (dir != null && !dir.exists())
                dir.mkdirs();
            if (temporary) {
                this.dir = dir == null ? Files.createTempDirectory("Load") : Files.createTempDirectory(dir.toPath(), "Load");
            } else {
                this.dir = dir == null ? Files.createTempDirectory("Load") : dir.toPath();
            }
            directory = MMapDirectory.open(this.dir);
        } catch (IOException ex) {
            throw new StoreException("Unable to get store directory", ex);
        }
        IndexWriterConfig config = new IndexWriterConfig(this.queryUtils.getAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try {
            this.writer = new IndexWriter(directory, config);
        } catch (IOException ex) {
            throw new StoreException("Unable to make index in " + directory, ex);
        }
        this.annotationObservable = Observable.string(LuceneClassifier.ANNOTATION_FIELD);
        this.annotationObservable.setExternal(ExternalContext.LUCENE, LuceneClassifier.ANNOTATION_FIELD);
        log.info("Lucene load store " + this.getName() + " created at " + directory);
    }

    /**
     * Construct a temporary load store.
     * <p>
     * This creates a store in a temporary work directory.
     * </p>
     *
     * @param name The name for this store
     * @param work The work directory
     */
    public LuceneLoadStore(String name, File work) throws StoreException {
        this(name, work, true, true, 0);
    }

    /**
     * Get the store location
     *
     * @return The path to the store
     */
    @Override
    public String getLocation() {
        return this.dir.toString();
    }

    /**
     * Get the observable that allows us to find annotations.
     *
     * @return The annotation observable
     */
    @Override
    public Observation getAnnotationObservation(Term annotation) {
        return new Observation(true, this.annotationObservable, Classifier.getAnnotationValue(annotation));
    }

    /**
     * Create a new, empty classifier
     *
     * @return The new classifier
     */
    @Override
    protected LuceneClassifier doNewClassifier() {
        return new LuceneClassifier();
    }

    /**
     * Store an entry in the load store
     *
     * @param classifier The collection of information that makes up the entry
     *
     * @throws StoreException if unable to write the document
     */
    @Override
    synchronized protected void doStore(LuceneClassifier classifier) throws StoreException {
        try {
            this.writer.addDocument(classifier.isRetrieved() ? classifier.makeDocumentCopy() : classifier.getDocument());
        } catch (IOException ex) {
            log.error("Unable to store " + classifier.getIdentifier());
            for (String[] vals: classifier.getAllValues()) {
                log.info(vals[0] + ": " + IntStream.range(1, vals.length).mapToObj(i -> vals[i]).collect(Collectors.joining(", ")));
            }
            throw new StoreException("Unable to store " + classifier, ex);
        }
    }

    /**
     * Store an entry in the load store
     *
     * @param classifier The collection of information that makes up the entry
     * @param type The document type
     *
     * @throws BayesianException if unable to annotate or store the classifier
     */
    @Override
    synchronized protected void doStore(LuceneClassifier classifier, @NonNull Term type) throws BayesianException {
         try {
            classifier.identify();
            classifier.setType(type);
            this.writer.addDocument(classifier.getDocument());
        } catch (Exception ex) {
            log.error("Unable to store " + classifier.getIdentifier());
            for (String[] vals: classifier.getAllValues()) {
                log.info(vals[0] + ": " + IntStream.range(1, vals.length).mapToObj(i -> vals[i]).collect(Collectors.joining(", ")));
            }
            throw new StoreException("Unable to store " + classifier, ex);
        }
    }

    /**
     * Update an entry in the load store
     *
     * @param classifier The collection of information that makes up the entry
     */
    @Override
    synchronized protected void doUpdate(LuceneClassifier classifier) throws StoreException {
        try {
            String id = classifier.getIdentifier();
            if (id == null || id.isEmpty())
                throw new StoreException("No identifier for " + classifier);
            org.apache.lucene.index.Term term = new org.apache.lucene.index.Term(LuceneClassifier.ID_FIELD, id);
            this.writer.updateDocument(term, classifier.makeDocumentCopy());
        } catch (IOException ex) {
            log.error("Unable to store " + classifier.getIdentifier());
            for (String[] vals: classifier.getAllValues()) {
                log.info(vals[0] + ": " + IntStream.range(1, vals.length).mapToObj(i -> vals[i]).collect(Collectors.joining(", ")));
            }
            throw new StoreException("Unable to store " + classifier, ex);
        }
    }

    @Override
    public void store(Metadata metadata) throws StoreException {
        try {
            ObjectMapper mapper = JsonUtils.createMapper();
            File store = new File(this.dir.toFile(), LuceneClassifierSearcher.METADATA_FILE);
            mapper.writeValue(store, metadata);
        } catch (Exception ex) {
            throw new StoreException("Unable to write metadata", ex);
        }
    }

    @Override
    public void store(Network network) throws StoreException {
        try {
            ObjectMapper mapper = JsonUtils.createMapper();
            File store = new File(this.dir.toFile(), NETWORK_FILE);
            mapper.writeValue(store, network);
        } catch (Exception ex) {
            throw new StoreException("Unable to write network", ex);
        }
    }

    /**
     * Get an entry from the underlying store, assuming term has unique values.
     *
     * @param observable The term to search
     * @param value The value the term has
     *
     * @return The uniquely matching entry
     *
     * @throws StoreException
     */
    @Override
    protected LuceneClassifier doGet(Term type, Observable observable, String value) throws StoreException {
        this.ensureReader();
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(LuceneClassifier.getTypeClause(type));
            builder.add(this.queryUtils.asClause(observable, value));
            Query query = builder.build();
            TopDocs docs = this.searcher.search(query, 1);
            if (docs.totalHits.value == 0)
                return null;
            if (docs.totalHits.value > 1)
                throw new StoreException("More than one answer for " + observable.getId() + " = " + value);
            return new LuceneClassifier(this.searcher.doc(docs.scoreDocs[0].doc));
        } catch (IOException ex) {
            throw new StoreException("Unable to search for " + observable.getId() + " = " + value, ex);
        }
    }

    /**
     * Get all the entries from the underlying store, assuming term has unique values.
     *
     * @param type The object type
     * @param values A list of conditions in observab;e/value format
     *
     * @return The uniquely matching entry
     *
     * @throws StoreException
     */
    @Override
    protected Iterable<LuceneClassifier> doGetAll(Term type, Observation... values) throws StoreException {
         this.ensureReader();
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(LuceneClassifier.getTypeClause(type));
            for (Observation o: values) {
                builder.add(this.queryUtils.asClause(o));
            }
            return new LuceneIterator(builder.build());
        } catch (IOException ex) {
            throw new StoreException("Unable to search for " + values, ex);
        }
    }

    /**
     * Provide a count of classifiers that will be returned by a {@link #getAll(Term, Observation...)} query.
     *
     * @param type   The type of document
     * @param values The conditions for the count
     * @return The number of matching classifiers
     */
    @Override
    public int count(Term type, Observation... values) throws StoreException {
        this.ensureReader();
        try {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(LuceneClassifier.getTypeClause(type));
            for (Observation o: values) {
                builder.add(this.queryUtils.asClause(o));
            }
            return this.searcher.count(builder.build());
        } catch (IOException ex) {
            throw new StoreException("Unable to search for " + values, ex);
        }
    }

    /**
     * Get a parameter analyser for this store.
     *
     * @return The parameter analyser
     *
     * @throws BayesianException if unable to build an analyser
     */
    @Override
    public ParameterAnalyser getParameterAnalyser(Network network, Observable weight, double defaultWeight) throws BayesianException {
        this.ensureReader();
        return new LuceneParameterAnalyser(network, this.searcher, weight, defaultWeight, network.getInputs(), network.getOutputs(), this.getCacheSize(), true);
    }

    /**
     * Commit a loaded set of data.
     *
     * @throws StoreException if unable to commit the underlying lucene index
     */
    @Override
    synchronized public void commit() throws StoreException {
        try {
            this.writer.commit();
            if (this.reader != null) {
                this.reader.close();
                this.reader = null; // Forces re-open
                this.searcher = null;
            }
        } catch (IOException ex) {
            throw new StoreException("Unable to commit to store", ex);

        }
    }

    /**
     * Close the store.
     * <p>
     * This also deletes the store itself on the fie system.
     * </p>
     *
     * @throws StoreException if unable to close the underlying lucene index
     */
    @Override
    synchronized public void close() throws StoreException {
        try {
            if (this.writer != null)
                this.writer.close();
            if (this.reader != null)
                this.reader.close();
            if (this.temporary)
                FileUtils.deleteDirectory(this.dir.toFile());
        } catch (IOException ex) {
            throw new StoreException("Unable to close store", ex);
        }
    }


    /**
     * Ensure that we have an open reader.
     *
     * @throws StoreException if not able to ensure the reader
     */
    protected void ensureReader() throws StoreException {
        if (this.reader == null) {
            try {
                this.reader = DirectoryReader.open(this.writer);
                this.searcher = new IndexSearcher(this.reader);
            } catch (IOException ex) {
                throw new StoreException("Unable to open reader", ex);
            }
        }
    }

    protected class LuceneIterator implements Iterable<LuceneClassifier>, Iterator<LuceneClassifier> {
        /** The query to iterate over */
        private final Query query;
         /** The current index into the results */
        private int resultIndex;
        /** The running count of results returned */
        private long index;
        /** The current set of documents */
        private TopDocs topDocs;

        public LuceneIterator(Query query) throws IOException {
            this.query = query;
            this.index = 0;
            this.resultIndex = 0;
            this.topDocs = LuceneLoadStore.this.searcher.search(this.query, LuceneLoadStore.this.batchSize);
        }

        /**
         * Check to see if we have any more documents to return.
         * @return
         */
        @Override
        public boolean hasNext() {
             return this.index < this.topDocs.totalHits.value;
        }

        @Override
        public LuceneClassifier next() {
            try {
                if (this.resultIndex >= this.topDocs.scoreDocs.length) {
                    this.resultIndex = 0;
                    this.topDocs = LuceneLoadStore.this.searcher.searchAfter(this.topDocs.scoreDocs[this.topDocs.scoreDocs.length - 1], this.query, LuceneLoadStore.this.batchSize);
                    if (this.topDocs.scoreDocs.length == 0)
                        throw new IndexOutOfBoundsException("Expecting another document");
                }
                this.index++;
                return new LuceneClassifier(LuceneLoadStore.this.searcher.doc(this.topDocs.scoreDocs[this.resultIndex++].doc));
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to get next document", ex);
             }
        }

        @Override
        public Iterator<LuceneClassifier> iterator() {
            return this;
        }
    }
}
