package au.org.ala.bayesian;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

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
    @Override
    public void analyse(C classification, boolean strict) {
    }

    @Override
    public Set<String> analyseNames(Classifier classifier, Observable name, Optional<Observable> complete, Optional<Observable> additional, boolean canonical) {
        return Collections.emptySet();
    }
}
