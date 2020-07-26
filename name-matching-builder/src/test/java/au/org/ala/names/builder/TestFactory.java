package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import lombok.NonNull;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestFactory implements NetworkFactory<Classification, Parameters, Inferencer<Classification, Parameters>, TestFactory> {
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
    public Classification createClassification() {
        return new Classification() {
            @Override
            public Term getType() {
                return DwcTerm.Taxon;
            }

            @Override
            public Collection<Observation> toObservations() {
                return Collections.emptyList();
            }

            @Override
            public void infer() throws InferenceException {
            }

            @Override
            public void populate(Classifier classifier, boolean overwrite) throws InferenceException {
            }

            @Override
            public void translate(Classifier classifier) throws InferenceException, StoreException {
            }
        };
    }

    /**
     * Create a new inferencer for the network
     *
     * @return The inferencer
     */
    @Override
    public Inferencer createInferencer() {
        return new Inferencer() {
            @Override
            public double probability(Classification classification, Classifier classifier, Parameters parameters) throws InferenceException {
                return 0.0;
            }
        };
    }

    /**
     * Create a new, empty parameter set for the network
     *
     * @return The parameters
     */
    @Override
    public Parameters createParameters() {
        return new Parameters() {
            @Override
            public void load(double[] vector) {
            }

            @Override
            public double[] store() {
                return new double[0];
            }
        };
    }

    /**
     * Create an optional evidence analyser for this network.
     *
     * @return The analyser, or null for no analyser
     */
    @Override
    public EvidenceAnalyser<Classification> createAnalyser() {
        return null;
    }

    /**
     * Create a matcher for the network.
     *
     * @param searcher The underlying searcher that finds matching classifications
     * @return The new matcher
     */
    @Override
    public @NonNull ClassificationMatcher<Classification, Parameters, Inferencer<Classification, Parameters>, TestFactory> createMatcher(ClassifierSearcher searcher) {
        return null;
    }
}
