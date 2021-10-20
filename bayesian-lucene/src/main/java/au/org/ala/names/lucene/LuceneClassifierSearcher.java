package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.gbif.dwc.terms.Term;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A searcher that searches a lucene index for possible candidates
 */
public class LuceneClassifierSearcher extends ClassifierSearcher<LuceneClassifier> {
    /** The maximum number of results to return */
    private int limit;
    /** The location of the lucene index */
    private Directory directory;
    /** The index reader */
    private IndexReader indexReader;
    /** The index searcher */
    private IndexSearcher searcher;
    /** Query utiltities */
    private QueryUtils queryUtils;

    /**
     * Construct for a path to the lucene index.
     *
     * @param path The path
     *
     * @throws StoreException if unable to open the index
     */
    public LuceneClassifierSearcher(Path path) throws StoreException {
        try {
            this.directory = FSDirectory.open(path);
            this.indexReader = DirectoryReader.open(this.directory);
            this.searcher = new IndexSearcher(this.indexReader);
            this.queryUtils = new QueryUtils();
            this.limit = 20;
        } catch (IOException ex) {
            throw new StoreException("Unable to open lucene index at " + path, ex);
        }
    }

    /**
     * Construct for a file to the lucene index directory.
     *
     * @param file The path
     *
     * @throws StoreException if unable to open the index
     */
    public LuceneClassifierSearcher(File file) throws StoreException {
        this(file.toPath());
    }

    /**
     * Search for a classifier by identifier.
     *
     * @param identifier The identifier
     * @return An optional result
     * @throws InferenceException if unable to infer information about the classifier
     * @throws StoreException     if unable to retrieve the classifier
     */
    @Override
    public LuceneClassifier get(Term type, Observable identifier, Object id) throws InferenceException, StoreException {
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
            Document document = this.indexReader.document(docs.scoreDocs[0].doc);
            return new LuceneClassifier(document);
        } catch (IOException ex) {
            throw new StoreException("Unable to retrive documents", ex);
        }
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
     * @throws InferenceException if unable to correctly match the classifiers
     * @throws StoreException     if unable to retrieve the results
     */
    @Override
    public List<LuceneClassifier> search(Classification classification) throws InferenceException, StoreException {
        Collection<Observation> criteria = classification.toObservations();
        BooleanQuery.Builder builder = this.queryUtils.createBuilder();
        builder.add(LuceneClassifier.getTypeClause(classification.getType()));
        String name = classification.getName();
        if (name != null)
            builder.add(this.queryUtils.nameClause(name));
        for (Observation observation: criteria)
            builder.add(this.queryUtils.asClause(observation, false));
        Query query = builder.build();
        List<LuceneClassifier> classifiers = null;
        try {
            TopDocs docs = this.searcher.search(query, this.limit);
            classifiers = new ArrayList<>(Math.min(this.limit, (int) docs.totalHits.value));
            for (ScoreDoc doc: docs.scoreDocs) {
                Document document = this.indexReader.document(doc.doc);
                classifiers.add(new LuceneClassifier(document));
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
