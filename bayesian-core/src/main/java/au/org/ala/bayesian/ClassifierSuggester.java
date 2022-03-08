package au.org.ala.bayesian;

import au.org.ala.util.Service;
import lombok.Builder;
import lombok.Value;
import org.gbif.dwc.terms.Term;

import java.util.Comparator;
import java.util.List;

/**
 * A suggester for classifiers that fit a classification.
 * <p>
 * The suggester looks for names and provides a selection of candidates.
 * </p>
 *
 * @param <C> The type of classification used to search
 */
@Service
public abstract class ClassifierSuggester<C extends Classifier> implements AutoCloseable {
    /**
     * Build a suggestion list for a fragment of text.
     *
     * @param fragment The fragment
     * @param size The number of results to return
     * @param includeSynonyms Include synonym matches
     *
     * @return A list of suggestions that match the fragment
     *
     * @throws BayesianException
     */
    public abstract List<Suggestion<C>> suggest(String fragment, int size, boolean includeSynonyms) throws BayesianException;

    @Value
    public static class Suggestion<C extends Classifier> {
        /** Score order with highest score first */
        public static final Comparator<Suggestion> SCORE_ORDER = (s1, s2) -> Double.compare(s2.getScore(), s1.getScore());

        /** The match score */
        private double score;
        /** The matching string for the fragment */
        private String name;
        /** The document type */
        private Term type;
        /** The actual match */
        private C match;
        /** Any synonym */
        private C synonym;

        public Suggestion(double score, String name, Term type, C match, C synonym) {
            this.score = score;
            this.name = name;
            this.type = type;
            this.match = match;
            this.synonym = synonym;
        }
    }
}
