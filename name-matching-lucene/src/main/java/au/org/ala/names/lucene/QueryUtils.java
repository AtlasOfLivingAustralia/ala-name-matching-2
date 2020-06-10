package au.org.ala.names.lucene;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.util.BytesRef;

import java.util.stream.Collectors;

/**
 * Build queries for lucene indexes
 */
public class QueryUtils {
    /**
     * Default constructor
     */
    public QueryUtils() {
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
     */
    public Builder createBuilder(Observable observable, String value) {
        return this.createBuilder().add(new TermQuery(new Term(observable.getField(), value)), BooleanClause.Occur.FILTER);
    }

    /**
     * Concert an observation into a boolean clause.
     *
     * @param observation The observation
     *
     * @return A matching lucene clause
     */
    public BooleanClause asClause(Observation observation) {
        String field = observation.getObservable().getField();
        Query base;

        if (observation.isPresent()) {
            base = new DocValuesFieldExistsQuery(field);
        } else if (observation.isSingleton()) {
            base = new TermQuery(new Term(field, observation.getValue()));
        } else {
            base = new TermInSetQuery(field, observation.getValues().stream().map(v -> new BytesRef(v)).collect(Collectors.toList()));
        }
        return new BooleanClause(base, observation.isPositive() ? BooleanClause.Occur.MUST : BooleanClause.Occur.MUST_NOT);
    }

    /**
     * Concert an observable/value into a boolean clause.
     *
     * @param observable The observable
     * @param value The required value
     *
     * @return A matching lucene clause
     */
    public BooleanClause asClause(Observable observable, String value) {
        return this.asClause(observable.getField(), value);
    }

    /**
     * Concert an field/value into a boolean clause.
     *
     * @param field The field name
     * @param value The required value
     *
     * @return A matching lucene clause
     */
    public BooleanClause asClause(String field, String value) {
        Query base = new TermQuery(new Term(field, value));
        return new BooleanClause(base, BooleanClause.Occur.MUST);
    }

}
