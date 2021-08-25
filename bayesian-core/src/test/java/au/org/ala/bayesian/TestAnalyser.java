package au.org.ala.bayesian;

import java.util.Optional;
import java.util.Set;

public class TestAnalyser implements Analyser<TestClassification> {
    /**
     * Analyse the information in a classification and extend the classification
     * as required.
     *
     * @param classification The classification
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException     if an error occurs updating the classifier
     */
    @Override
    public void analyse(TestClassification classification, boolean strict) throws InferenceException, StoreException {
    }

    /**
     * Build a collection of base names for the classification.
     */
    @Override
    public Set<String> analyseNames(Classifier classifier, Observable name, Optional<Observable> complete, Optional<Observable> additional, boolean canonical) throws InferenceException, StoreException {
        return classifier.getAll(TestClassification.SCIENTIFIC_NAME);
    }
}
