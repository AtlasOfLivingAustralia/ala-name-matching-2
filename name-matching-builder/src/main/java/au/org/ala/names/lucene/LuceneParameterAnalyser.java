package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.names.builder.Annotator;
import au.org.ala.bayesian.StoreException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.*;
import org.gbif.dwc.terms.DwcTerm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LuceneParameterAnalyser extends ParameterAnalyser {
    /** The minimum probability we can get to. This is defined so that #MAXIMUM_PROBABILITY does not evaluate to 1 */
    public static final double MINIMUM_PROBABILITY = 1.0e-9;
    /** The maximum probability we can get to, 1 - #MINIMUM_PROABILITY This must be (just) less than 1. */
    public static final double MAXIMUM_PROBABILITY = 1.0 - MINIMUM_PROBABILITY;

    /** The network for this analyser */
    protected Network network;
    /** The annotator for this analyser  */
    protected Annotator annotator;
    /** The index */
    private IndexSearcher searcher;
    /** The weight observable */
    private Observable weight;
    /** The default weight */
    private double defaultWeight;
    /** The query builder */
    private QueryUtils queryUtils;
    /** The query for taxon entries */
    private Query taxonQuery;
    /** The total weight of all values, cached because this gets used a lot */
    private double totalWeight;

    /**
     * Create for an index.
     *
     * @param network The network model
     * @param annotator The annotator to use
     * @param searcher The index searcher for queries
     * @param weight The weight observable
     * @param defaultWeight The weight to use when a value is unavailable
     */
    public LuceneParameterAnalyser(Network network, Annotator annotator, IndexSearcher searcher, Observable weight, double defaultWeight) throws InferenceException {
        this.network = network;
        this.annotator = annotator;
        this.searcher = searcher;
        this.weight = weight;
        this.defaultWeight = defaultWeight;
        this.queryUtils = new QueryUtils();
        this.taxonQuery = this.queryUtils.createBuilder().add(LuceneClassifier.getTypeClause(DwcTerm.Taxon)).build();
        this.totalWeight = this.sum(this.taxonQuery);
    }

    /**
     * Get the total weight for all taxa.
     *
     * @return The total weight
     */
    public double getTotalWeight() {
        return totalWeight;
    }

    /**
     * Sum all the weights for documents matching this query.
     *
     * @param query The query
     *
     * @return The resulting sum
     */
    protected double sum(Query query) throws InferenceException {
        try {
            SumCollector collector = new SumCollector(this.searcher, this.weight.getField(), this.defaultWeight);
            this.searcher.search(query, collector);
            return collector.getSum();
        } catch (IOException ex) {
            throw new InferenceException("Unable to calculate weight sum", ex);
        }
    }

    /**
     * Compute the prior probability of a field.
     * <p>
     * This is the weight of documents with these values, divided by the total weight of documents.
     * </p>
     *
     * @param observation The observation we are computing a probability for
     *
     * @return The probaility of the observation, by weight.
     *
     * @throws InferenceException
     */
    public double computePrior(Observation observation) throws InferenceException {
        if (totalWeight <= 0.0)
            return 0.0;

        Query valueQuery = this.queryUtils.createBuilder(this.taxonQuery).add(this.queryUtils.asClause(observation)).build();
        double valueWeight = this.sum(valueQuery);
        return Math.max(MINIMUM_PROBABILITY, Math.min(MAXIMUM_PROBABILITY, valueWeight / this.totalWeight));
    }

    /**
     * Computer a conditional probability for a observation
     *
     * @param observation The observation
     * @param inputs The facts inputting into the result
     *
     * @return The probability of the observation, given the inputs, by weight
     *
     * @throws InferenceException
     */
    public double computeConditional(Observation observation, Observation... inputs) throws InferenceException {
        if (totalWeight <= 0.0)
            return 0.0;
        if (observation.isBlank())
            return 1.0;
        BooleanQuery.Builder conditionalBuilder = this.queryUtils.createBuilder(this.taxonQuery);
        for (int i = 0; i < inputs.length; i++) {
            if (!inputs[i].isBlank())
                conditionalBuilder.add(this.queryUtils.asClause(inputs[i]));
        }
        double priorWeight = this.sum(conditionalBuilder.build());
        if (priorWeight < MINIMUM_PROBABILITY)
            return MINIMUM_PROBABILITY;
        conditionalBuilder.add(this.queryUtils.asClause(observation));
        double conditionalWeight = this.sum(conditionalBuilder.build());
        return Math.max(MINIMUM_PROBABILITY, Math.min(MAXIMUM_PROBABILITY, conditionalWeight / priorWeight));
    }
}
