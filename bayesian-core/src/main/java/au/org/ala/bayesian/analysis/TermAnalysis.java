package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwc.terms.UnknownTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analysis for a GBIF term in a vocabulary.
 */
public class TermAnalysis extends Analysis<Term, String, String> {
    private static final Logger logger = LoggerFactory.getLogger(TermAnalysis.class);

    /**
     * Get the class of object that this analyser handles.
     *
     * @return The class of analyser object
     */
    @Override
    public Class<Term> getType() {
        return Term.class;
    }

    /**
     * Get the class of object that this analyser handles.
     *
     * @return The class of analyser object
     */
    @Override
    public Class<String> getStoreType() {
        return String.class;
    }


    /**
     * Analyse this object, providing any special interpretation
     * required.
     *
     * @param value The value to analyse
     * @return The analysed value.
     */
    @Override
    public Term analyse(Term value) {
        return value;
    }

    /**
     * Convert this object into a string for storage
     * <p>
     * Since the type of term is determined by the field,
     * this is just the simple name.
     * </p>
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     *
     * @see Term#simpleName()
     */
    @Override
    public String toStore(Term value) {
        return value == null ? null : value.qualifiedName();
    }

    /**
     * Convert this object into a query
     * </p>
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     *
     * @see Term#simpleName()
     */
    @Override
    public String toQuery(Term value) {
        return this.toStore(value);
    }

    /**
     * Parse this value from storage.
     *
     * @param value The value
     * @return The parsed value
     */
    @Override
    public Term fromStore(String value) {
        return this.fromString(value);
    }

    /**
     * Compute a fidelity measure for this type of object.
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<Term> buildFidelity(Term original, Term actual) throws InferenceException {
        return original == null ? null : new SimpleFidelity<>(original, actual, original.equals(actual) ? 1.0 : 0.0);
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * Uses the GBIF {@link TermFactory} to look the term up.
     * </p>
     *
     * @param value The value
     * @return The parsed value
     */
    @Override
    public Term fromString(String value) {
        if (value == null || value.isEmpty())
            return null;
        Term term =  TermFactory.instance().findTerm(value);
        if (term instanceof UnknownTerm)
            logger.warn("Unknown term returned for " + value + " in " + this.getClass());
        return term;
    }
}
