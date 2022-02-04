package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.util.BasicNormaliser;
import org.apache.commons.lang3.Range;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.util.QueryBuilder;

import java.io.IOException;
import java.util.Collection;

import static au.org.ala.bayesian.ExternalContext.LUCENE;
import static au.org.ala.bayesian.ExternalContext.LUCENE_VARIANT;

/**
 * Build queries for lucene indexes
 */
public class QueryUtils {
    /** The amount to boost results from a matching name */
    public static final float NAME_BOOST = 2.0f;

    private Analyzer analyzer;

    /**
     * Default constructor
     */
    public QueryUtils() throws StoreException {
        this.analyzer = this.createAnalyzer();
    }

    /**
     * Get the analyser associated with this set of quert utiltities
     *
     * @return The analyser
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * Create a new query builder.
     *
     * @return The new, empty builder
     */
    public Builder createBuilder() {
        return new Builder();
    }

    /**
     * Create a new query builder for a base query.
     *
     * @param base The base query
     *
     * @return The builder required to set
     */
    public Builder createBuilder(Query base) {
        return this.createBuilder().add(base, BooleanClause.Occur.FILTER);
    }

    /**
     * Create a new query builder for a base query.
     *
     * @return The new, empty builder
     *
     * @throws StoreException if unable to convert to a query
     */
    public Builder createBuilder(Observable observable, String value) throws StoreException {
        return this.createBuilder().add(this.asClause(observable, value));
    }

    /**
     * Concert an observation into a required boolean clause.
     *
     * @param observation The observation
     *
     * @return A matching lucene clause
     *
     * @throws StoreException if unable to convert to a query
     */
    public BooleanClause asClause(Observation observation) throws StoreException {
        return this.asClause(observation, true, 1.0f);
    }

    /**
     * A clause that searches really hard for a matching name.
     *
     * @param value The name
     *
     * @return The name search query
     */
    public BooleanClause nameClause(String value) {
        QueryBuilder builder = new QueryBuilder(this.analyzer);
        Query query = builder.createPhraseQuery(LuceneClassifier.NAMES_FIELD, value);
        query = new BoostQuery(query, NAME_BOOST);
        return new BooleanClause(query, BooleanClause.Occur.SHOULD);
    }
    /**
     * Concert an observation into a potentially required boolean clause.
     * <p>
     * Note that this uses {@link TermQuery} and {@link TermInSetQuery} to search for entries.
     * Terms are normalised using a {@link BasicNormaliser}
     * </p>
     *
     * @param observation The observation
     * @param required True if the clause is required
     * @param boost Add a boost to the query if required. 1,0f means no boost
     *
     * @return A matching lucene clause
     *
     * @throws StoreException if unable to convert to a query
     */
    public BooleanClause asClause(Observation observation, boolean required, float boost) throws StoreException {
        Observable observable = observation.getObservable();
        String field = observable.getExternal(observable.getMultiplicity().isMany() ? LUCENE_VARIANT : LUCENE);
        Query base;

        if (observation.isPresent()) {
            // Invert MUST/MUST_NOT for presence, a blank observations means that something should not be there
            return new BooleanClause(new DocValuesFieldExistsQuery(field), observation.isPositive() ? BooleanClause.Occur.MUST_NOT : BooleanClause.Occur.MUST);
        } else {
            base = this.asQuery(
                field,
                observable.getStyle(),
                observable.getNormaliser(),
                observable.getAnalysis(),
                observation.isSingleton() ? observation.getValue() : observation.getValues()
            );
        }
        if (boost != 1.0f)
            base = new BoostQuery(base, boost);
        return new BooleanClause(base, observation.isPositive() ? (required ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD) : BooleanClause.Occur.MUST_NOT);
    }

    /**
     * Concert an observable/value into a boolean clause.
     *
     * @param observable The observable
     * @param value The required value
     *
     * @return A matching lucene clause
     *
     * @throws StoreException if unable to convert to a query
     */
    public BooleanClause asClause(Observable observable, String value) throws StoreException {
        return new BooleanClause(
                this.asQuery(
                        observable.getExternal(observable.getMultiplicity().isMany() ? LUCENE_VARIANT : LUCENE),
                        observable.getStyle(),
                        observable.getNormaliser(),
                        observable.getAnalysis(),
                        value),
                BooleanClause.Occur.FILTER
        );
    }

    /**
     * Concert a field/value into a query.
     *
     * @param field The field name
     * @param value The required value
     *
     * @return A matching lucene clause
     *
     * @throws StoreException if unable to convert to a query
     */
    public <C, S, Q> Query asQuery(String field, Observable.Style style, Normaliser normaliser, Analysis<C, S, Q> analysis, Object value) throws StoreException {
        if (value instanceof Collection) {
            BooleanQuery.Builder all = new BooleanQuery.Builder();
            for (Object v: (Collection) value) {
                all.add(new BooleanClause(this.asQuery(field, style, normaliser, analysis, v), BooleanClause.Occur.SHOULD));
            }
            return all.build();
        }
        Class<C> type = analysis.getType();
        if (value != null && !type.isAssignableFrom(value.getClass()))
            throw new StoreException("Value " + value + " does not match " + type);
        Q query = analysis.toQuery((C) value);
        if (query instanceof String && normaliser != null)
            query = (Q) normaliser.normalise((String) query);
        if (query instanceof Integer) {
            return IntPoint.newExactQuery(field, ((Number) query).intValue());
        }
        if (query instanceof Number) {
            return DoublePoint.newExactQuery(field, ((Number) query).doubleValue());
        }
        if (query instanceof Range) {
              Range<Integer> range = (Range<Integer>) query;
            return IntPoint.newRangeQuery(field, range.getMinimum(), range.getMaximum());
        }
        switch (style) {
            case IDENTIFIER:
            case CANONICAL:
                return new TermQuery(new Term(field, query.toString()));
            default:
                QueryBuilder builder = new QueryBuilder(this.getAnalyzer());
                return builder.createPhraseQuery(field, query.toString());
        }
    }

    /**
     * Create a text analyser for the store.
     * <p>
     * By default, this is a lower-case, keyword analyzer (i.e. case insensitive, treating eveything as a single unit)
     * </p>
     *
     * @return A analyzer.
     *
     * @throws StoreException if unable to construct the analyzer
     */
    protected Analyzer createAnalyzer() throws StoreException {
        try {
            return CustomAnalyzer.builder().withTokenizer(KeywordTokenizerFactory.NAME).addTokenFilter(LowerCaseFilterFactory.NAME).build();
        } catch (IOException ex) {
            throw new StoreException("Unable to construct an analyzer", ex);
        }
    }
}
