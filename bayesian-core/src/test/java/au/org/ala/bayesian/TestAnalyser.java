package au.org.ala.bayesian;

import au.org.ala.util.SimpleClassifier;

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
}
