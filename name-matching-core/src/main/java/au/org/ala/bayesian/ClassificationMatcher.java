package au.org.ala.bayesian;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide search services for a classification.
 * <p>
 * A classification is provided and the best possible match for the classification
 * is then returned.
 * </p>
 */
abstract public class ClassificationMatcher<C extends Classification, P extends Parameters, I extends Inferencer<C, P>> {
    /** The default possible theshold for something to be considered. @see isPossible */
    public static double POSSIBLE_THESHOLD = 0.1;
    /** The default acceptable threshold for something to be regarded as accepted. @see isAcceptable */
    public static double ACCEPTABLE_THRESHOLD = 0.99;

    private ClassifierSearcher searcher;
    private I inferencer;

    /**
     * Create with a searcher and inferencer.
     *
     * @param searcher The mechanism for getting candidiates
     * @param inferencer The mechanism for computing match probabilities
     */
    public ClassificationMatcher(ClassifierSearcher searcher, I inferencer) {
        this.searcher = searcher;
        this.inferencer = inferencer;
    }

    public Match<C> findMatch(C classification) throws InferenceException, StoreException {
        List<Classifier> candididates = this.searcher.search(classification);

        if (candididates.isEmpty())
            return null;
        List<Match<C>> results = new ArrayList<>(candididates.size());
        for (Classifier candidate: candididates) {
            P parameters = this.createParameters();
            candidate.loadParameters(parameters);
            double p = this.inferencer.probability(classification, candidate, parameters);
            if (this.isPossible(classification, candidate, p)) {
                C candidateClassification = this.createClassification();
                candidateClassification.populate(candidate, true);
                Match<C> match = new Match<>(candidateClassification, candidate, p);
                if (this.isAcceptable(classification, candidate, p))
                    return match;
                results.add(match);
            }
        }
        results.sort((m1, m2) -> - Double.compare(m1.getProbability(), m2.getProbability()));
        // TODO resolution
        return results.get(0);
    }

    /**
     * Is this a possible match.
     * <p>
     * Generally, this means something above the minmum possible threshold
     * Subclasses can get more creative, if required.
     * </p>
     *
     * @param classification The source classification
     * @param candidate The candidate classifier
     * @param p The match probability
     *
     * @return True if this is a possible match
     */
    protected boolean isPossible(C classification, Classifier candidate, double p) {
        return p >= POSSIBLE_THESHOLD;
    }

    /**
     * Is this an accepted match, meaning that it will be used as a valid match
     * <p>
     * Generally, this means something above the acceptable threshold probability
     * Subclasses can get more creative, if required.
     * </p>
     *
     * @param classification The source classification
     * @param candidate The candidate classifier
     * @param p The match probability
     *
     * @return True if this is a possible match
     */
    protected boolean isAcceptable(C classification, Classifier candidate, double p) {
        return p >= ACCEPTABLE_THRESHOLD;
    }

    /**
     * Create an empty classification
     *
     * @return The empty classification
     */
    abstract public C createClassification();

    /**
     * Create an empty set of parameters
     *
     * @return The empty parameters
     */
    abstract public P createParameters();
}
