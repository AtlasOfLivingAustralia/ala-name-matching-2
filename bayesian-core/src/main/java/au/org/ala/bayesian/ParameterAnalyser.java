package au.org.ala.bayesian;

/**
 * Analyse the parameters needed by an entry.
 * <p>
 * This interface forms the base of actual parameter analysers.
 * </p>
 */
public interface ParameterAnalyser {
    /**
     * Get the total weight for all entries.
     *
     * @return The total weight
     */
    public double getTotalWeight();

    /**
     * Compute the prior probability of a field.
     * <p>
     * Subclasses need to be able to compute pr = weight(field = observation)/totalWeight
     * </p>
     *
     * @param observation The observation we are computing a probability for
     *
     * @return The probaility of the observation, by weight.
     *
     * @throws InferenceException if unable to compute the result
     */
    public double computePrior(Observation observation) throws InferenceException;

    /**
     * Computer a conditional probability for a observation
     * <p>
     * Subclasses need to be able to compute pr(observation | input1, inpput2, ...) = pr(observation, input1, input2, ...) / pr(input1, input2, ...)
     * </p>
     *
     * @param observation The observation
     * @param inputs The facts inputting into the result
     *
     * @return The conditional probability
     *
     * @throws InferenceException if unable to compute the result
     */
    public double computeConditional(Observation observation, Observation... inputs) throws InferenceException;

    /**
     * Get a fact from a classifier
     *
     * @param positive A positive or negative fact
     * @param observable The onservable
     * @param classifier The classifier to get results from
     *
     * @return The associated fact.
     *
     * @throws InferenceException if unable to get the observation data
     * @throws StoreException if unable to retrieve the data
     */
    public default Observation getObservation(boolean positive, Observable observable, Classifier classifier) throws StoreException {
        return new Observation(positive, observable, classifier.getAll(observable));
    }

}
