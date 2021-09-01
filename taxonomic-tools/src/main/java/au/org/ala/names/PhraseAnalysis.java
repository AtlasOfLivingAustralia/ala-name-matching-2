package au.org.ala.names;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.analysis.StringAnalysis;
import org.gbif.checklistbank.authorship.AuthorComparator;

import java.util.regex.Pattern;

/**
 * Analysis for phrase names and vouchers.
 * <p>
 * Eliminate all but the most basic elements.
 * </p>
 */
public class PhraseAnalysis extends StringAnalysis {
    private static Pattern SINE_NOMINE = Pattern.compile("s\\.n\\.");
    private static Pattern INVALID_CHARS = Pattern.compile("[^A-Za-z0-9]+");

    /**
     * Analyse this object, providing any special interpretation
     * required.
     * <p>
     * This provides a canonical version of the
     * Remove any sine nomine annotations
     * Remove any non-alphanumeric characters and make upper case.
     * </p>
     *
     * @param value The value to analyse
     * @return The analysed value.
     * @throws InferenceException if unable to analyse the value
     */
    @Override
    public String analyse(String value) throws InferenceException {
        value = super.analyse(value);
        if (value == null)
            return null;
        value = SINE_NOMINE.matcher(value).replaceAll("");
        value = INVALID_CHARS.matcher(value).replaceAll("");
        value = value.toUpperCase();
        return value;
    }
}
