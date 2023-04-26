package au.org.ala.bayesian;

import au.org.ala.vocab.BayesianTerm;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.SneakyThrows;
import lombok.Value;
import org.gbif.dwc.terms.Term;

import java.util.*;

/**
 * A classification match.
 */
@Value
@JsonPropertyOrder({"actual", "valid", "candidate", "match", "acceptedCandidate", "accepted", "probability", "left", "right", "fidelity", "issues", "measurement", "trace"})
@TraceDescriptor(identify = true, description = "getFullDescription")
public class Match<C extends Classification<C>, M extends MatchMeasurement> {
    private static final Match<?, ?> INVALID_MATCH = new Match<>(null, null, null, null, null, null, null, Issues.of(BayesianTerm.invalidMatch), null, null);

    /** The classification that was actually searched for */
    private C actual;
    /** The candidate match */
    @JsonIgnore
    private Classifier candidate;
    /** The match classification */
    private C match;
    /** The synonym de-referenced accepted candidate match */
    @JsonIgnore
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
    /** The trace for the match (may be null if no trace was recorded) */
    private Trace trace;

    /**
     * Construct for a match and a probability
     *
     * @param match The match
     * @param candidate The candidate classifier
     * @param probability The match probability
     * @param fidelity The fidelity
     * @param issues The issues list
     * @param measurement The collection statistics
     * @param trace The inference trace
     */
    protected Match(C actual, Classifier candidate, C match, Classifier acceptedCandidate, C accepted, Inference probability, Fidelity<C> fidelity, Issues issues, M measurement, Trace trace) {
        this.actual = actual;
        this.candidate = candidate;
        this.match = match;
        this.acceptedCandidate = acceptedCandidate;
        this.accepted = accepted;
        this.probability = probability;
        this.fidelity = fidelity;
        this.issues = issues;
        this.measurement = measurement;
        this.trace = trace;
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
        this(actual, candidate, match, candidate, match, probabilty, null, match.getIssues().merge(actual.getIssues()), null, null);
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
        return new Match<>(this.actual, this.candidate, this.match, acceptedCandidate, accepted, this.probability, this.fidelity, this.issues.merge(accepted.getIssues()), this.measurement, this.trace);
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
     * Get the left-value of the accepted taxon
     *
     * @return The left-value
     *
     * @throws StoreException if unable to retrieve the left value
     */
    public Integer getLeft() throws StoreException {
        Classifier classifier = this.acceptedCandidate != null ? this.acceptedCandidate : this.candidate;
        int[] index = classifier == null ? null : classifier.getIndex();
        return index == null ? null : index[0];
    }

    /**
     * Get the right-value of the accepted taxon
     *
     * @return The right-value
     *
     * @throws StoreException if unable to retrieve the right value
     */
    public Integer getRight() throws StoreException {
        Classifier classifier = this.acceptedCandidate != null ? this.acceptedCandidate : this.candidate;
        int[] index = classifier == null ? null : classifier.getIndex();
        return index == null ? null : index[1];
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
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.fidelity, this.issues.merge(additional), this.measurement, this.trace);
    }

    /**
     * Add an issue to the match.
     *
     * @param issue The issue
     */
    public Match<C, M> with(Term issue) {
       return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.fidelity, this.issues.with(issue), this.measurement, this.trace);
    }

    /**
     * Change the probability of the match.
     *
     * @param probability The new probability
     */
    public Match<C, M> with(Inference probability) {
        if (probability == null)
            return this;
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, probability, this.fidelity, this.issues, this.measurement, this.trace);
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
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, p, this.fidelity, this.issues, this.measurement, this.trace);
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
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, this.fidelity, this.issues, measurement, this.trace);
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
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, fidelity, this.issues, this.measurement, this.trace);
    }


    /**
     * Add a trace to the match.
     *
     * @param trace The trace
     *
     * @return A match with the appropriate measurements attached
     */
    public Match<C, M> with(Trace trace) {
        if (trace == null && this.trace == null)
            return this;
        return new Match<>(this.actual, this.candidate, this.match, this.acceptedCandidate, this.accepted, this.probability, fidelity, this.issues, this.measurement, trace);
    }

    @SneakyThrows
    public Map<String, Object> getFullDescription(NetworkFactory<?, ?, ?> factory) {
        Map<String, Object> desc = new LinkedHashMap<>();
        desc.put("actual", this.actual);
        desc.put("candidate", this.candidate == null ? null : this.candidate.getSummaryDescription(factory));
        desc.put("match", this.match);
        if (this.accepted != this.match) {
            desc.put("accepted", this.accepted);
            desc.put("acceptedCandidate", this.acceptedCandidate == null ? null : this.acceptedCandidate.getSummaryDescription(factory));
        }
        desc.put("probability", this.getProbability());
        desc.put("fidelity", this.getFidelity());
        desc.put("left", this.getLeft());
        desc.put("right", this.getRight());
        desc.put("issues", this.getIssues());
        return desc;
    }


    /**
     * Get all the identifiers that could be used to match this concept, from the actual identifier
     * through all the parents.
     *
     * @return The identifier set for the accepted candidate (empty for an invalid match)
     */
    public Set<String> getAllIdentifiers() {
        if (!this.isValid())
            return Collections.emptySet();
        Set<String> hierarchy = new HashSet<>(this.acceptedCandidate.getTrail());
        hierarchy.add(this.accepted.getIdentifier());
        return hierarchy;
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
