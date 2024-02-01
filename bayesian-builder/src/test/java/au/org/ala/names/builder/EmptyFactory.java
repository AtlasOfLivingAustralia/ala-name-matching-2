package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.BayesianTerm;
import lombok.NonNull;
import org.gbif.dwc.terms.Term;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EmptyFactory implements NetworkFactory<EmptyClassification, EmptyInferencer, EmptyFactory> {
    @Override
    public @NonNull String getNetworkId() {
        return "empty";
    }

    @Override
    public @NonNull Term getConcept() {
        return BayesianTerm.Concept;
    }

    @Override
    public @NonNull List<Observable<?>> getObservables() {
        return Collections.emptyList();
    }


    @Override
    public @NonNull List<Term> getAllIssues() {
        return Arrays.asList(BayesianTerm.illformedData, BayesianTerm.invalidMatch);
    }

    @Override
    public @NonNull Optional<Observable<String>> getIdentifier() {
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<Observable<String>> getName() {
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<Observable<String>> getParent() {
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<Observable<String>> getAccepted() {
        return Optional.empty();
    }

    @Override
    public List<Observable<?>> getKey() {
        return null;
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
    public Analyser<EmptyClassification> createAnalyser(AnalyserConfig config) {
        return new NullAnalyser<>();
    }

    /**
     * Create a matcher for the network.
     *
     * @param searcher The underlying searcher that finds matching classifications
     * @return The new matcher
     */
    @Override
    public @NonNull ClassificationMatcher<EmptyClassification, EmptyInferencer, EmptyFactory, MatchMeasurement> createMatcher(ClassifierSearcher searcher, ClassificationMatcherConfiguration config, AnalyserConfig analyserConfig) {
        return null;
    }
}
