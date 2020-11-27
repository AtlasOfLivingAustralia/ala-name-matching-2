package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.util.BasicNormaliser;
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

/**
 * Build queries for lucene indexes
 */
public class QueryUtils {
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
        return this.asClause(observation, true);
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
     *
     * @return A matching lucene clause
     *
     * @throws StoreException if unable to convert to a query
     */
    public BooleanClause asClause(Observation observation, boolean required) throws StoreException {
        Observable observable = observation.getObservable();
        String field = observable.getExternal(LUCENE);
        Query base;

        if (observation.isPresent()) {
            // Invert MUST/MUST_NOT for presence, a blank observations means that something should not be there
            return new BooleanClause(new DocValuesFieldExistsQuery(field), observation.isPositive() ? BooleanClause.Occur.MUST_NOT : BooleanClause.Occur.MUST);
        } else {
            base = this.asQuery(
                field,
                observable.getType(),
                observable.getStyle(),
                observable.getNormaliser(),
                observable.getAnalysis(),
                observation.isSingleton() ? observation.getValue() : observation.getValues()
        );
        }
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
                        observable.getExternal(LUCENE),
                        observable.getType(),
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
    public <C> Query asQuery(String field, Class<C> type, Observable.Style style, Normaliser normaliser, Analysis<C> analysis, Object value) throws StoreException {
        if (value instanceof Collection) {
            BooleanQuery.Builder all = new BooleanQuery.Builder();
            for (Object v: (Collection) value) {
                all.add(new BooleanClause(this.asQuery(field, type, style, normaliser, analysis, v), BooleanClause.Occur.SHOULD));
            }
            return all.build();
        }
        if (Integer.class.isAssignableFrom(type)) {
            return IntPoint.newExactQuery(field, ((Number) value).intValue());
        }
        if (Number.class.isAssignableFrom(type)) {
            return DoublePoint.newExactQuery(field, ((Number) value).doubleValue());
        }
        String val = analysis.toString((C) value);
        if (normaliser != null)
            val = normaliser.normalise(val);
        switch (style) {
            case IDENTIFIER:
            case CANONICAL:
                return new TermQuery(new Term(field, val));
            default:
                QueryBuilder builder = new QueryBuilder(this.getAnalyzer());
                return builder.createPhraseQuery(field, val);
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
