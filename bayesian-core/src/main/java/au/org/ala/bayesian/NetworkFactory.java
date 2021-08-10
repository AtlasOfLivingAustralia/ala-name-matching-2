package au.org.ala.bayesian;

import lombok.NonNull;

import java.util.List;

/**
 * Interface to objects that create network information.
 *
 * @param <C> The classifier to use
 * @param <I> The inferencer class to user
 */
public interface NetworkFactory<C extends Classification<C>, I extends Inferencer<C>, F extends NetworkFactory<C, I, F>> {
    /**
     * Get a list of all the observables used by the network.
     *
     * @return The observables list
     */
    @NonNull
    public List<Observable> getObservables();

    /**
     * Create a new, empty classification
     *
     * @return The classification
     */
    @NonNull
    public C createClassification();

    /**
     * Create a new inferencer for the network
     *
     * @return The inferencer
     */
    @NonNull
    public I createInferencer();

    /**
     * Create an optional evidence analyser for this network.
     *
     * @return The analyser, or null for no analyser
     */
    public Analyser<C> createAnalyser();

    /**
     * Create a matcher for the network.
     *
     * @param searcher The underlying searcher that finds matching classifications
     *
     * @return The new matcher
     */
    @NonNull
    public ClassificationMatcher<C, I, F> createMatcher(ClassifierSearcher searcher);
}
