package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static au.org.ala.bayesian.ExternalContext.LUCENE;
import static au.org.ala.bayesian.Inference.MAXIMUM_PROBABILITY;
import static au.org.ala.bayesian.Inference.MINIMUM_PROBABILITY;

public class LuceneParameterAnalyser implements ParameterAnalyser {
    /** The network for this analyser */
    protected final Network network;
    /** The type of term this analyses */
    private final Term type;
    /** The index */
    private final IndexSearcher searcher;
    /** The weight cache */
    private final ConcurrentMap<Integer, Double> weightCache;
    /** The query cache */
    private final Cache<Query, Double>  queryCache;
    /** The weight observable */
    private final Observable<Double> weight;
    /** The default weight */
    private final double defaultWeight;
    /** The query builder */
    private final QueryUtils queryUtils;
    /** The query for taxon entries */
    private final Query taxonQuery;
    /** The total weight of all values, cached because this gets used a lot */
    private final double totalWeight;

    /**
     * Create for an index.
     *
     * @param network The network model
     * @param searcher The index searcher for queries
     * @param weight The weight observable
     * @param defaultWeight The weight to use when a value is unavailable
     *
     * @throws BayesianException if unable to build the analyser
     */
    public LuceneParameterAnalyser(Network network, IndexSearcher searcher, Observable weight, double defaultWeight) throws BayesianException {
        this.network = network;
        this.type = TermFactory.instance().findTerm(network.getConcept().toASCIIString());
        this.searcher = searcher;
        this.weightCache = new ConcurrentHashMap<>();
        // this.queryCache = null;
        this.queryCache = Cache2kBuilder.of(Query.class, Double.class)
            .eternal(true)
            .entryCapacity(1000000)
            .loader(this::doSum)
            .enableJmx(true)
            .build();
        this.weight = weight;
        this.defaultWeight = defaultWeight;
        this.queryUtils = new QueryUtils();
        this.taxonQuery = this.queryUtils.createBuilder().add(LuceneClassifier.getTypeClause(this.type)).build();
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
     * <p>
     * The result is cached.
     * The {@link #doSum(Query)} method actually calculates the sum.
     * </p>
     *
     * @param query The query
     *
     * @return The resulting sum
     */
    protected double sum(final Query query) throws InferenceException {
        return this.queryCache != null ? this.queryCache.get(query) : this.doSum(query);
    }


    /**
     * Perform a sum of all the weights for documents matching this query.
     *
     * @param query The query
     *
     * @return The resulting sum
     *
     * @see #sum(Query)
     */
    protected double doSum(final Query query) throws InferenceException {
        try {
            SumCollector collector = new SumCollector(this.searcher, this.weightCache, this.weight.getExternal(LUCENE), this.defaultWeight);
            this.searcher.search(query, collector);
            return collector.getSum();
        } catch (IOException ex) {
            throw new InferenceException("Unable to calculate weight sum" , ex);
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
     * @throws InferenceException if unable to sum
     * @throws StoreException if unable to convert to a query
     */
    public double computePrior(Observation observation) throws InferenceException, StoreException {
        if (totalWeight <= 0.0)
            return 0.0;

        Query valueQuery = this.queryUtils.createBuilder(this.taxonQuery).add(this.queryUtils.asClause(observation)).build();
        double valueWeight = this.sum(valueQuery);
        return Math.max(MINIMUM_PROBABILITY, Math.min(MAXIMUM_PROBABILITY, valueWeight / this.totalWeight));
    }

    /**
     * Computer a conditional probability for a observation.
     * <p>
     * This contains special code for when all inputs are negative observations.
     * Since we have the total weight, it's faster to compute all the positive
     * cases and then subtract them from the total weight than compute the negative cases,
     * which will be manyfold.
     * </p>
     *
     * @param observation The observation
     * @param inputs The facts inputting into the result
     *
     * @return The probability of the observation, given the inputs, by weight
     *
     * @throws InferenceException if ybable to compute the weight
     * @throws StoreException if unable to convert to a query
     */
    public double computeConditional(Observation observation, Observation... inputs) throws InferenceException, StoreException {
        if (totalWeight <= 0.0)
            return 0.0;
         List<Observation> nonBlank = Arrays.stream(inputs).filter(o -> !o.isBlank()).collect(Collectors.toList());

        // If no inputs, then pass on lower probabilities
        if (nonBlank.isEmpty()) {
            if (observation.isPositive())
                return Arrays.stream(inputs).allMatch(o -> o.isPositive()) ? 1.0 : 0.0;
            else
                return Arrays.stream(inputs).allMatch(o -> !o.isPositive()) ? 1.0 : 0.0;
        }
        double priorWeight = 1.0;
        boolean allNegative = true;
        BooleanQuery.Builder conditionalBuilder = this.queryUtils.createBuilder(this.taxonQuery);
        for (Observation input : nonBlank) {
            conditionalBuilder.add(this.queryUtils.asClause(input));
            allNegative = allNegative && !input.isPositive();
        }
        if (!allNegative) {
            priorWeight = this.sum(conditionalBuilder.build());
        } else {
            priorWeight = this.totalWeight;
            for (List<Observation> cases : this.generatePositiveCases(nonBlank)) {
                BooleanQuery.Builder positiveBuilder = this.queryUtils.createBuilder(this.taxonQuery);
                for (Observation input : cases)
                    positiveBuilder.add(this.queryUtils.asClause(input));
                priorWeight -= this.sum(positiveBuilder.build());
            }
        }
        if (priorWeight < MINIMUM_PROBABILITY)
            return MINIMUM_PROBABILITY;
        conditionalBuilder.add(this.queryUtils.asClause(observation));
        double conditionalWeight = this.sum(conditionalBuilder.build());
        return Math.max(MINIMUM_PROBABILITY, Math.min(MAXIMUM_PROBABILITY, conditionalWeight / priorWeight));
    }

    /**
     * Make a list of positive variations of a list of inputs.
     * <p>
     * Any instance of the list with at least one positive
     * </p>
     *
     * @param inputs The list of inputs.
     *
     * @return The inputs, power-setted over positive/negative without any all-negative instances.
     */
    private List<List<Observation>> generatePositiveCases(List<Observation> inputs) {
        List<List<Observation>> cases = new ArrayList<>(2 * inputs.size());
        this.generatePositiveCases(inputs, 0, cases, new Stack<Observation>());
        return cases;
    }

    /**
     * Generator to {@link #generatePositiveCases(List)}.
     *
     * @param inputs The list of inputs
     * @param index Which input to manipulate
     * @param cases A cases accumulator
     * @param stack The stack of existing inputs
     */
    private void generatePositiveCases(List<Observation> inputs, int index, List<List<Observation>> cases, Stack<Observation> stack) {
        if (index >= inputs.size()) {
            if (!stack.stream().allMatch(o -> !o.isPositive()))
                cases.add(new ArrayList<>(stack));
            return;
        }
        Observation o = inputs.get(index);
        stack.push(o.asPositive());
        this.generatePositiveCases(inputs, index + 1, cases, stack);
        stack.pop();
        stack.push(o.asNegative());
        this.generatePositiveCases(inputs, index + 1, cases, stack);
        stack.pop();
    }
}
