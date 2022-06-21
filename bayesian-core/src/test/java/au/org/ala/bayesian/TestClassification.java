package au.org.ala.bayesian;

import au.org.ala.bayesian.fidelity.CompositeFidelity;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@TraceDescriptor(identify = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder(alphabetic = true)
public class TestClassification implements Classification<TestClassification> {
    public String taxonID;
    public String class_;
    public String scientificName;
    public String vernacularName;
    public Integer rankID;
    public Integer rankRange;
    public TestEnum testEnum;
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
    @NonNull
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
    public Collection<Observation<?>> toObservations() {
        Collection<Observation<?>> obs = new ArrayList<>(12);

        if (this.taxonID != null)
            obs.add(new Observation<String>(true, TestFactory.TAXON_ID, this.taxonID));
        if (this.class_ != null)
            obs.add(new Observation<String>(true, TestFactory.CLASS_, this.class_));
        if (this.scientificName != null)
            obs.add(new Observation<String>(true, TestFactory.SCIENTIFIC_NAME, this.scientificName));
        if (this.vernacularName != null)
            obs.add(new Observation<String>(true, TestFactory.VERNACULAR_NAME, this.vernacularName));
        if (this.rankID != null)
            obs.add(new Observation<Integer>(true, TestFactory.RANK_ID, this.rankID));
        if (this.rankRange != null)
            obs.add(new Observation<Integer>(true, TestFactory.RANK_RANGE, this.rankRange));
        if (this.testEnum != null)
            obs.add(new Observation<TestEnum>(true, TestFactory.TEST_ENUM, this.testEnum));
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
    @JsonIgnore
    public Hints<TestClassification> getHints() {
        return new Hints<>();
    }

    @Override
    public <T> void addHint(Observable<T> observable, T value) {
    }

    @Override
    public void inferForSearch(@NonNull Analyser<TestClassification> analyser, @NonNull MatchOptions options) {
    }

    @Override
    public Fidelity<TestClassification> buildFidelity(TestClassification actual) throws InferenceException {
        CompositeFidelity<TestClassification> fidelity = new CompositeFidelity<>(this, actual);
        if (this.taxonID != null)
            fidelity.add(TestFactory.TAXON_ID.getAnalysis().buildFidelity(this.taxonID, actual.taxonID));
        if (this.class_ != null)
            fidelity.add(TestFactory.CLASS_.getAnalysis().buildFidelity(this.class_, actual.class_));
        if (this.scientificName != null)
            fidelity.add(TestFactory.SCIENTIFIC_NAME.getAnalysis().buildFidelity(this.scientificName, actual.scientificName));
        if (this.vernacularName != null)
            fidelity.add(TestFactory.VERNACULAR_NAME.getAnalysis().buildFidelity(this.vernacularName, actual.vernacularName));
        if (this.rankID != null)
            fidelity.add(TestFactory.RANK_ID.getAnalysis().buildFidelity(this.rankID, actual.rankID));
        if (this.rankRange != null)
            fidelity.add(TestFactory.RANK_RANGE.getAnalysis().buildFidelity(this.rankRange, actual.rankRange));
        if (this.testEnum != null)
            fidelity.add(TestFactory.TEST_ENUM.getAnalysis().buildFidelity(this.testEnum, actual.testEnum));
        return fidelity;
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
            this.taxonID = classifier.get(TestFactory.TAXON_ID);
        if (overwrite || this.class_ == null)
            this.class_ = classifier.get(TestFactory.CLASS_);
        if (overwrite || this.scientificName == null)
            this.scientificName = classifier.get(TestFactory.SCIENTIFIC_NAME);
        if (overwrite || this.vernacularName == null)
            this.vernacularName = classifier.get(TestFactory.VERNACULAR_NAME);
        if (overwrite || this.rankID == null)
            this.rankID = classifier.get(TestFactory.RANK_ID);
        if (overwrite || this.rankRange == null)
            this.rankRange = classifier.get(TestFactory.RANK_RANGE);
        if (overwrite || this.testEnum == null)
            this.testEnum = classifier.get(TestFactory.TEST_ENUM);
    }

    @Override
    public void write(Classifier classifier, boolean overwrite) throws BayesianException {
        if (overwrite) {
            classifier.clear(TestFactory.TAXON_ID);
            classifier.clear(TestFactory.CLASS_);
            classifier.clear(TestFactory.SCIENTIFIC_NAME);
            classifier.clear(TestFactory.VERNACULAR_NAME);
            classifier.clear(TestFactory.RANK_ID);
            classifier.clear(TestFactory.RANK_RANGE);
            classifier.clear(TestFactory.TEST_ENUM);
        }
        classifier.add(TestFactory.TAXON_ID, this.taxonID, false, false);
        classifier.add(TestFactory.CLASS_, this.class_, false, false);
        classifier.add(TestFactory.SCIENTIFIC_NAME, this.scientificName, false, false);
        classifier.add(TestFactory.VERNACULAR_NAME, this.vernacularName, false, false);
        classifier.add(TestFactory.RANK_ID, this.rankID, false, false);
        classifier.add(TestFactory.RANK_RANGE, this.rankRange, false, false);
        classifier.add(TestFactory.TEST_ENUM, this.testEnum, false, false);
    }
}
