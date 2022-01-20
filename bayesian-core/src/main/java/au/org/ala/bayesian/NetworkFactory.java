package au.org.ala.bayesian;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

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
    public List<Observable<?>> getObservables();

    /**
     * Get the observable that acts as the identifier for the classification
     *
     * @return The identifier observable
     */
    @NonNull
    public Optional<Observable<String>> getIdentifier();

    /**
     * Get the observable that acts as the name for the classification
     *
     * @return The name observable
     */
    @NonNull
    public Optional<Observable<String>> getName();

    /**
     * Get the observable that acts as the parent link for the classification
     *
     * @return An optional parent observable
     */
    @NonNull
    public Optional<Observable<String>> getParent();

    /**
     * Get the observable that acts as the accepted link for the classification
     *
     * @return An optional accepted observable
     */
    @NonNull
    public Optional<Observable<String>> getAccepted();

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
