package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.Observable;
import au.org.ala.util.FileUtils;
import au.org.ala.vocab.BayesianTerm;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.apache.commons.text.similarity.LongestCommonSubsequenceDistance;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.*;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.suggest.DocumentDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.gbif.dwc.terms.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LuceneClassifierSuggester extends ClassifierSuggester<LuceneClassifier> {
    private static final Logger logger = LoggerFactory.getLogger(LuceneClassifierSuggester.class);

    /** The name of the suggester store file */
    protected static final String SUGGESTER_STORE = "suggester.bin";

    /** The name field for the temporary index */
    public static final String NAME_FIELD = "name";
    /** The identifier field for the temporary index */
    public static final String ID_FIELD = "id";
    /** The weight field for the temporary index */
    public static final String WEIGHT_FIELD = "weight";
    /** The synonym flag */
    public static final String SYNONYM_FIELD = "synonym";
    /** The type field */
    public static final String TYPE_FIELD = "type";

    /** The source of actual classifications */
    private LuceneClassifierSearcher base;
    /** The factory for the base classifications */
    private NetworkFactory baseFactory;
    /** The suggester analyser */
    private Analyzer analyzer;
    /** The suggester */
    private AnalyzingInfixSuggester suggester;
    /** The suggester store directory */
    private FSDirectory directory;
    /** How to measure distance from the query */
    private EditDistance<Integer> distance;
    /** The work directory */
    private File workDirectory = null;
    /** The index writer for building the temporary index */
    private IndexWriter workWriter = null;
    /** The lucene utilities */
    private QueryUtils utils;

    /**
     * Construct an empty suggester with a source of truth.
     *
     * @param directory The directory to store the suggestion index in
     * @param base The searcher
     * @param baseFactory The base description
     *
     * @throws StoreException if unable to make the suggester
     */
    public LuceneClassifierSuggester(FSDirectory directory, LuceneClassifierSearcher base, NetworkFactory baseFactory) throws StoreException {
        try {
            this.directory = directory;
            this.base = base;
            this.baseFactory = baseFactory;
            if (!this.baseFactory.getIdentifier().isPresent())
                throw new IllegalArgumentException("Network " + this.baseFactory.getNetworkId() + " must have an identifier");
            this.utils = new QueryUtils();
            this.analyzer = this.utils.createSuggesterAnalyzer();
            this.suggester = new BlendedInfixSuggester(
                    this.directory,
                    this.analyzer
            );
            logger.info("Suggester " + this.suggester + " in " + this.directory + " has " + this.suggester.getCount() + " entries");
            this.distance = new LongestCommonSubsequenceDistance();
        } catch (Exception ex) {
            throw new StoreException("Unable to set up suggester", ex);
        }
    }

    /**
     * Start construction of a suggester.
     *
     * @see #add(LuceneClassifierSearcher, Observable, Observable)
     *
     * @throws StoreException if unable to construct the work indexes
     */
    public void start() throws StoreException {
        try {
            this.workDirectory = FileUtils.makeTmpDir("suggest");
            logger.info("Creating temporary index at " + this.workDirectory);
            IndexWriterConfig config = new IndexWriterConfig(this.utils.createAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            this.workWriter = new IndexWriter(FSDirectory.open(this.workDirectory.toPath()), config);
        } catch (IOException ex) {
            throw new StoreException("Unable to start index building");
        }
    }

    /**
     * Add a source of lookups.
     * <p>
     * This method allows the building of a suggester from multiple sources.
     * To use this, start with {@link #start()},
     * add a number of sources with {@link #add(LuceneClassifierSearcher, Observable, Observable)}
     * and then finish it off with {@link #create()}
     * </p>
     *
     * @param source The source searcher
     * @param key The observable that generates the lookup key
     *
     * @throws IOException if unable to add the source
     */
    public void add(LuceneClassifierSearcher source, Observable<String> key, Function<LuceneClassifier, Double> weighter) throws StoreException {
        logger.info("Adding source " + source.getDirectory() + " with key " + key);
        IndexReader sr = source.getIndexReader();
        if (sr.hasDeletions())
            throw new StoreException("Unable to handle deleted documents");
        String synonymAnnotation = BayesianTerm.isSynonym.qualifiedName();
        try {
            for (int i = 0; i < sr.maxDoc(); i++) {
                Document doc = sr.document(i);
                LuceneClassifier classifier = new LuceneClassifier(doc);
                String id = classifier.get(key);
                if (id == null)
                    continue;
                Document suggest = new Document();
                suggest.add(new StoredField(ID_FIELD, id));
                suggest.add(new StoredField(TYPE_FIELD, classifier.getType().qualifiedName()));
                for (String name: classifier.getNames()) {
                    suggest.add(new StringField(NAME_FIELD, name, Field.Store.YES));
                }
                if (weighter != null) {
                    double weight = weighter.apply(classifier);
                    suggest.add(new StoredField(WEIGHT_FIELD, weight));
                }
                boolean synonym = false;
                for (IndexableField f: doc.getFields(LuceneClassifier.ANNOTATION_FIELD)) {
                    synonym = synonym || synonymAnnotation.equals(f.stringValue());
                }
                suggest.add(new StoredField(SYNONYM_FIELD, synonym ? "T" : "F"));
                this.workWriter.addDocument(suggest);
            }
            logger.info("Added " + sr.maxDoc() + " entries");
            this.workWriter.commit();
        } catch (IOException ex) {
            throw new StoreException("Unable to generate suggestion", ex);
        }
    }

    /**
     * Create the suggester form the work index
     *
     * @see #add(LuceneClassifierSearcher, Observable, Observable)
     *
     * @throws StoreException
     */
    public void create() throws StoreException {
        if (this.workWriter == null || this.workDirectory == null) {
            throw new StoreException("Unable to create. Work index has not been built");
        }
        logger.info("Building suggester from work index " + this.workDirectory);
        try {
            this.workWriter.commit();
            this.workWriter.close();
            FSDirectory dir = FSDirectory.open(this.workDirectory.toPath());
            IndexReader reader = DirectoryReader.open(dir);
            DocumentDictionary dictionary = new DocumentDictionary(
                    reader,
                    NAME_FIELD,
                    WEIGHT_FIELD,
                    ID_FIELD,
                    LuceneClassifier.TYPE_FIELD
            );
            this.suggester.build(dictionary);
            this.suggester.commit();
            logger.info("Suggester contains " + this.suggester.getCount() + " entries");
            dir.close();
            FileUtils.deleteAll(this.workDirectory);
            this.workDirectory = null;
            this.workWriter = null;
        } catch (IOException ex) {
            throw new StoreException("Unable to create from " + this.workWriter, ex);
        }
    }

    /**
     * Try to load a pre-built suggester
     *
     * @return True if loaded successfully
     *
     * @throws IOException if unable to read the suggester
     */
    public boolean load() throws IOException {
        File store = new File(this.directory.getDirectory().toFile(), SUGGESTER_STORE);
        if (!store.exists())
            return false;
        logger.info("Found pre-existing " + store + " with " + this.suggester.getCount() + " entries");
        return true;
    }

    /**
     * Store the pre-built suggester
     *
     * @throws IOException if unable to write the suggester
     */
    public void store() throws IOException {
        File store = new File(this.directory.getDirectory().toFile(), SUGGESTER_STORE);
        logger.info("Creating store " + store);
        store.createNewFile();
     }

    /**
     * Build a suggestion list for a fragment of text.
     *
     * @param fragment        The fragment
     * @param size            The number of results to return
     * @return A list of classifiers that match the fragment
     * @throws BayesianException
     */
    @Override
    public List<Suggestion<LuceneClassifier>> suggest(String fragment, int size, boolean includeSynonyms) throws BayesianException {
        int gets = Math.max(size * 2, 10);
        List<Suggestion<LuceneClassifier>> suggestions = new ArrayList<>(gets);
        try {
            List<Lookup.LookupResult> results = this.suggester.lookup(fragment.toLowerCase(), false, gets);
            Term baseType = this.baseFactory.getConcept();
            Optional<Observable<String>> baseIdentifier = this.baseFactory.getIdentifier(); // Presence checked on construction
            Optional<Observable<String>> synonymIdentifier = this.baseFactory.getAccepted();
            for (Lookup.LookupResult result : results) {
                LuceneClassifier classifier = this.base.get(baseType, baseIdentifier.get(), result.payload.utf8ToString());
                LuceneClassifier match = classifier;
                LuceneClassifier synonym = null;
                if (classifier != null) {
                    if (synonymIdentifier.isPresent()) {
                        String acceptedId = classifier.get(synonymIdentifier.get());
                        if (acceptedId != null) {
                            if (!includeSynonyms)
                                continue;
                            match = this.base.get(baseType, baseIdentifier.get(), acceptedId);
                            synonym = classifier;
                        }
                    }
                }
                if (match != null) {
                    String actual = result.key.toString();
                    Term type = match.getType();
                    double score = 1.0 - ((double) this.distance.apply(fragment.toLowerCase(), actual.toLowerCase())) / Math.max(fragment.length(), actual.length());
                    suggestions.add(new Suggestion<LuceneClassifier>(score, result.key.toString(), type, match, synonym));
                }
            }
        } catch (IOException ex) {
            throw new StoreException("Unable to search for " + fragment, ex);
        }
        suggestions = suggestions.stream().sorted(Suggestion.SCORE_ORDER).limit(size).collect(Collectors.toList());
        return suggestions;
    }

    /**
     * Close the resource.
     *
     * @throws Exception if unable to close
     */
    @Override
    public void close() throws Exception {
        this.suggester.close();
    }
}
