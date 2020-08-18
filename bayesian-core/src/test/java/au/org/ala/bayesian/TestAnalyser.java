package au.org.ala.bayesian;

import au.org.ala.util.SimpleClassifier;

public class TestAnalyser implements Analyser<TestClassification> {
    /**
     * Analyse the information in a classifier and extend the classifier
     * as required.
     *
     * @param classifier The classifier
     * @param issues     A store of issues associated with analysis and matching
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException     if an error occurs updating the classifier
     */
    @Override
    public void analyse(Classifier classifier, Issues issues) throws InferenceException, StoreException {
    }

    /**
     * Analyse the information in a classification and extend the classification
     * as required.
     *
     * @param classification The classification
     * @param issues         A store of issues associated with analysis and matching
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException     if an error occurs updating the classifier
     */
    @Override
    public void analyse(TestClassification classification, Issues issues) throws InferenceException, StoreException {
        SimpleClassifier classifier = new SimpleClassifier();
        classification.translate(classifier);
        this.analyse(classifier, issues);
        classification.populate(classifier, true);
    }
}
