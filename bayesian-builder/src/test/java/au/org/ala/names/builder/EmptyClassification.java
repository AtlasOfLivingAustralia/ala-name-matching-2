package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import lombok.Getter;
import lombok.NonNull;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.Collection;
import java.util.Collections;

/**
 * An empty classification for simple tests.
 */
public class EmptyClassification implements Classification<EmptyClassification> {
    @Getter
    private NullAnalyser<EmptyClassification> analyser = new NullAnalyser<>();
    @Getter
    private Issues issues = new Issues();

    @Override
    public @NonNull Term getType() {
        return DwcTerm.Taxon;
    }

    @Override
    public Collection<Observation> toObservations() {
        return Collections.emptyList();
    }

    @Override
    public void infer() {

    }

    @Override
    public void read(Classifier classifier, boolean overwrite) {
    }

    @Override
    public void write(Classifier classifier, boolean overwrite) {
    }
}
