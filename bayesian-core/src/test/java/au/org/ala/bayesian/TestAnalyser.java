package au.org.ala.bayesian;

import java.util.Optional;
import java.util.Set;

public class TestAnalyser implements Analyser<TestClassification> {
    /**
     * No extension
     */
    @Override
    public void analyseForIndex(Classifier classifier) throws InferenceException {
    }

    /**
     * No extension
     */
    @Override
    public void analyseForSearch(TestClassification classification, MatchOptions options) throws InferenceException {
    }

    /**
     * Build a collection of base names for the classification.
     */
    @Override
    public Set<String> analyseNames(Classifier classifier, Observable<String> name, Optional<Observable<String>> complete, Optional<Observable<String>> additional, boolean canonical) throws InferenceException {
        return classifier.getAll(TestFactory.SCIENTIFIC_NAME);
    }

    @Override
    public boolean acceptSynonym(Classifier base, Classifier candidate) {
        return true;
    }

}
