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
public class ClassificationMatcher<C extends Classification<C>, P extends Parameters, I extends Inferencer<C, P>, F extends NetworkFactory<C, P, I, F>> {
    /** The default possible theshold for something to be considered. @see isPossible */
    public static double POSSIBLE_THESHOLD = 0.1;
    /** The default acceptable threshold for something to be regarded as accepted. @see isAcceptable */
    public static double ACCEPTABLE_THRESHOLD = 0.99;

    private F factory;
    private ClassifierSearcher<?> searcher;
    private I inferencer;
    private Analyser<C> analyser;

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
    }

    public Match<C> findMatch(C classification) throws InferenceException, StoreException {
        classification.infer();
        List<? extends Classifier> candidates = this.searcher.search(classification);
        if (candidates.isEmpty())
            return null;
        List<Match<C>> results = new ArrayList<>(candidates.size());
        P parameters = this.factory.createParameters();
        for (Classifier candidate: candidates) {
            candidate.loadParameters(parameters);
            double p = this.inferencer.probability(classification, candidate, parameters);
            if (this.isPossible(classification, candidate, p)) {
                C candidateClassification = this.factory.createClassification();
                candidateClassification.read(candidate, true);
                Match<C> match = new Match<>(candidateClassification, candidate, p, candidateClassification.getIssues());
                if (this.isAcceptable(classification, candidate, p))
                    return match;
                results.add(match);
            }
        }
        if (results.isEmpty())
            return null;
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
}
