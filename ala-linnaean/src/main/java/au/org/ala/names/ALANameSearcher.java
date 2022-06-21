package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import au.org.ala.names.lucene.LuceneClassifierSearcherConfiguration;
import au.org.ala.names.lucene.LuceneClassifierSuggester;
import au.org.ala.vocab.BayesianTerm;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.EditDistance;
import org.apache.lucene.store.FSDirectory;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.nameparser.api.Rank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Name searching interface.
 * <p>
 * Searches for names based on an underlying ALA Linnaean classifier and a Lucene
 * name index.
 * </p>
 */
public class ALANameSearcher implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ALANameSearcher.class);

    @Getter
    private final LuceneClassifierSearcher searcher;
    @Getter
    private final ALAClassificationMatcher matcher;
    @Getter
    private final LuceneClassifierSearcher vernacularSearcher;
    @Getter
    private final ALAVernacularClassificationMatcher vernacularMatcher;
    private final File suggesterDir;
    @Getter(lazy = true)
    private final LuceneClassifierSuggester suggester = buildSuggester();
    private final RankAnalysis rankAnalysis;

    public ALANameSearcher(File index, File vernacular, File suggesterDir, LuceneClassifierSearcherConfiguration sConfig, ClassificationMatcherConfiguration cConfig) throws StoreException {
        this.searcher = new LuceneClassifierSearcher(index, sConfig);
        this.vernacularSearcher = new LuceneClassifierSearcher(vernacular, sConfig);
        this.matcher = new ALAClassificationMatcher(AlaLinnaeanFactory.instance(), this.searcher, cConfig);
        this.vernacularMatcher = new ALAVernacularClassificationMatcher(AlaVernacularFactory.instance(), this.vernacularSearcher, cConfig);
        this.suggesterDir = suggesterDir;
        this.rankAnalysis = new RankAnalysis();
    }

    /**
     * Close the searcher
     */
    @Override
    public void close() throws Exception {
        if (this.suggester != null)
            this.getSuggester().close();
        this.matcher.close();
        this.vernacularMatcher.close();
        this.searcher.close();
        this.vernacularSearcher.close();
    }


    /**
     * Search for a classification, based on template classification data.
     *
     * @param template The template classification with various amounts of information filled in.
     * @param options The match options to use.
     *
     * @return The closest possible match.
     *
     * @throws BayesianException if unable to compuete match charactersics
     */
    public Match<AlaLinnaeanClassification, MatchMeasurement> search(AlaLinnaeanClassification template, MatchOptions options) throws BayesianException {
        return this.matcher.findMatch(template, options);
    }

    /**
     * Search for a classification, based on template classification data and with default options.
     *
     * @param template The template classification with various amounts of information filled in.
     *
     * @return The closest possible match.
     *
     * @throws BayesianException if unable to compuete match charactersics
     *
     * @see #search(AlaLinnaeanClassification, MatchOptions)
     * @see MatchOptions#ALL
     */
    public Match<AlaLinnaeanClassification, MatchMeasurement> search(AlaLinnaeanClassification template) throws BayesianException {
        return this.search(template, MatchOptions.ALL);
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
     * Search for a taxon identifier.
     *
     * @param taxonId The taxon identifier
     *
     * @return The resulting match
     *
     * @throws BayesianException If unable to retrieve the match
     */
    @NonNull
    public Match<AlaLinnaeanClassification, MatchMeasurement> search(String taxonId) throws BayesianException {
        AlaLinnaeanClassification actual = new AlaLinnaeanClassification();
        actual.taxonId = taxonId;
        LuceneClassifier classifier = this.searcher.get(DwcTerm.Taxon, AlaLinnaeanFactory.taxonId, taxonId);
        if (classifier == null)
            return Match.invalidMatch();
        AlaLinnaeanClassification classification = AlaLinnaeanFactory.instance().createClassification();
        classification.read(classifier, true);
        Match<AlaLinnaeanClassification, MatchMeasurement> match = new Match<>(actual, classifier, classification, Inference.one())
                .with(new SimpleFidelity<>(actual, actual, 1.0));
        if (classification.acceptedNameUsageId != null) {
            LuceneClassifier accepted = this.searcher.get(DwcTerm.Taxon, AlaLinnaeanFactory.taxonId, classification.acceptedNameUsageId);
            if (accepted != null) {
                AlaLinnaeanClassification acceptedClassification = AlaLinnaeanFactory.instance().createClassification();
                acceptedClassification.read(accepted, true);
                match = match.withAccepted(accepted, acceptedClassification);
            }
        }
        return match;
    }

    /**
     * Search for a classification, based on template vernacular data.
     *
     * @param template The template classification with various amounts of information filled in.
     * @param options The search options
     *
     * @return The closest possible match.
     *
     * @throws BayesianException if unable to compuete match charactersics
     */
    public Match<AlaVernacularClassification, MatchMeasurement> search(AlaVernacularClassification template, MatchOptions options) throws BayesianException {
        return this.vernacularMatcher.findMatch(template, options);
    }

    /**
     * Search for a classification, based on template vernacular data and with default match options.
     *
     * @param template The template classification with various amounts of information filled in.
     *
     * @return The closest possible match.
     *
     * @throws BayesianException if unable to compuete match charactersics
     *
     * @see #search(AlaVernacularClassification, MatchOptions)
     * @see MatchOptions#ALL
     */
    public Match<AlaVernacularClassification, MatchMeasurement> search(AlaVernacularClassification template) throws BayesianException {
        return this.search(template, MatchOptions.ALL);
    }

    /**
     * Get tall the vernacular names associated with a taxon.
     *
     * @param taxonId The taxon identifier
     *
     * @return A list of vernacular names
     *
     * @throws BayesianException if unable to retrevie the names
     */
    @NonNull
    public List<String> getVernacularNames(String taxonId) throws BayesianException {
        List<LuceneClassifier> results = this.vernacularSearcher.getAll(GbifTerm.VernacularName, AlaVernacularFactory.taxonId, taxonId);
        results.sort((c1, c2) -> - c1.getOrDefault(AlaVernacularFactory.weight, 0.0).compareTo(c2.getOrDefault(AlaVernacularFactory.weight, 0.0)));
        return results.stream().map(c -> c.get(AlaVernacularFactory.vernacularName)).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    @SneakyThrows
    protected LuceneClassifierSuggester buildSuggester() {
        LuceneClassifierSuggester suggester = new LuceneClassifierSuggester(
                FSDirectory.open(this.suggesterDir.toPath()),
                this.searcher,
                AlaLinnaeanFactory.instance()
        );
        if (!suggester.load()) {
            suggester.start();
            suggester.add(this.searcher, AlaLinnaeanFactory.taxonId, this::suggestWeight);
            suggester.add(this.vernacularSearcher, AlaVernacularFactory.taxonId, this::suggestWeight);
            suggester.create();
            suggester.store();
        }
        return suggester;
    }

    /**
     * Calculate a suggester weight for a linnaean classifier.
     * <p>
     * If ranked, linnaean ranks are preferred.
     * Shorter names are also preferred.
     * </p>
     *
     * @param classifier
     *
     * @return The resulting weight
     */
    private double suggestWeight(LuceneClassifier classifier) {
        double weight = 10.0;
        String name = classifier.getNames().stream().findFirst().orElse("");
        weight /= Math.log(Math.max(4, name.length())) + 1.0;
        try {
            Term type = classifier.getType();
            if (DwcTerm.Taxon.equals(type)) {
                Rank rank = classifier.get(AlaLinnaeanFactory.taxonRank);
                if (rank != null && rank.isLinnean())
                    weight *= 10.0;
            }
        } catch (StoreException ex) {
            logger.error("Unable to get type for " + classifier.getIdentifier(), ex);
         }
        return weight;
    }

    /**
     * Return a list of autocomplete results for a simple query.
     * <p>
     * Results are sorted by closeness to the query
     * </p>
     *
     * @param query The query (partial piece of text)
     * @param size The number of results to return
     * @param includeSynonyms Include any synonyms
     *
     * @return The result
     */
    public List<Autocomplete> autocomplete(String query, int size, boolean includeSynonyms) throws BayesianException {
         return this.getSuggester().suggest(query, size, includeSynonyms).stream()
                .map(c -> this.buildAutocomplete(c))
                .collect(Collectors.toList());
    }

    /**
     * Build an autocomplete entry for a linnaean classifier
     *
     * @param suggestion The suggestion
     *
     * @return An autocomplete that matches the classifier
     */
    protected Autocomplete buildAutocomplete(ClassifierSuggester.Suggestion<LuceneClassifier> suggestion) {
        List<Autocomplete> synonyms = null;
        LuceneClassifier accepted = suggestion.getMatch();
        Integer left = null;
        Integer right = null;
        if (suggestion.getSynonym() != null) {
            ClassifierSuggester.Suggestion<LuceneClassifier> synonym = new ClassifierSuggester.Suggestion<>(suggestion.getScore(), suggestion.getName(), suggestion.getType(), suggestion.getSynonym(), null);
            synonyms = Collections.singletonList(this.buildAutocomplete(synonym));
        }
        int[] index = accepted.getIndex();
        if (index != null) {
            left = index[0];
            right = index[1];
        }
        String taxonId = accepted.get(AlaLinnaeanFactory.taxonId);
        List<String> vernacularNames = null;
        try {
            vernacularNames = this.getVernacularNames(taxonId);
        } catch (BayesianException ex) {
            logger.error("Unable to get vernacular names for doc=" + accepted.getIdentifier(), ex);
        }
        String vernacularName = suggestion.getType().equals(GbifTerm.VernacularName) ?
                suggestion.getName() :
                accepted.get(AlaLinnaeanFactory.vernacularName);
        AlaLinnaeanClassification classification = AlaLinnaeanFactory.instance().createClassification();
        try {
            classification.read(accepted, true);
        } catch (BayesianException ex) {
            logger.error("Unable to get classification names for doc=" + accepted.getIdentifier(), ex);
            classification = null;
        }
        return Autocomplete.builder()
                .score((float) suggestion.getScore())
                .name(suggestion.getName())
                .taxonId(taxonId)
                .left(left)
                .right(right)
                .rank(this.rankAnalysis.toStore(accepted.get(AlaLinnaeanFactory.taxonRank)))
                .rankId(accepted.get(AlaLinnaeanFactory.rankId))
                .vernacularName(vernacularName)
                .vernacularNames(vernacularNames)
                .classification(classification)
                .synonyms(synonyms)
                .build();
    }


}
