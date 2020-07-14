package au.org.ala.bayesian;

import lombok.NonNull;

import java.util.List;

/**
 * Interface to objects that create network information.
 *
 * @param <C> The classifier to use
 * @param <P> The parameters class to use
 * @param <I> The inferencer class to user
 */
public interface NetworkFactory<C extends Classification, P extends Parameters, I extends Inferencer<C, P>> {
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
     * Create a new, empty parameter set for the network
     *
     * @return The parameters
     */
    @NonNull
    public P createParameters();

    /**
     * Create an optional evidence analyser for this network.
     *
     * @return The analyser, or null for no analyser
     */
    public EvidenceAnalyser<C> createAnalyser();

    /**
     * Create a matcher for the network.
     *
     * @param searcher The underlying searcher that finds matching classifications
     *
     * @return The new matcher
     */
    @NonNull
    public ClassificationMatcher<C, P, I> createMatcher(ClassifierSearcher searcher);
}
