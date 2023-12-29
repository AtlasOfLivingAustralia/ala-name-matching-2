package au.org.ala.bayesian;

import au.org.ala.util.BasicNormaliser;
import au.org.ala.vocab.BayesianTerm;
import lombok.NonNull;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TestFactory implements NetworkFactory<TestClassification, TestInferencer, TestFactory> {
    public static final Normaliser NORMALISER = new BasicNormaliser("basic", true, false, true, true, true, false);

    public static final Term RANK_ID_TERM = TermFactory.instance().findTerm("rankID");
    public static final Term RANK_RANGE_TERM = TermFactory.instance().findTerm("rankRange");
    public static final Term TEST_ENUM_TERM = TermFactory.instance().findTerm("testEnum");
    public static final Observable<String> TAXON_ID = Observable.string(DwcTerm.taxonID);
    public static final Observable<String> CLASS_ = Observable.string(DwcTerm.class_);
    public static final Observable<String> SCIENTIFIC_NAME = Observable.string(DwcTerm.scientificName);
    public static final Observable<String> VERNACULAR_NAME = Observable.string(DwcTerm.vernacularName);
    public static final Observable<Integer> RANK_ID = Observable.integer(RANK_ID_TERM);
    public static final Observable<Integer> RANK_RANGE = Observable.integer(RANK_RANGE_TERM);
    public static final Observable<TestEnum> TEST_ENUM = Observable.enumerated(TestEnum.class, TEST_ENUM_TERM);
    public static final List<Observable<?>> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
            TAXON_ID,
            CLASS_,
            SCIENTIFIC_NAME,
            VERNACULAR_NAME,
            RANK_ID,
            RANK_RANGE,
            TEST_ENUM
    ));

    static {
        TAXON_ID.setMultiplicity(Observable.Multiplicity.REQUIRED);
        TAXON_ID.setStyle(Observable.Style.IDENTIFIER);
        CLASS_.setNormaliser(NORMALISER);
        CLASS_.setStyle(Observable.Style.PHRASE);
        SCIENTIFIC_NAME.setMultiplicity(Observable.Multiplicity.REQUIRED_MANY);
        SCIENTIFIC_NAME.setStyle(Observable.Style.PHRASE);
        SCIENTIFIC_NAME.setNormaliser(NORMALISER);
        VERNACULAR_NAME.setNormaliser(NORMALISER);
        VERNACULAR_NAME.setStyle(Observable.Style.PHRASE);
        RANK_ID.setType(Integer.class);
        RANK_RANGE.setType(Integer.class);
        RANK_RANGE.setAnalysis(new TestRangeAnalysis());
        TEST_ENUM.setType(TestEnum.class);
    }

    private static final List<Observable<?>> KEY = Arrays.asList(TAXON_ID);

    /**
     * Get the network identifier this factory is for.
     *
     * @return The network identifier
     */
    @Override
    public @NonNull String getNetworkId() {
        return "test";
    }

    /**
     * Get a list of all the observables used by the network.
     *
     * @return The observables list
     */
    @Override
    public @NonNull List<Observable<?>> getObservables() {
        return OBSERVABLES;
    }

    /**
     * Get the concept associated with this network
     *
     * @return The concept
     */
    @Override
    public @NonNull Term getConcept() {
        return DwcTerm.Taxon;
    }

    /**
     * Get the observable that acts as the identifier for the classification
     *
     * @return The identifier observable
     */
    @Override
    public @NonNull Optional<Observable<String>> getIdentifier() {
        return Optional.of(TAXON_ID);
    }

    /**
     * Get the observable that acts as the name for the classification
     *
     * @return The name observable
     */
    @Override
    public @NonNull Optional<Observable<String>> getName() {
        return Optional.of(SCIENTIFIC_NAME);
    }

    /**
     * Get the observable that acts as the parent link for the classification
     *
     * @return An optional parent observable
     */
    @Override
    public @NonNull Optional<Observable<String>> getParent() {
        return Optional.empty();
    }

    /**
     * Get the observable that acts as the accepted link for the classification
     *
     * @return An optional accepted observable
     */
    @Override
    public @NonNull Optional<Observable<String>> getAccepted() {
        return Optional.empty();
    }

    @Override
    public List<Observable<?>> getKey() {
        return KEY;
    }

    /**
     * Get a list of all the issues that can be raised by this network.
     * <p>
     * The list should contain {@link BayesianTerm#invalidMatch} and
     * {@link BayesianTerm#illformedData} along with any network-specific issues.
     * </p>
     *
     * @return The observables list
     */
    @Override
    public @NonNull List<Term> getAllIssues() {
        return Collections.emptyList();
    }

    /**
     * Create a new, empty classification
     *
     * @return The classification
     */
    @Override
    public @NonNull TestClassification createClassification() {
        return new TestClassification();
    }

    /**
     * Create a new inferencer for the network
     *
     * @return The inferencer
     */
    @Override
    public @NonNull TestInferencer createInferencer() {
        return new TestInferencer();
    }

    /**
     * Create an optional evidence analyser for this network.
     *
     * @return The analyser, or null for no analyser
     */
    @Override
    public Analyser<TestClassification> createAnalyser() {
        return new TestAnalyser();
    }

    /**
     * Create a matcher for the network.
     *
     * @param searcher The underlying searcher that finds matching classifications
     * @param config   The matcher configuration (null for a default configuration)
     * @return The new matcher
     */
    @Override
    public @NonNull <M extends MatchMeasurement> ClassificationMatcher<TestClassification, TestInferencer, TestFactory, M> createMatcher(ClassifierSearcher searcher, ClassificationMatcherConfiguration config) {
        return new ClassificationMatcher<>(this, searcher, config);
    }
}
