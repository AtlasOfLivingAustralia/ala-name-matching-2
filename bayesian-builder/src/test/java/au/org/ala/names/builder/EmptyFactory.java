package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;

public class EmptyFactory implements NetworkFactory<EmptyClassification, EmptyInferencer, EmptyFactory> {
    @Override
    public @NonNull List<Observable> getObservables() {
        return Collections.emptyList();
    }

    /**
     * Create a new, empty classification
     *
     * @return The classification
     */
    @Override
    public EmptyClassification createClassification() {
        return new EmptyClassification();
    }

    /**
     * Create a new inferencer for the network
     *
     * @return The inferencer
     */
    @Override
    public EmptyInferencer createInferencer() {
        return new EmptyInferencer();
    }

    /**
     * Create an optional evidence analyser for this network.
     *
     * @return The analyser, or null for no analyser
     */
    @Override
    public Analyser<EmptyClassification> createAnalyser() {
        return new NullAnalyser<>();
    }

    /**
     * Create a matcher for the network.
     *
     * @param searcher The underlying searcher that finds matching classifications
     * @return The new matcher
     */
    @Override
    public @NonNull ClassificationMatcher<EmptyClassification, EmptyInferencer, EmptyFactory> createMatcher(ClassifierSearcher searcher) {
        return null;
    }
}
