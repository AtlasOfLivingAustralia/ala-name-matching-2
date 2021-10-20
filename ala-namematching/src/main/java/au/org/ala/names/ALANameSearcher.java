package au.org.ala.names;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Match;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import org.gbif.dwc.terms.DwcTerm;

import java.io.File;

/**
 * Name searching interface.
 * <p>
 * Searches for names based on an underlying ALA Linnaean classifier and a Lucene
 * name index.
 * </p>
 */
public class ALANameSearcher {
    private LuceneClassifierSearcher searcher;
    private ALAClassificationMatcher matcher;
    private LuceneClassifierSearcher vernacularSearcher;
    private ALAVernacularClassificationMatcher vernacularMatcher;

    public ALANameSearcher(File index, File vernacular) throws StoreException {
        this.searcher = new LuceneClassifierSearcher(index);
        this.vernacularSearcher = new LuceneClassifierSearcher(vernacular);
        this.matcher = new ALAClassificationMatcher(AlaLinnaeanFactory.instance(), this.searcher);
        this.vernacularMatcher = new ALAVernacularClassificationMatcher(AlaVernacularFactory.instance(), this.vernacularSearcher);
    }

    /**
     * Close the searcher
     */
    public void close() throws StoreException {
        this.searcher.close();
    }

    /**
     * Search for a classification, based on template classification data.
     *
     * @param template The template classification with various amounts of information filled in.
     *
     * @return The closest possible match.
     *
     * @throws InferenceException if unable to compuete match charactersics
     * @throws StoreException if unable to read the index
     */
    public Match<AlaLinnaeanClassification> search(AlaLinnaeanClassification template) throws InferenceException, StoreException {
        return this.matcher.findMatch(template);
    }

    /**
     * Get classification by taxon identifier.
     *
     * @param taxonId The taxon identifier
     *
     * @return The matching classification or null for not found
     *
     * @throws StoreException if there is an error retrieving the informstion
     * @throws InferenceException if there is an error matching the information to the taxonId
     */
    public AlaLinnaeanClassification get(String taxonId) throws StoreException, InferenceException {
        LuceneClassifier classifier = this.searcher.get(DwcTerm.Taxon, AlaLinnaeanFactory.taxonId, taxonId);
        if (classifier == null)
            return null;
        AlaLinnaeanClassification classification = AlaLinnaeanFactory.instance().createClassification();
        classification.read(classifier, true);
        return classification;
    }


    /**
     * Search for a classification, based on template vernacular data.
     *
     * @param template The template classification with various amounts of information filled in.
     *
     * @return The closest possible match.
     *
     * @throws InferenceException if unable to compuete match charactersics
     * @throws StoreException if unable to read the index
     */
    public Match<AlaVernacularClassification> search(AlaVernacularClassification template) throws InferenceException, StoreException {
        return this.vernacularMatcher.findMatch(template);
    }

}
