package au.org.ala.names;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Annotator;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.builder.LoadStore;
import au.org.ala.names.builder.WeightAnalyser;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.util.Counter;
import au.org.ala.vocab.TaxonomicStatus;
import com.opencsv.CSVReader;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.nameparser.api.Rank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ALA-specific weight analyser.
 * <p>
 * This contains several parts.
 * A default weight based on the score allocated by the merging process (or 1 by default).
 * A lookup weight based on tables of data in a DwCA.
 * A modifier that prefers Linnaean ranks over assorted mid-rank information.
 * </p>
 */
public class AlaWeightAnalyser implements WeightAnalyser, Annotator {
    private static final Logger logger = LoggerFactory.getLogger(AlaWeightAnalyser.class);

    /**
     * The name of the rank weights files.
     * If there is a file called {@value} in the configuration directory, then
     * that file is read, otherwise a default file is loaded as a resource.
     */
    public static final String RANK_WEIGHTS_FILE = "rank-weights.csv";
    /**
     * The name of the taxonomic status weights files.
     * If there is a file called {@value} in the configuration directory, then
     * that file is read, otherwise a default file is loaded as a resource.
     */
    public static final String TAXONOMIC_STATUS_WEIGHTS_FILE = "taxonomic-status-weights.csv";
    /**
     * The name of the nomemclatural status weights files.
     * If there is a file called {@value} in the configuration directory, then
     * that file is read, otherwise a default file is loaded as a resource.
     */
    public static final String NOMENCLATURAL_STATUS_WEIGHTS_FILE = "nomenclatural-status-weights.csv";
    /**
     * The name of the weight lookup file with taxonID -> weight pairs.
     * If there is a file called {@value} in the data directory, then
     * that file is read into a store.
     */
    public static final String TAXON_WEIGHTS_FILE = "taxon-weights.csv";

    private Map<Rank, Double> rankWeights;
    private Map<TaxonomicStatus, Double> taxonomicStatusWeights;
    private Map<NomenclaturalStatus, Double> nomenclauturalStatusWeights;
    private LoadStore<LuceneClassifier> weightStore;

    /**
     * Construct a weight analyser for a configuration
     *
     * @param configuration The configuration
     * @throws Exception If unable to build the analyser
     */
    public AlaWeightAnalyser(IndexBuilderConfiguration configuration) throws Exception {
        this.rankWeights = this.buildWeights(configuration, RANK_WEIGHTS_FILE, Rank.class);
        this.taxonomicStatusWeights = this.buildWeights(configuration, TAXONOMIC_STATUS_WEIGHTS_FILE, TaxonomicStatus.class);
        this.nomenclauturalStatusWeights = this.buildWeights(configuration, NOMENCLATURAL_STATUS_WEIGHTS_FILE, NomenclaturalStatus.class);
        this.buildNameWeights(configuration);
    }

    /**
     * Compute the base weight of the classifier.
     * <p>
     * If there is a {@link AlaLinnaeanFactory#priority} then base the weight on the priority.
     * Otherwise, default to 1.
     * </p>
     *
     * @param classifier The classuifier to weight
     * @return The base weight (must be at least 1.0)
     * @throws BayesianException if unable to compute the weight
     */
    @Override
    public double weight(Classifier classifier) throws BayesianException {
        String id = classifier.get(AlaLinnaeanFactory.taxonId);
        Classifier wc = id == null ? null : this.weightStore.get(DwcTerm.Taxon, AlaLinnaeanFactory.taxonId, id);
        Double weight = wc == null ? null : wc.get(AlaLinnaeanFactory.weight);
        if (weight != null)
            return weight;
        Integer priority = classifier.get(AlaLinnaeanFactory.priority);
        if (priority == null || priority < 1)
            return 1.0;
        return Math.log(priority.doubleValue()) + 1.0;
    }

    /**
     * Modify the base weight in the classifier.
     * <p>
     * If the classifier has an interesting rank, then boost it bu the rank weighting.
     * </p>
     *
     * @param classifier The classuifier
     * @param weight     The base weight
     * @return The modified weight (must be at least 1.0)
     * @throws BayesianException if unable to compute the weight
     */
    @Override
    public double modify(Classifier classifier, double weight) throws BayesianException {
        Rank rank = classifier.get(AlaLinnaeanFactory.taxonRank);
        if (rank != null)
            weight *= this.rankWeights.getOrDefault(rank, 1.0);
        TaxonomicStatus taxonomicStatus = classifier.get(AlaLinnaeanFactory.taxonomicStatus);
        if (taxonomicStatus != null)
            weight *= this.taxonomicStatusWeights.getOrDefault(taxonomicStatus, 1.0);
        NomStatus nomStatus = classifier.get(AlaLinnaeanFactory.nomenclaturalStatus);
        if (nomStatus != null) {
            double factor = nomStatus.getStatus().stream()
                    .mapToDouble(s -> this.nomenclauturalStatusWeights.getOrDefault(s, 1.0))
                    .min()
                    .orElse(1.0);
            weight *= factor;
        }
        return Math.max(1.0, weight);
    }

    protected <T extends Enum<T>> Map<T, Double> buildWeights(IndexBuilderConfiguration configuration, String source, Class<T> clazz) throws IOException, StoreException {
        CSVReader reader = null;
        Map<T, Double> weights = new HashMap<>();
        logger.info("Loading " + source);
        Counter counter = new Counter("Read {0} weights, {2,number,#}/s", logger, 100, 0);
        try {
            reader = configuration.openConfigCsv(source, this.getClass());
            counter.start();
            for(String[] row: reader) {
                T value = Enum.valueOf(clazz, row[0]);
                double weight = Double.parseDouble(row[1]);
                weights.put(value, weight);
                counter.increment(value);
            }
            counter.stop();
        } finally {
            if (reader != null)
                reader.close();
        }
        return weights;
    }

    protected void buildNameWeights(IndexBuilderConfiguration configuration) throws Exception {
        CSVReader reader = null;
        this.weightStore = configuration.createLoadStore("weights", true);
        logger.info("Loading " + TAXON_WEIGHTS_FILE);
        Counter counter = new Counter("Read {0} taxon weights, {2,number,#}/s", logger, 100000, 0);
        try {
            reader = configuration.openDataCsv(TAXON_WEIGHTS_FILE);
            counter.start();
            if (reader != null) {
                for (String[] row : reader) {
                    String taxonId = row[0];
                    double weight = Double.parseDouble(row[1]);
                    LuceneClassifier classifier = this.weightStore.newClassifier();
                    classifier.identify();
                    classifier.add(AlaLinnaeanFactory.taxonId, taxonId, false);
                    classifier.add(AlaLinnaeanFactory.weight, weight, false);
                    this.weightStore.store(classifier, DwcTerm.Taxon);
                    counter.increment(taxonId);
                }
            }
            this.weightStore.commit();
            counter.stop();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /**
     * Annotate a classifier with additional information.
     *
     * @param classifier The classifier
     */
    @Override
    public void annotate(Classifier classifier) {
    }

    /**
     * Clean up the weight store
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.weightStore != null) {
            this.weightStore.close();
        }
    }
}
