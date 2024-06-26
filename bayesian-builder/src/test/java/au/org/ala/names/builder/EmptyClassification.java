package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.fidelity.CompositeFidelity;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * An empty classification for simple tests.
 */
public class EmptyClassification implements Classification<EmptyClassification> {
    @Getter
    private Issues issues = new Issues();

    @Override
    public @NonNull Term getType() {
        return DwcTerm.Taxon;
    }

    @Override
    public Collection<Observation<?>> toObservations() {
        return Collections.emptyList();
    }

    @Override
    public String getIdentifier() {
        return null;
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
    public String getName() {
        return null;
    }

    @Override
    public void inferForSearch(@NonNull Analyser analyser, @NonNull MatchOptions options) {
    }

    /**
     * Create a clone of this classification.
     *
     * @return The cloned classification
     */
    @Override
    @SneakyThrows
    public @NonNull EmptyClassification clone() {
        return (EmptyClassification) super.clone();
    }

    /**
     * Add an issue to the issues list.
     * <p>
     * Adding an issue should apply to the classification itself.
     * Shared issues lists need to be disambigauted before being modified.
     * </p>
     *
     * @param issue The issue to add
     */
    @Override
    public void addIssue(Term issue) {
        this.issues = this.issues.with(issue);
    }


    @Override
    public void addIssues(Issues issues) {
        this.issues = this.issues.merge(issues);
    }

    @Override
    public @NonNull Hints<EmptyClassification> getHints() {
        return new Hints<>();
    }

    @Override
    public <T> void addHint(Observable<T> observable, T value) {
    }

    @Override
    public boolean isValidCandidate(Classifier classifier) throws BayesianException {
        return true;
    }

    @Override
    public Fidelity<EmptyClassification> buildFidelity(EmptyClassification actual) {
        return new CompositeFidelity<>(this, actual);
    }

     @Override
    public List<List<Function<EmptyClassification, EmptyClassification>>> searchModificationOrder() {
        return Collections.emptyList();
    }

    @Override
    public List<List<Function<EmptyClassification, EmptyClassification>>> matchModificationOrder() {
        return Collections.emptyList();
    }

    @Override
    public List<List<Function<EmptyClassification, EmptyClassification>>> hintModificationOrder() {
        return Collections.emptyList();
    }

    @Override
    public void read(Classifier classifier, boolean overwrite) {
    }

    @Override
    public void write(Classifier classifier, boolean overwrite) {
    }
}
