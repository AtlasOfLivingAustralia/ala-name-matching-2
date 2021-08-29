package au.org.ala.bayesian;

import au.org.ala.vocab.BayesianTerm;
import lombok.Value;
import lombok.With;
import org.gbif.dwc.terms.Term;

import java.util.Collection;

/**
 * A classification match.
 */
@Value
public class Match<C extends Classification<C>> {
    private static final Match<?> INVALID_MATCH = new Match<>(null, null, null, null, null, null, Issues.of(BayesianTerm.invalidMatch));

    private C actual;
    private Classifier candidate;
    private C match;
    private Classifier acceptedCandidate;
    private C accepted;
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
    protected Match(C actual, Classifier candidate, C match, Classifier acceptedCandidate, C accepted, Inference probability, Issues issues) {
        this.actual = actual;
        this.candidate = candidate;
        this.match = match;
        this.acceptedCandidate = acceptedCandidate;
        this.accepted = accepted;
        this.probability = probability;
        this.issues = issues;
    }

    /**
     * Creaate a simple match with the accepted version the same as the match.
     *
     * @param actual The actual classifier
     * @param candidate The candidate
     * @param match The match built from the candidate
     * @param probabilty The inferred probability of the match
     */
    public Match(C actual, Classifier candidate, C match, Inference probabilty) {
        this(actual, candidate, match, candidate, match, probabilty, match.getIssues().merge(actual.getIssues()));
    }

    /**
     * Create a new match with an accepted candidate/classifier pair
     *
     * @param acceptedCandidate The accepted candididate
     * @param accepted The accepted classifier
     *
     * @return A new match with a new accepted
     */
    public Match<C> withAccepted(Classifier acceptedCandidate, C accepted) {
        return new Match<>(this.actual, this.candidate, this.match, acceptedCandidate, accepted, this.probability, this.issues.merge(accepted.getIssues()));
    }

    /**
     * Is this a valid match?
     *
     * @return True if there is a match that can be used
     */
    public boolean isValid() {
        return this.match != null;
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
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.issues.merge(additional));
    }

    /**
     * Add an issue to the match.
     *
     * @param issue The issue
     */
    public Match<C> with(Term issue) {
       return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.issues.with(issue));
    }

    /**
     * Boost the probability of the match with the probability of other matches
     *
     * @param matches The liust of matches
     */
    public Match<C> boost(Collection<Match<C>> matches) {
        Inference p = this.probability;
        for (Match<C> match: matches) {
            if (match.getMatch() == this.match)
                continue;
            p = p.or(match.getProbability());
        }
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, p, this.issues);
    }

    /**
     * Create an invalid match
     *
     * @return The invalid match singleton
     */
    public static <T extends Classification<T>> Match<T> invalidMatch() {
        return (Match<T>) INVALID_MATCH;
    }
}
