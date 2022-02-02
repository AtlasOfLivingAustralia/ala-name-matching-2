package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import au.org.ala.names.lucene.LuceneClassifierSearcherConfiguration;
import lombok.Getter;
import org.gbif.dwc.terms.DwcTerm;

import java.io.File;

/**
 * Name searching interface.
 * <p>
 * Searches for names based on an underlying ALA Linnaean classifier and a Lucene
 * name index.
 * </p>
 */
public class ALANameSearcher implements AutoCloseable {
    @Getter
    private LuceneClassifierSearcher searcher;
    @Getter
    private ALAClassificationMatcher matcher;
    @Getter
    private LuceneClassifierSearcher vernacularSearcher;
    @Getter
    private ALAVernacularClassificationMatcher vernacularMatcher;

    public ALANameSearcher(File index, File vernacular, LuceneClassifierSearcherConfiguration sConfig, ClassificationMatcherConfiguration cConfig) throws StoreException {
        this.searcher = new LuceneClassifierSearcher(index, sConfig);
        this.vernacularSearcher = new LuceneClassifierSearcher(vernacular, sConfig);
        this.matcher = new ALAClassificationMatcher(AlaLinnaeanFactory.instance(), this.searcher, cConfig);
        this.vernacularMatcher = new ALAVernacularClassificationMatcher(AlaVernacularFactory.instance(), this.vernacularSearcher, cConfig);
    }

    /**
     * Close the searcher
     */
    @Override
    public void close() throws Exception {
        this.matcher.close();
        this.vernacularMatcher.close();
        this.searcher.close();
        this.vernacularSearcher.close();
    }

    /**
     * Search for a classification, based on template classification data.
     *
     * @param template The template classification with various amounts of information filled in.
     *
     * @return The closest possible match.
     *
     * @throws BayesianException if unable to compuete match charactersics
      */
    public Match<AlaLinnaeanClassification, MatchMeasurement> search(AlaLinnaeanClassification template) throws BayesianException {
        return this.matcher.findMatch(template);
    }

    /**
     * Get classification by taxon identifier.
     *
     * @param taxonId The taxon identifier
     *
     * @return The matching classification or null for not found
     *
     * @throws BayesianException if there is an error retrieving the informstion
     */
    public AlaLinnaeanClassification get(String taxonId) throws BayesianException {
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
     * @throws BayesianException if unable to compuete match charactersics
      */
    public Match<AlaVernacularClassification, MatchMeasurement> search(AlaVernacularClassification template) throws BayesianException {
        return this.vernacularMatcher.findMatch(template);
    }

}
