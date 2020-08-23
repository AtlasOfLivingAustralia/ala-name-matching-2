package au.org.ala.bayesian;

abstract public interface Inferencer<C extends Classification, P extends Parameters> {
    /**
     * Calculate the probability of a match between a classification and a classifier.
     *
     * @param classification The classification
     * @param classifier The classifier to match
     * @param parameters The probability parameters
     *
     * @return The probability of a match
     *
     * @throws InferenceException if unable to calculate the probability
     * @throws StoreException if unable to get a value from the classifier
     */
    public double probability(C classification, Classifier classifier, P parameters) throws StoreException, InferenceException;
}
