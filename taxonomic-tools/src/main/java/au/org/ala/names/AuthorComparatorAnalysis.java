package au.org.ala.names;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.analysis.StringAnalysis;
import org.gbif.checklistbank.authorship.AuthorComparator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analysis for taxon concept authorship.
 * <p>
 * Use the GBIF author comparator to see if two authors are equivalent.
 * </p>
 * @deprecated This requires authorship parsing every time you do a comparsion.
 *             Consider {@link AuthorCanonicaliserDerivation} instead.
 */
@Deprecated
public class AuthorComparatorAnalysis extends StringAnalysis {
    /** The author comparator to use when checking authorship equality */
    private AuthorComparator comparator = AuthorComparator.createWithAuthormap();

    private static final String YEAR_GROUP = "(\\d\\d|\\d\\d\\d\\d)[.?]?";
    /** Detect a ", year" ending and extract the year */
    protected static final Pattern YEAR_PATTERN = Pattern.compile(",?\\s*(?:ms\\s+)?(?:" +
            YEAR_GROUP + "|" +
            "\\(" + YEAR_GROUP + "\\)|" +
            "\\[" + YEAR_GROUP + "\\]" +
            ")" +
            "\\s*(?:\\[" + YEAR_GROUP + "\\])?" +
            "$");

    private String firstYear(Matcher matcher) {
        if (matcher.start(1) >= 0)
            return matcher.group(1);
        if (matcher.start(2) >= 0)
            return matcher.group(2);
        if (matcher.start(3) >= 0)
            return matcher.group(3);
        return null;
    }

    /**
     * Test for equivalence.
     * <p>
     * Authors are declared equivalent based on the GBIF author comparator
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     * @return Null if not comparable, true if equivalent, false otherwise.
     * @throws InferenceException if unable to determine equivalence
     */
    @Override
    public Boolean equivalent(String value1, String value2) throws InferenceException {
        String year1 = null;
        String year2 = null;
        if (value1 == null || value2 == null)
            return null;
        if (value1.equalsIgnoreCase(value2)) // Quick check before going to all the effort
            return true;
        value1 = value1.trim();
        if (value1.startsWith("(") && value1.endsWith(")"))
            value1 = value1.substring(1, value1.length() - 1);
        value2 = value2.trim();
        if (value2.startsWith("(") && value2.endsWith(")"))
            value2 = value2.substring(1, value2.length() - 1);
        if (value1.equalsIgnoreCase(value2))
            return true;
        // Split out year
        Matcher matcher1 = YEAR_PATTERN.matcher(value1);
        if (matcher1.find()) {
            value1 = value1.substring(0, matcher1.start()).trim();
            year1 = this.firstYear(matcher1);
        }
        Matcher matcher2 = YEAR_PATTERN.matcher(value2);
        if (matcher2.find()) {
            value2 = value2.substring(0, matcher2.start()).trim();
            year2 = this.firstYear(matcher2);
        }
        switch (this.comparator.compare(value1, year1, value2, year2)) {
            case EQUAL:
                return true;
            case DIFFERENT:
                return false;
            default:
                return null;
        }
    }
}
