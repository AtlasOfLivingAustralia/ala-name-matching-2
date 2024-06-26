package au.org.ala.bayesian;

import au.org.ala.util.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * Provides application-specific analysis and extension of evidence supplied.
 * <p>
 * Subclasses can be used to fill out inferred evidence that doesn't
 * fit nicely into a network.
 * For example, it can be used to work out implied rank from a scientific name,
 * if one has not been specified.
 * </p>
 * <p>
 * Analysers are intended to be <em>services</em>.
 * They can be used by multiple clients to analyse
 * </p>
 *
 * @param <C> The classifier that this analyser handles
 *
 */
@Service
public interface Analyser<C extends Classification<C>> {
    /** The default name for the analyser configuration */
    static final String CONFIG_FILE = "analyser-config.json";

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required in preparation for building an index.
     * <p>
     * Generally, if there is a recoverable problem here, fix it.
     * And if the name looks weird, then that's what the source says.
     * </p>
     *
     * @param classifier The classification
     *
     * @throws InferenceException if an error occurs during inference
     */
    void analyseForIndex(Classifier classifier) throws InferenceException, StoreException;

    /**
     * Analyse the information in a classification and extend the classification
     * as required in preparation for search/inference.
     *
     * @param classification The classification
     * @param options Options on what analysis to perform
     *
     * @throws InferenceException if an error occurs during inference
     */
    void analyseForSearch(C classification, MatchOptions options) throws InferenceException;

    /**
     * Build a collection of base names for the classification.
     * <p>
     * If a classification can be referred to in multiple ways, this method
     * builds the various ways of referring to the classification.
     * </p>
     * <p>
     * As a general principle, if {@link #analyseForIndex(Classifier)} and
     * {@link #analyseForSearch(Classification, MatchOptions)} would produce different results,
     * this method should ensure that whatever is in the search is included in the
     * set of names.
     * </p>
     *
     * @param classifier The classification
     * @param name The observable that gives the name
     * @param complete The observable that gives the complete name
     * @param disambiguator The observable that gives disambiguator disambiguation, geneerally complete = name + ' ' + disambiguator
     * @param canonical Only include canonical names
     *
     * @return All the names that refer to the classification
     *
     * @throws InferenceException if unable to analyuse the names
     */
    Set<String> analyseNames(Classifier classifier, Observable<String> name, Optional<Observable<String>> complete, Optional<Observable<String>> disambiguator, boolean canonical) throws InferenceException;

    /**
     * Decide whether to accept a synonym or not.
     * <p>
     * Some classifiers are synonyms of classifications but we really don't want to use them, since
     * they represent unusual or inaccurate synonyms.
     * </p>
     *
     * @param base The base classifier the synonym is for
     * @param candidate The classifier for the synonym
     *
     * @return True if this is an acceptable synonym.
     */
    boolean acceptSynonym(Classifier base, Classifier candidate);

    /**
     * Write the configuration needed by this analyser into a directory.
     * <p>
     * This may include a {@link AnalyserConfig} and any ancillary files that may be needed.
     * The default implementation returns null for no configuration needed.
     * </p>
     *
     * @param directory The directory to write to
     *
     * @return The file the configuration has been written to, ot null for nothing written.
     *
     * @throws IOException if unable to write the configuration
     */
    default File writeConfiguration(File directory) throws IOException {
        return null;
    }
}
