package au.org.ala.bayesian;

import au.org.ala.util.BacktrackingIterator;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provide search services for a classification.
 * <p>
 * A classification is provided and the best possible match for the classification
 * is then returned.
 * </p>
 */
public class ClassificationMatcher<C extends Classification<C>, I extends Inferencer<C>, F extends NetworkFactory<C, I, F>> {
    private static final Logger logger = LoggerFactory.getLogger(ClassificationMatcher.class);

    /** The default possible theshold for something to be considered. @see #isPossible */
    public static double POSSIBLE_THESHOLD = 0.1;
    /** The default immediately acceptable threshold for something to be regarded as accepted. @see @isImmediateMatch */
    public static double IMMEDIATE_THRESHOLD = 0.99;
    /** The default acceptable threshold for something. @see @isAcceptableMatch */
    public static double ACCEPTABLE_THRESHOLD = 0.90;
    /** The amount that the evidence is allowed to be below the prior priobaility */
    public static double EVIDENCE_SLOP = 20.0;
    /** The amount of evidence before something is too unlikely */
    public static double EVIDENCE_THRESHOLD = Inference.MINIMUM_PROBABILITY * 10.0;

    /** The default match sort */
    protected Comparator<Match<C>> DEFAULT_SORT = (m1, m2) -> - Double.compare(m1.getProbability().getPosterior(), m2.getProbability().getPosterior());

    private F factory;
    private ClassifierSearcher<?> searcher;
    private I inferencer;
    private Analyser<C> analyser;
    private Optional<Observable> identifier;
    private Optional<Observable> accepted;

    /**
     * Create with a searcher and inferencer.
     *
     * @param factory The factory for creating objects for the matcher to work on
     * @param searcher The mechanism for getting candidiates
     */
    public ClassificationMatcher(F factory, ClassifierSearcher<?> searcher) {
        this.factory = factory;
        this.searcher = searcher;
        this.inferencer = this.factory.createInferencer();
        this.analyser = this.factory.createAnalyser();
        this.identifier = this.factory.getIdentifier();
        this.accepted = this.factory.getAccepted();
    }

    /**
     * Find a match for a classification
     *
     * @param classification The classification to match
     *
     * @return A match, {@link Match#invalidMatch()} is returned if no match is found
     *
     * @throws InferenceException if there is a failure during inference
     * @throws StoreException if unable to retrieve matches
     */
    @NonNull
    public Match<C> findMatch(C classification) throws InferenceException, StoreException {
        classification.infer(true);
        Iterator<C> sourceClassifications = new BacktrackingIterator<>(classification, classification.sourceModificationOrder());
        C previous = null;
        while (sourceClassifications.hasNext()) {
            C modified = sourceClassifications.next();
            if (modified == previous)
                continue;
            Match<C> match = this.findSource(modified);
            if (match != null)
                return match;
            previous = modified;
        }
        return Match.invalidMatch();
    }

    protected Match<C> findSource(C classification) throws InferenceException, StoreException {
        classification.infer(true);
        List<? extends Classifier> candidates = this.searcher.search(classification);
        if (candidates.isEmpty())
            return null;

        // First do a basic match and see if we have something easily matchable
        List<Match<C>> results = this.doMatch(classification, candidates);
        Match<C> match = this.findSingle(results);
        if (match != null)
            return match;

        // Do we have an evidence problem?
        C previous = classification;
        if (results.stream().allMatch(m -> this.isBadEvidence(m))) {
            Iterator<C> subClassifications = new BacktrackingIterator<C>(classification, classification.matchModificationOrder());
            while (subClassifications.hasNext()) {
                C modified = subClassifications.next();
                if (modified == classification || modified == previous) // Skip null case
                    continue;
                results = this.doMatch(modified, candidates);
                match = this.findSingle(results);
                if (match != null)
                    return match;
                previous = modified;
             }
        }

        return null;
    }

    protected List<Match<C>> doMatch(C classification, List<? extends Classifier> candidates) throws StoreException, InferenceException {
        List<Match<C>> results = new ArrayList<>(candidates.size());
        for (Classifier candidate: candidates) {
             Inference inference = this.inferencer.probability(classification, candidate);
            if (this.isPossible(classification, candidate, inference)) {
                C candidateClassification = this.factory.createClassification();
                candidateClassification.read(candidate, true);
                Match<C> match = new Match<>(classification, candidate, candidateClassification, inference);
                results.add(match);
            }
        }
        results.sort(this.getMatchSorter());
        results = this.resolve(results);
        results = this.annotate(results);
        return results;
    }

    /**
     * Resolve the results to ensure that accepted values are kept.
     *
     * @param results The results
     *
     * @return The resolved results.
     */
    protected List<Match<C>> resolve(List<Match<C>> results) {
        return results.stream().map(this::resolve).collect(Collectors.toList());
    }

    /**
     * Add the accepted taxon to the match for any synonym.
     *
     * @param match The match
     *
     * @return The original match, if not a synonym or the match with the accepted result
      */
    protected Match<C> resolve(Match<C> match)  {
        try {
            String acceptedId = match.getMatch().getAccepted();
            if (acceptedId == null || !accepted.isPresent() || !this.identifier.isPresent())
                return match;
            Classifier acceptedCandidate = this.searcher.get(match.getMatch().getType(), this.identifier.get(), acceptedId);
            if (acceptedCandidate == null)
                return match;
            C acceptedClassification = this.factory.createClassification();
            acceptedClassification.read(acceptedCandidate, true);
            return match.withAccepted(acceptedCandidate, acceptedClassification);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to match " + match, ex);
        }
    }

    /**
     * Annotate the result list with additional information and issues.
     *
     * @param results The list of results
     *
     * @throws StoreException if there is a problem retrieving information
     * @throws InferenceException if there is a problem making inferences about the data
     *
     * @return The modified set of matches
     */
    protected List<Match<C>> annotate(List<Match<C>> results) throws StoreException, InferenceException {
        return results;
    }

    /**
     * Find a single match that is acceptable.
     *
     * @param results The list of results
     *
     * @return A single acceptable result, or null
     */
    protected Match<C> findSingle(List<Match<C>> results) {
        if (!results.isEmpty()) {
            Match<C> first = results.get(0);
            // Look for an immediate match
            if (this.isImmediateMatch(first))
                return first;
            // See if we have a single acceptable answer
            List<Match<C>> acceptable = results.stream().filter(m -> this.isAcceptableMatch(m)).collect(Collectors.toList());
            if (acceptable.size() == 1)
                return acceptable.get(0);
        }
        return null;
    }

    /**
     * Get the sorting method for a list of matches
     *
     * @return The sort to use
     */
    protected Comparator<Match<C>> getMatchSorter() {
        return DEFAULT_SORT;
    }

    /**
     * Is this a possible match.
     * <p>
     * Generally, this means something above the minmum possible threshold
     * and with a p(E | H) greater than the p(H), meaning that the evidence
     * isn't completely unlikely.
     * Subclasses can get more creative, if required.
     * </p>
     *
     * @param classification The source classification
     * @param candidate The candidate classifier
     * @param inference The match probability
     *
     * @return True if this is a possible match
     */
    protected boolean isPossible(C classification, Classifier candidate, Inference inference) {
        return inference.getPosterior() >= POSSIBLE_THESHOLD && inference.getBoost() >= 1.0;
    }

    /**
     * Is this an immediate match, meaning that it will be used as a valid match
     * without the need to examine other possibilities.
     * <p>
     * Generally, this means something above the acceptable threshold probability
     * for both the posterior probability and the conditional evidence.
     * Subclasses can get more creative, if required.
     * </p>
     *
     * @param match The potential match
     *
     * @return True if this is a possible match
     */
    protected boolean isImmediateMatch(Match<C> match) {
        Inference p = match.getProbability();
        return p.getPosterior() >= IMMEDIATE_THRESHOLD && p.getConditional() >= IMMEDIATE_THRESHOLD;
    }

    /**
     * Is this match acceptable?
     * <p>
     * Generally, this means something that looks like it might have a chance of
     * being the right thing.
     * </p>
     *
     * @param match The candidate match
     *
     * @return True if this is an acceptable match
     */
    protected boolean isAcceptableMatch(Match<C> match) {
        Inference p = match.getProbability();
        return p.getPosterior() >= ACCEPTABLE_THRESHOLD && p.getEvidence() >= EVIDENCE_THRESHOLD && p.getConditional() >= POSSIBLE_THESHOLD;
    }

    /**
     * Does this match indicate an evidence problem.
     * <p>
     * Bad matches can occur when the likelihood of the evidence is so low
     * that it is overwhelming priors.
     * </p>
     *
     * @param match The candidate match
     *
     * @return True if this match is an example of bad evidence.
     */
    protected boolean isBadEvidence(Match<C> match) {
        Inference p = match.getProbability();
        return p.getPrior() > p.getEvidence() * EVIDENCE_SLOP || p.getPrior() > p.getConditional();
    }
}
