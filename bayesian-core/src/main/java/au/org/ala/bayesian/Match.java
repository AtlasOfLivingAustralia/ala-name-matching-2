package au.org.ala.bayesian;

import lombok.Value;
import lombok.With;
import org.gbif.dwc.terms.Term;

/**
 * A classification match.
 */
@Value
public class Match<C extends Classification> {
    private C match;
    private Classifier candidate;
    private C actual;
    private Inference probability;
    private Issues issues;

    /**
     * Construct for a match and a probability
     *
     * @param match The match
     * @param candidate The candidate classifier
     * @param probability The match probability
     * @param issues The issues list
     */
    public Match(C match, Classifier candidate, C actual, Inference probability, Issues issues) {
        this.match = match;
        this.candidate = candidate;
        this.actual = actual;
        this.probability = probability;
        this.issues = issues.merge(match.getIssues());
    }

    /**
     * Create a new match with additional issues.
     *
     * @param additional The issues to add
     *
     * @return This if the additional issues are null or empty, otherwise a match with merged issues.
     */
    public Match<C> with(Issues additional) {
        if (additional == null || additional.isEmpty())
            return this;
        return new Match<>(this.match, this.candidate, this.actual, this.probability, this.issues.merge(additional));
    }

    /**
     * Add an issue to the match.
     *
     * @param issue The issue
     */
    public Match<C> with(Term issue) {
       return new Match<>(this.match, this.candidate, this.actual, this.probability, this.issues.with(issue));
    }
}
