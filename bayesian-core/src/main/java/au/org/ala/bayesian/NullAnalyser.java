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
     * Null analysis on a classification
     *
     * @param classification The classification
     */
    @Override
    public void analyse(C classification) {
    }
}
