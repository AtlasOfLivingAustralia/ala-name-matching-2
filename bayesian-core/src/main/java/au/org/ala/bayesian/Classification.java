package au.org.ala.bayesian;

import lombok.NonNull;
import org.gbif.dwc.terms.Term;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * The base class for classifications.
 * <p>
 * Classifications hold the vector of evidence that make up a individual piece of data.
 * </p>
 *
 * @param <C> The type of classification in use
 */
public interface Classification<C extends Classification<C>> extends Cloneable {
    /**
     * Create a clone of this classification.
     *
     * @return The cloned classification
     */
    @NonNull C clone();
    /**
     * Get the type of the classification
     *
     * @return The sort of thing this classification is supposed to match.
     */
    @NonNull Term getType();

    /**
     * Get the identifier of this classification
     *
     * @return The identifier
     */
    String getIdentifier();

    /**
     * Get the parent identifier of this classification
     *
     * @return The parent identifier or null for none
     */
    String getParent();

    /**
     * Get the accepted value of this classification
     *
     * @return The accepted identifier or null for none
     */
    String getAccepted();

    /**
     * Get the base name of this classification
     *
     * @return The name
     */
    String getName();

    /**
     * Get any issues recorded with this classification.
     * <p>
     * The analyser in {@link #inferForSearch(Analyser, MatchOptions)} may add issues as required.
     * </p>
     *
     * @return Any issues associated with the classification/
     */
    @NonNull Issues getIssues();

    /**
     * Add an issue to the issues list.
     * <p>
     * Adding an issue should apply to the classification itself.
     * Shared issues lists need to be disambiguated before being modified.
     * </p>
     *
     * @param issue The issue to add
     */
    void addIssue(Term issue);

    /**
     * Add a set of issues to the issues list.
     * <p>
     * Adding an issue should apply to the classification itself.
     * Shared issues lists need to be disambigauted before being modified.
     * </p>
     *
     * @param issues The issues to add
     */
    void addIssues(Issues issues);

    /**
     * Get any hints associated with this classification.
     *
     * @return The hints object
     */
    @NonNull Hints<C> getHints();

    /**
     * Get a set of hints about the properties of a classification.
     * <p>
     * These are possible values for an observable that are not strong enough or are subject to
     * enough uncertainty that they cannot be reliably used for inference but can be used to choose
     * between multiple candidates.
     * </p>
     * <p>
     * Hints are usually included by an {@link Analyser}.
     * </p>
     *
     * @return Any hints or null for none
     */
    List<List<Function<C, C>>> hintModificationOrder();

    /**
     * Add a hint to the classification.
     *
     * @param observable The observable the hint applies to
     * @param value The hint value
     *
     * @param <T> The type of hint
     *
     * @see #hintModificationOrder()
     */
    <T> void addHint(Observable<T> observable, T value);

    /**
     * Get a list of observations that match this classification.
     * <p>
     * This can be used to query an underlying name matcher for candidiate matches.
     * </p>
     *
     * @return This classification as a list of observations
     */
    Collection<Observation<?>> toObservations();

    /**
     * Infer empty elements of the classification from the network definition
     * in preparation for using this as a search template.
     * <p>
     * This method is usually generated to implement any derivations that are
     * specified by {@link Observable#getDerivation()}.
     * The method can be used to perform common derivations without requiring further coding.
     * </p>
     *
     * @param analyser The analser to use
     * @param options The options for matching
     *
     * @throws BayesianException if unable to calculate an inferred value or retrieve a source value
     */
    void inferForSearch(@NonNull Analyser<C> analyser, @NonNull MatchOptions options) throws BayesianException;

    /**
     * Does this classification roughly match a classifier?
     * <p>
     * This method usually does an approximate name check and a signature check.
     * </p>
     *
     * @param classifier The classifier to check
     *
     * @return True if this classifier is a rough match to the classification
     *
     * @throws BayesianException if there is a problem checking the candidate
     *
     */
    boolean isValidCandidate(Classifier classifier) throws BayesianException;

    /**
     * Build a fidelity model for this (original) classification against an actual classification.
     * <p>
     * The fidelity model can be used to build an estimate of how much the matched classification
     * has been messed about before a match could be found.
     * </p>
     *
     * @param actual The acutal classification used
     *
     * @return A fidelity measure for the classification.
     */
    Fidelity<C> buildFidelity(C actual) throws InferenceException;

    /**
     *
     * Read this classification from a classifier.
     * <p>
     * This allows a classification to be built that matches the classifier.
     * </p>
     * @param classifier The classifier that contains the original data
     * @param overwrite Overwrite what is already in the classification
     *
     * @throws BayesianException if unable to compute the population value or unable to populate the classifier
     */
    void read(Classifier classifier, boolean overwrite) throws BayesianException;

    /**
     * Write this classification into a classifier.
     *
     * @param classifier The empty classifier to populate
     * @param overwrite Overwrite what is already in the classifier
     *
     * @throws InferenceException if unable to translate or unable to store the translation
     */
    void write(Classifier classifier, boolean overwrite) throws BayesianException;


    /**
     * The order in which to modify this classification when attempting source searches.
     * <p>
     * Source modifications assume that the search for possible candidates is needs to be done again.
     * </p>
     * <p>
     * Returned is a list of functions that will take a classification and return
     * a modified classification
     * </p>
     * @return The list of lists of search modifications to try
     */
    List<List<Function<C, C>>> searchModificationOrder();

    /**
     * The order in which to modify this classification when attempting matches.
     * <p>
     * Match modifications assume that the search for possible candidates is acceptable and
     * need not be done again.
     * </p>
     * <p>
     * Returned is a list of functions that will take a classification and return
     * a modified classification
     * </p>
     * @return The list of lists of match modifications to try
     */
    List<List<Function<C, C>>> matchModificationOrder();
}
