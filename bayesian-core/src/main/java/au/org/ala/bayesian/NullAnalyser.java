package au.org.ala.bayesian;

/**
 * A default, null analyser that performs no special analysis.
 * <p>
 * This class is present so that there can always be an analyser object.
 * If no analyser is specified, the null analyser is used.
 * </p>
 *
 * @param <C> The classification type.
 */
public class NullAnalyser<C extends Classification> implements Analyser<C> {
    /**
     * Null analysis on a classifier.
     *
     * @param classifier The classifier
     * @param issues     A store of issues associated with analysis and matching
     */
    @Override
    public void analyse(Classifier classifier, Issues issues) {
    }

    /**
     * Null analysis on a classification
     *
     * @param classification The classification
     * @param issues         A store of issues associated with analysis and matching
     */
    @Override
    public void analyse(C classification, Issues issues) {
    }
}
