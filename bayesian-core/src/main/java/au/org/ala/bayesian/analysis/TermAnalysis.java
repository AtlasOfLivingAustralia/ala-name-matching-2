package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwc.terms.UnknownTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analysis for a GBIF term in a vocabulary.
 */
public class TermAnalysis extends Analysis<Term> {
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
    public String toString(Term value) {
        return value == null ? null : value.simpleName();
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
        Term term =  TermFactory.instance().findTerm(value);
        if (term instanceof UnknownTerm)
            logger.warn("Unknown term returned for " + value + " in " + this.getClass());
        return term;
    }
}
