package au.org.ala.bayesian;

import au.org.ala.util.BasicNormaliser;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.util.*;
import java.util.function.Function;

public class TestClassification implements Classification<TestClassification> {
    public static final Term RANK_ID_TERM = TermFactory.instance().findTerm("rankID");
    public static final Term RANK_RANGE_TERM = TermFactory.instance().findTerm("rankRange");
    public static final Term TEST_ENUM_TERM = TermFactory.instance().findTerm("testEnum");
    public static final Normaliser NORMALISER = new BasicNormaliser("basic", true, true, true, true, false);
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

    public String taxonID;
    public String class_;
    public String scientificName;
    public String vernacularName;
    public Integer rankID;
    public Integer rankRange;
    public TestEnum testEnum;
    @Getter
    private TestAnalyser analyser = new TestAnalyser();
    @Getter
    private Issues issues = new Issues();

    /**
     * Create a clone of this classification.
     *
     * @return The cloned classification
     */
    @SneakyThrows
    @Override
    public @NonNull TestClassification clone() {
        return (TestClassification) super.clone();
    }

    @Override
    public Term getType() {
        return DwcTerm.Taxon;
    }

    @Override
    public @NonNull String getIdentifier() {
        return this.taxonID;
    }

    @Override
    public String getParent() {
        return null;
    }

    @Override
    public String getAccepted() {
        return null;
    }

    @Override
    public @NonNull String getName() {
        return this.scientificName;
    }

    @Override
    public Collection<Observation> toObservations() {
        Collection<Observation> obs = new ArrayList<>(12);

        if (this.taxonID != null)
            obs.add(new Observation(true, TAXON_ID, this.taxonID));
        if (this.class_ != null)
            obs.add(new Observation(true, CLASS_, this.class_));
        if (this.scientificName != null)
            obs.add(new Observation(true, SCIENTIFIC_NAME, this.scientificName));
        if (this.vernacularName != null)
            obs.add(new Observation(true, VERNACULAR_NAME, this.vernacularName));
        if (this.rankID != null)
            obs.add(new Observation(true, RANK_ID, this.rankID));
        if (this.rankRange != null)
            obs.add(new Observation(true, RANK_RANGE, this.rankRange));
        if (this.testEnum != null)
            obs.add(new Observation(true, TEST_ENUM, this.testEnum));
        return obs;
    }

    @Override
    public void addIssue(Term issue) {
        this.issues = this.issues.with(issue);
    }

    @Override
    public void addIssues(Issues issues) {
        this.issues = this.issues.merge(issues);
    }

    @Override
    public Hints<TestClassification> getHints() {
        return new Hints<>();
    }

    @Override
    public <T> void addHint(Observable observable, T value) {
    }

    @Override
    public void inferForSearch(Analyser<TestClassification> analyser) {
    }

    /**
     * The order in which to modify this classification for searches.
     * <p>
     * Returned is a list of functions that will take a classification and return
     * a modified classification
     * </p>
     *
     * @return The modification options for the classifier
     */
    @Override
    public List<List<Function<TestClassification, TestClassification>>> searchModificationOrder() {
        List<List<Function<TestClassification, TestClassification>>> modifiers = new ArrayList<>();
        return modifiers;
    }

    /**
     * The order in which to modify this classification.
     * <p>
     * Returned is a list of functions that will take a classification and return
     * a modified classification
     * </p>
     *
     * @return The modification options for the classifier
     */
    @Override
    public List<List<Function<TestClassification, TestClassification>>> matchModificationOrder() {
        List<List<Function<TestClassification, TestClassification>>> modifiers = new ArrayList<>();
        if (this.class_ != null) {
            modifiers.add(Arrays.asList(
                null,
                c -> { TestClassification nc = c.clone(); nc.class_ = null; nc.addIssue(DwcTerm.class_); return nc;}
            ));
        }
        if (this.vernacularName != null) {
            modifiers.add(Arrays.asList(
                null,
                c -> { TestClassification nc = c.clone(); nc.vernacularName = null; nc.addIssue(DwcTerm.vernacularName); return nc;}
            ));
        }
        return modifiers;
    }

    @Override
    public List<List<Function<TestClassification, TestClassification>>> hintModificationOrder() {
        List<List<Function<TestClassification, TestClassification>>> modifiers = new ArrayList<>();
        return modifiers;
    }

    @Override
    public void read(Classifier classifier, boolean overwrite) {
        if (overwrite || this.taxonID == null)
            this.taxonID = classifier.get(TAXON_ID);
        if (overwrite || this.class_ == null)
            this.class_ = classifier.get(CLASS_);
        if (overwrite || this.scientificName == null)
            this.scientificName = classifier.get(SCIENTIFIC_NAME);
        if (overwrite || this.vernacularName == null)
            this.vernacularName = classifier.get(VERNACULAR_NAME);
        if (overwrite || this.rankID == null)
            this.rankID = classifier.get(RANK_ID);
        if (overwrite || this.rankRange == null)
            this.rankRange = classifier.get(RANK_RANGE);
        if (overwrite || this.testEnum == null)
            this.testEnum = classifier.get(TEST_ENUM);
    }

    @Override
    public void write(Classifier classifier, boolean overwrite) throws BayesianException {
        if (overwrite) {
            classifier.clear(TAXON_ID);
            classifier.clear(CLASS_);
            classifier.clear(SCIENTIFIC_NAME);
            classifier.clear(VERNACULAR_NAME);
            classifier.clear(RANK_ID);
            classifier.clear(RANK_RANGE);
            classifier.clear(TEST_ENUM);
        }
        classifier.add(TAXON_ID, this.taxonID, false);
        classifier.add(CLASS_, this.class_, false);
        classifier.add(SCIENTIFIC_NAME, this.scientificName, false);
        classifier.add(VERNACULAR_NAME, this.vernacularName, false);
        classifier.add(RANK_ID, this.rankID, false);
        classifier.add(RANK_RANGE, this.rankRange, false);
        classifier.add(TEST_ENUM, this.testEnum, false);
    }
}
