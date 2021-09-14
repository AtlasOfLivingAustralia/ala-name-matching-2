package au.org.ala.bayesian;

import au.org.ala.bayesian.analysis.RangeAnalysis;
import org.apache.commons.lang3.Range;

public class TestRangeAnalysis extends RangeAnalysis {
    /**
     * Convert this object into a value for query
     *
     * @param value The value to convert
     * @return The converted value (null should return null)
     * @throws StoreException if unable to convert to a query object
     */
    @Override
    public Range<Integer> toQuery(Integer value) throws StoreException {
        return Range.between(value - 10, value + 10);
    }
}
