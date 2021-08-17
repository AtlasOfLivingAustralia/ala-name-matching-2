package au.org.ala.bayesian;

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
    public void analyse(TestClassification classification) throws InferenceException, StoreException {
    }

    /**
     * Build a collection of base names for the classification.
     */
    @Override
    public Set<String> analyseNames(Classifier classifier) throws InferenceException, StoreException {
        return classifier.getAll(TestClassification.SCIENTIFIC_NAME);
    }
}
