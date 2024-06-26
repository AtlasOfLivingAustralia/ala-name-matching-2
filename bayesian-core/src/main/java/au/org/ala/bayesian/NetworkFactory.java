package au.org.ala.bayesian;

import lombok.NonNull;
import org.gbif.dwc.terms.Term;

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
     * Get the network identifier this factory is for.
     *
     * @return The network identifier
     */
    @NonNull String getNetworkId();

    /**
     * Get a list of all the observables used by the network.
     *
     * @return The observables list
     */
    @NonNull List<Observable<?>> getObservables();

    /**
     * Get the concept associated with this network
     *
     * @return The concept
     */
    @NonNull Term getConcept();

    /**
     * Get the observable that acts as the identifier for the classification
     *
     * @return The identifier observable
     */
    @NonNull Optional<Observable<String>> getIdentifier();

    /**
     * Get the observable that acts as the name for the classification
     *
     * @return The name observable
     */
    @NonNull Optional<Observable<String>> getName();

    /**
     * Get the observable that acts as the parent link for the classification
     *
     * @return An optional parent observable
     */
    @NonNull Optional<Observable<String>> getParent();

    /**
     * Get the observable that acts as the accepted link for the classification
     *
     * @return An optional accepted observable
     */
    @NonNull Optional<Observable<String>> getAccepted();

    /**
     * Get the list of observables that forms a unique key for the classification
     *
     * @return The list of key observables, or null for none
     */
    List<Observable<?>> getKey();

    /**
     * Get a list of all the issues that can be raised by this network.
     * <p>
     * The list should contain {@link au.org.ala.vocab.BayesianTerm#invalidMatch} and
     * {@link au.org.ala.vocab.BayesianTerm#illformedData} along with any network-specific issues.
     * </p>
     *
     * @return The observables list
     */
    @NonNull List<Term> getAllIssues();

    /**
     * Create a new, empty classification
     *
     * @return The classification
     */
    @NonNull C createClassification();

    /**
     * Create a new inferencer for the network
     *
     * @return The inferencer
     */
    @NonNull I createInferencer();

    /**
     * Create an optional evidence analyser for this network.
     *
     * @param config The configuration to use
     *
     * @return The analyser, or null for no analyser
     */
    Analyser<C> createAnalyser(AnalyserConfig config);

    /**
     * Create a matcher for the network.
     *
     * @param searcher The underlying searcher that finds matching classifications
     * @param config The matcher configuration (null for a default configuration)
     * @param analyserConfig The analyser configuration
     *
     * @return The new matcher
     */
    @NonNull <M extends MatchMeasurement> ClassificationMatcher<C, I, F, M> createMatcher(ClassifierSearcher searcher, ClassificationMatcherConfiguration config, AnalyserConfig analyserConfig);

    /**
     * Build a label for a classifier.
     * @param classifier
     * @return
     */
    default String buildLabel(Classifier classifier) {
        final StringBuilder builder = new StringBuilder(64);
        final String identifier = this.getIdentifier().map(i -> classifier.get(i)).orElse(null);
        final String name = this.getName().map(n -> classifier.get(n)).orElse(null);
        if (identifier != null) {
            builder.append(identifier);
        }
        if (name != null) {
            if (identifier != null) {
                builder.append(", ");
            }
            builder.append(name);
        }
        return builder.toString();
    }
}
