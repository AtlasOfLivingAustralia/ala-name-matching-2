package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.names.builder.Annotator;
import au.org.ala.names.builder.LoadStore;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.model.ExternalContext;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.gbif.dwc.terms.Term;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class LuceneLoadStore extends LoadStore<LuceneClassifier> {
    /** The default batch size for getting results. Assumes a smallish size */
    public static final int DEFAULT_BATCH_SIZE = 256;

    /** The query utilities */
    private QueryUtils queryUtils;
    /** Analyser to use for load analysis */
    private Analyzer analyzer;
    /** The temporary directory for the loading index */
    private Path dir;
    /** The load writer */
    private IndexWriter writer;
    /** The load reader */
    private IndexReader reader;
    /** The load searcher */
    private IndexSearcher searcher;
    /** The batch size for gind all queries */
    private int batchSize;
    /** Delete this store on closing */
    private boolean temporary;
    /** The annotation observable */
    private Observable annotationObservable;


    /**
     * Construct a load store.
     * <p>
     * This creates a store in a directory.
     * </p>
     *
     * @param annotator The annotator for this store
     * @param dir The store directory (if temporary is true, this is a work directory under which a temoporray directory is created, null for the standard temporary work area)
     * @param temporary Delete the store on closing (for temporary stores)
     */
    public LuceneLoadStore(Annotator annotator, File dir, boolean temporary) throws StoreException {
        super(annotator);
        this.temporary = temporary;
        this.queryUtils = new QueryUtils();
        this.analyzer = new KeywordAnalyzer();
        FSDirectory directory = null;
        if (!temporary && dir ==  null)
            throw new IllegalArgumentException("Non-temporary store must have a directory supplied.");
        try {
            if (temporary) {
                this.dir = dir == null ? Files.createTempDirectory("Load") : Files.createTempDirectory(dir.toPath(), "Load");
            } else {
                this.dir = dir == null ? Files.createTempDirectory("Load") : dir.toPath();
            }
            directory = FSDirectory.open(this.dir);
        } catch (IOException ex) {
            throw new StoreException("Unable to get store directory", ex);
        }
        IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try {
            this.writer = new IndexWriter(directory, config);
        } catch (IOException ex) {
            throw new StoreException("Unable to make index in " + directory, ex);
        }
        this.batchSize = DEFAULT_BATCH_SIZE;
        this.annotationObservable = new Observable(LuceneClassifier.ANNOTATION_FIELD);
        this.annotationObservable.setExternal(ExternalContext.LUCENE, LuceneClassifier.ANNOTATION_FIELD);
    }

    /**
     * Construct a temporary load store.
     * <p>
     * This creates a store in a temporary work directory.
     * </p>
     *
     * @param annotator The annotator for this store
     * @param work The work directory
     */
    public LuceneLoadStore(Annotator annotator, File work) throws StoreException {
        this(annotator, work, true);
    }

    /**
     * Get the batch size for multiple results
     *
     * @return The batch size
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Set the match size for returning multiple results.
     *
     * @param batchSize The new batch size
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Get the observable that allows us to find annotations.
     *
     * @return The annotation observable
     */
    @Override
    public Observation getAnnotationObservation(Term annotation) {
        return new Observation(true, this.annotationObservable, LuceneClassifier.getAnnotationValue(annotation));
    }

    /**
     * Create a new, empty classifier
     *
     * @return The new classifier
     */
    @Override
    public LuceneClassifier newClassifier() {
        return new LuceneClassifier();
    }

    /**
     * Store an entry in the load store
     *
     * @param classifier The collection of information that makes up the entry
     * @param type The document type
     */
    @Override
    public void store(LuceneClassifier classifier, Term type) throws StoreException {
         try {
            classifier.identify();
            classifier.setType(type);
            this.annotator.annotate(classifier);
            this.writer.addDocument(classifier.getDocument());
        } catch (IOException ex) {
            throw new StoreException("Unable to store " + classifier, ex);
        }
    }

    /**
     * Update an entry in the load store
     *
     * @param classifier The collection of information that makes up the entry
     */
    @Override
    public void update(LuceneClassifier classifier) throws StoreException {
        try {
            String id = classifier.getIdentifier();
            if (id == null || id.isEmpty())
                throw new StoreException("No identifier for " + classifier);
            org.apache.lucene.index.Term term = new org.apache.lucene.index.Term(LuceneClassifier.ID_FIELD, id);
            this.writer.updateDocument(term, classifier.getDocument());
        } catch (IOException ex) {
            throw new StoreException("Unable to store " + classifier, ex);
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
    public LuceneClassifier get(Term type, Observable observable, String value) throws StoreException {
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
    public Iterable<LuceneClassifier> getAll(Term type, Observation... values) throws StoreException {
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
     * Get a parameter analyser for this store.
     *
     * @return The parameter analyser
     */
    @Override
    public ParameterAnalyser getParameterAnalyser(Network network, Observable weight, double defaultWeight) throws InferenceException, StoreException {
        this.ensureReader();
        return new LuceneParameterAnalyser(network, this.annotator, this.searcher, weight, defaultWeight);
    }

    /**
     * Commit a loaded set of data.
     *
     * @throws StoreException if unable to commit the underlying lucene index
     */
    @Override
    public void commit() throws StoreException {
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
    public void close() throws StoreException {
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
        private Query query;
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
