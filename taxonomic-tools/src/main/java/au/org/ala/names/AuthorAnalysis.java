package au.org.ala.names;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.analysis.StringAnalysis;
import org.gbif.checklistbank.authorship.AuthorComparator;

/**
 * Analysis for taxon concept authorship.
 * <p>
 * Use the GBIF author comparator to see if two authors are equivalent.
 * </p>
 */
public class AuthorAnalysis extends StringAnalysis {
    /** The author comparator to use when checking authorship equality */
    private AuthorComparator comparator = AuthorComparator.createWithAuthormap();

    /**
     * Test for equivalence.
     * <p>
     * Authors are declared equivalent based on the
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
        if (value1 == null || value2 == null)
            return null;
        switch (this.comparator.compare(value1, null, value2, null)) {
            case EQUAL:
                return true;
            case DIFFERENT:
                return false;
            default:
                return null;
        }
    }
}
