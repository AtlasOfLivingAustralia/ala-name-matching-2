package au.org.ala.bayesian;

import java.util.Optional;
import java.util.Set;

public class TestAnalyser implements Analyser<TestClassification> {
    /**
     * No extension
     */
    @Override
    public void analyseForIndex(TestClassification classification) throws InferenceException {
    }

    /**
     * No extension
     */
    @Override
    public void analyseForSearch(TestClassification classification) throws InferenceException {
    }

    /**
     * Build a collection of base names for the classification.
     */
    @Override
    public Set<String> analyseNames(Classifier classifier, Observable name, Optional<Observable> complete, Optional<Observable> additional, boolean canonical) throws InferenceException {
        return classifier.getAll(TestClassification.SCIENTIFIC_NAME);
    }
}
