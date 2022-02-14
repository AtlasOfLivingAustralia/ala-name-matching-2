package au.org.ala.bayesian;

import au.org.ala.vocab.BayesianTerm;
import lombok.Value;
import org.gbif.dwc.terms.Term;

import java.util.Collection;

/**
 * A classification match.
 */
@Value
public class Match<C extends Classification<C>, M extends MatchMeasurement> {
    private static final Match<?, ?> INVALID_MATCH = new Match<>(null, null, null, null, null, null, null, Issues.of(BayesianTerm.invalidMatch), null);

    /** The classification that was actually searched for */
    private C actual;
    /** The candidate match */
    private Classifier candidate;
    /** The match classification */
    private C match;
    /** The synonym de-referenced accepted candidate match */
    private Classifier acceptedCandidate;
    /** The accepted classification */
    private C accepted;
    /** The match probability */
    private Inference probability;
    /** The match fidelity - how close to the original question we were */
    private Fidelity<C> fidelity;
    /** Any issues associated with the match */
    private Issues issues;
    /** The performance measurement for the match (may be null if no measurement was recorded) */
    private M measurement;

    /**
     * Construct for a match and a probability
     *
     * @param match The match
     * @param candidate The candidate classifier
     * @param probability The match probability
     * @param fidelity The fidelity
     * @param issues The issues list
     * @param measurement The collection statistics
     */
    protected Match(C actual, Classifier candidate, C match, Classifier acceptedCandidate, C accepted, Inference probability, Fidelity<C> fidelity, Issues issues, M measurement) {
        this.actual = actual;
        this.candidate = candidate;
        this.match = match;
        this.acceptedCandidate = acceptedCandidate;
        this.accepted = accepted;
        this.probability = probability;
        this.fidelity = fidelity;
        this.issues = issues;
        this.measurement = measurement;
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
        this(actual, candidate, match, candidate, match, probabilty, null, match.getIssues().merge(actual.getIssues()), null);
    }

    /**
     * Create a new match with an accepted candidate/classifier pair
     *
     * @param acceptedCandidate The accepted candididate
     * @param accepted The accepted classifier
     *
     * @return A new match with a new accepted
     */
    public Match<C, M> withAccepted(Classifier acceptedCandidate, C accepted) {
        return new Match<>(this.actual, this.candidate, this.match, acceptedCandidate, accepted, this.probability, null, this.issues.merge(accepted.getIssues()), this.measurement);
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
    public Match<C, M> with(Issues additional) {
        if (additional == null || additional.isEmpty())
            return this;
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.fidelity, this.issues.merge(additional), this.measurement);
    }

    /**
     * Add an issue to the match.
     *
     * @param issue The issue
     */
    public Match<C, M> with(Term issue) {
       return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.fidelity, this.issues.with(issue), this.measurement);
    }

    /**
     * Boost the probability of the match with the probability of other matches
     *
     * @param matches The liust of matches
     */
    public Match<C, M> boost(Collection<Match<C, M>> matches) {
        Inference p = this.probability;
        for (Match<C, M> match: matches) {
            if (match.getMatch() == this.match)
                continue;
            p = p.or(match.getProbability());
        }
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, p, this.fidelity, this.issues, this.measurement);
    }

    /**
     * Add measurements to the match.
     *
     * @param measurement The measurements
     *
     * @return A match with the appropriate measurements attached
     */
    public Match<C, M> with(M measurement) {
        if (measurement == null && this.measurement == null)
            return this;
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.fidelity, this.issues, measurement);
    }


    /**
     * Add a fidelity to the match.
     *
     * @param fidelity The fidelity
     *
     * @return A match with the appropriate measurements attached
     */
    public Match<C, M> with(Fidelity<C> fidelity) {
        if (fidelity == null && this.fidelity == null)
            return this;
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, fidelity, this.issues, measurement);
    }

    /**
     * Create an invalid match
     *
     * @return The invalid match singleton
     */
    public static <T extends Classification<T>, M extends MatchMeasurement> Match<T, M> invalidMatch() {
        return (Match<T, M>) INVALID_MATCH;
    }
}
