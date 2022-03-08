package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.analysis.TermAnalysis;
import au.org.ala.vocab.BayesianTerm;
import au.org.ala.vocab.OptimisationTerm;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.apache.lucene.search.suggest.DocumentDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.FuzzySuggester;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.extra.jmx.JmxSupport;
import org.gbif.dwc.terms.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A searcher that searches a lucene index for possible candidates.
 */
public class LuceneClassifierSearcher extends ClassifierSearcher<LuceneClassifier> implements LuceneClassifierSearcherMXBean {
    private static final Logger logger = LoggerFactory.getLogger(LuceneClassifierSearcher.class);

    /** Batch size for retrieving all values */
    private static final int BATCH_SIZE = 20;

    /** The searcher configuration */
    private LuceneClassifierSearcherConfiguration config;
    /** The location of the lucene index */
    @Getter(AccessLevel.PACKAGE)
    private Directory directory;
    /** The index reader */
    @Getter(AccessLevel.PACKAGE)
    private IndexReader indexReader;
    /** The index searcher */
    private IndexSearcher searcher;
    /** The suggester for auto-lookup */
    private Lookup suggester;
    /** Query utiltities */
    private QueryUtils queryUtils;
    /** The classifier cache */
    private Cache<Integer, LuceneClassifier> classifierCache;
    /** JMX registration */
    private ObjectInstance mbean;
    /** The get count */
    private AtomicLong gets = new AtomicLong();
    /** The query count */
    private AtomicLong queries = new AtomicLong();

    /**
     * Construct for a path to the lucene index.
     *
     * @param path The path
     * @param config The searcher configuration (null for a default value)
     *
     * @throws StoreException if unable to open the index
     */
    public LuceneClassifierSearcher(Path path, LuceneClassifierSearcherConfiguration config) throws StoreException {
        this.config = config == null ? LuceneClassifierSearcherConfiguration.builder().build() : config;
        String name = path.getFileName().toString();
        try {
            this.directory = FSDirectory.open(path);
            this.indexReader = DirectoryReader.open(this.directory);
            this.searcher = new IndexSearcher(this.indexReader);
            this.queryUtils = new QueryUtils();
            if (this.config.isCache()) {
                Cache2kBuilder<Integer, LuceneClassifier> builder = Cache2kBuilder.of(Integer.class, LuceneClassifier.class)
                        .name(name)
                        .permitNullValues(true)
                        .entryCapacity(this.config.getCacheSize())
                        .loader(this::doGet);
                if (this.config.isEnableJmx())
                    builder.enable(JmxSupport.class);
                this.classifierCache = builder.build();
            } else {
                this.classifierCache = null;
            }
            if (this.config.isEnableJmx()) {
                try {
                    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                    ObjectName on = new ObjectName(this.getClass().getPackage().getName() + ":type=" + this.getClass().getSimpleName() + ",name=" + name);
                    this.mbean = mbs.registerMBean(this, on);
                } catch (Exception ex) {
                    logger.error("Unable to register searcher " + name, ex);
                }
            }
        } catch (IOException ex) {
            throw new StoreException("Unable to open lucene index at " + path, ex);
        }
    }

    /**
     * Construct for a file to the lucene index directory.
     *
     * @param file The path
     * @param config The searcher configuration (null for a default value)
     *
     * @throws StoreException if unable to open the index
     */
    public LuceneClassifierSearcher(File file, LuceneClassifierSearcherConfiguration config) throws StoreException {
        this(file.toPath(), config);
    }

    /**
     * Get the number of individual document retrievals
     *
     * @return The get count
     */
    @Override
    public long getGets() {
        return this.gets.get();
    }

    /**
     * Get the number of queries
     *
     * @return The query count
     */
    @Override
    public long getQueries() {
        return this.queries.get();
    }

    /**
     * Search for a classifier by identifier.
     *
     * @param identifier The identifier
     *
     * @return An optional result
     *
     * @throws BayesianException if unable to retrieve or infer information about the classifier
     */
    @Override
    public LuceneClassifier get(Term type, Observable identifier, Object id) throws BayesianException {
        this.gets.incrementAndGet();
        BooleanQuery.Builder builder = this.queryUtils.createBuilder();
        builder.add(LuceneClassifier.getTypeClause(type));
        builder.add(
                this.queryUtils.asQuery(
                        identifier.getExternal(ExternalContext.LUCENE),
                        identifier.getStyle(),
                        identifier.getNormaliser(),
                        identifier.getAnalysis(),
                        id),
                BooleanClause.Occur.MUST);
        try {
            TopDocs docs = this.searcher.search(builder.build(), 1);
            if (docs.totalHits.value == 0)
                return null;
            if (docs.totalHits.value > 1)
                throw new StoreException("Multiple matches for identifier " + id);
            int docID = docs.scoreDocs[0].doc;
            return this.classifierCache == null ? this.doGet(docID) : this.classifierCache.get(docID);
        } catch (IOException ex) {
            throw new StoreException("Unable to retrive documents", ex);
        }
    }

    /**
     * Search for classifiers by identifier.
     *
     * @param identifier The identifier
     *
     * @return The results in retrieval order
     *
     * @throws BayesianException if unable to retrieve or infer information about the classifier
     */
    @Override
    public List<LuceneClassifier> getAll(Term type, Observable identifier, Object id) throws BayesianException {
        this.gets.incrementAndGet();
        List<LuceneClassifier> results = new ArrayList<>(BATCH_SIZE);
        BooleanQuery.Builder builder = this.queryUtils.createBuilder();
        builder.add(LuceneClassifier.getTypeClause(type));
        builder.add(
                this.queryUtils.asQuery(
                        identifier.getExternal(ExternalContext.LUCENE),
                        identifier.getStyle(),
                        identifier.getNormaliser(),
                        identifier.getAnalysis(),
                        id),
                BooleanClause.Occur.MUST);
        Query query = builder.build();
        try {
            TopDocs docs = this.searcher.search(query, BATCH_SIZE);
            while (docs.scoreDocs.length > 0) {
                for (ScoreDoc doc: docs.scoreDocs) {
                    results.add(this.classifierCache == null ? this.doGet(doc.doc) : this.classifierCache.get(doc.doc));
                }
                docs = this.searcher.searchAfter(docs.scoreDocs[docs.scoreDocs.length - 1], query, BATCH_SIZE);
            }
        } catch (IOException ex) {
            throw new StoreException("Unable to retrive documents", ex);
        }
        return results;
    }

    /**
     * Cache get for a document.
     *
     * @param docID The document ID
     *
     * @return The classifier from the index.
     */
    protected LuceneClassifier doGet(int docID) throws IOException {
        Document document = this.indexReader.document(docID);
        return new LuceneClassifier(document);
    }

    /**
     * Search for a set of possible candidate classifiers that match the supplied classification.
     * <p>
     * This uses a lucene
     * </p>
     *
     * @param classification The classification
     *
     * @return A list of potential classifiers
     *
     * @throws StoreException     if unable to retrieve the results
     */
    @Override
    public List<LuceneClassifier> search(Classification classification) throws StoreException {
        this.queries.incrementAndGet();
        Collection<Observation> criteria = classification.toObservations();
        BooleanQuery.Builder builder = this.queryUtils.createBuilder();
        builder.add(LuceneClassifier.getTypeClause(classification.getType()));
        String name = classification.getName();
        if (name != null)
            builder.add(this.queryUtils.nameClause(name));
        for (Observation observation: criteria) {
            if (!observation.getObservable().hasProperty(OptimisationTerm.luceneNoSearch, true)) {
                try {
                    Object bv = observation.getObservable().getProperty(OptimisationTerm.luceneBoost);
                    float boost = bv != null && bv instanceof Number ? ((Number) bv).floatValue() : 1.0f;
                    builder.add(this.queryUtils.asClause(observation, false, boost));
                } catch (Exception ex) {
                    this.logger.error("Unable to add claause for " + observation, ex);
                    throw new StoreException("Unable to add claause for " + observation, ex);
                }
            }
        }
        Query query = builder.build();
        List<LuceneClassifier> classifiers = null;
        try {
            TopDocs docs = this.searcher.search(query, this.config.getQueryLimit());
            float cutoff = this.config.getScoreCutoff();
            classifiers = new ArrayList<>(Math.min(this.config.getQueryLimit(), (int) docs.totalHits.value));
            for (ScoreDoc doc: docs.scoreDocs) {
                if (doc.score < cutoff)
                    continue;
                classifiers.add(this.classifierCache == null ? this.doGet(doc.doc) : this.classifierCache.get(doc.doc));
            }
        } catch (IOException ex) {
            throw new StoreException("Unable to retrive documents", ex);
        }
        return classifiers;
    }

    /**
     * Close the connection.
     *
     * @throws StoreException if unable to close for some reason.
     */
    @Override
    public void close() throws StoreException {
        try {
            if (this.mbean != null) {
                try {
                    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                    mbs.unregisterMBean(this.mbean.getObjectName());
                } catch (Exception ex) {
                    logger.error("Unable to deregister searcher " + mbean.getObjectName(), ex);
                }
            }
            if (this.classifierCache != null) {
                this.classifierCache.close();
            }
            this.searcher = null;
            if (this.indexReader != null) {
                this.indexReader.close();
                this.indexReader = null;
            }
            if (this.directory != null) {
                this.directory.close();
                this.directory = null;
            }
        } catch (IOException ex) {
           throw new StoreException("Unable to close lucene searcher at " + this.directory, ex);
        }
    }
}
