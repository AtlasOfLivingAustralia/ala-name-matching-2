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
 */
public class AuthorAnalysis extends StringAnalysis {
    /** The author comparator to use when checking authorship equality */
    private AuthorComparator comparator = AuthorComparator.createWithAuthormap();

    private static final Pattern YEAR_PATTERN = Pattern.compile("^\\s*(.+),\\s*(\\d{2,4})\\s*$");

    /**
     * Test for equivalence.
     * <p>
     * Authors are declared equivalent based on the GBIF author comparator
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     *
     * @return Null if not comparable, true if equivalent, false otherwise.
     * @throws InferenceException if unable to determine equivalence
     */
    @Override
    public Boolean equivalent(String value1, String value2) throws InferenceException {
        String year1 = null;
        String year2 = null;
        if (value1 == null || value2 == null)
            return null;
        if (value1 != null) {
            if (value1.startsWith("(") && value1.endsWith(")"))
                value1 = value1.substring(1, value1.length() - 1);
            Matcher matcher1 = YEAR_PATTERN.matcher(value1);
            if (matcher1.matches()) {
                value1 = matcher1.group(1);
                year1 = matcher1.group(2);
            }
        }
        if (value2 != null) {
            if (value2.startsWith("(") && value2.endsWith(")"))
                value2 = value2.substring(1, value2.length() - 1);
            Matcher matcher2 = YEAR_PATTERN.matcher(value2);
            if (matcher2.matches()) {
                value2 = matcher2.group(1);
                year2 = matcher2.group(2);
            }
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
