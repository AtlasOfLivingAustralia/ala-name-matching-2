package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import org.apache.commons.lang3.Range;

/**
 * Analyse an integer as something representing a range of possible values.
 * <p>
 * Subclasses are responsible for turning a value into a range.
 * </p>
 */
abstract public class RangeAnalysis extends Analysis<Integer, Integer, Range<Integer>> {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The range value class
     */
    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    /**
     * Get the class of object that this analyser handles.
     *
     * @return The integer class
     */
    @Override
    public Class<Integer> getStoreType() {
        return Integer.class;
    }

    /**
     * Convert this object into a value for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public Integer toStore(Integer value) throws StoreException {
        return value;
    }

    /**
     * Parse a stored value and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the stored value
     */
    @Override
    public Integer fromStore(Integer value) throws StoreException {
        return value;
    }

    /**
     * Parse this value from a string and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public Integer fromString(String value) throws StoreException {
        if (value == null || value.isEmpty())
            return null;
        try {
            return this.fromStore(Integer.parseInt(value));
        } catch (NumberFormatException ex) {
            throw new StoreException("Can't parse " + value, ex);
        }
    }

    /**
     * Analyse this object, providing any special interpretation
     * required.
     * <p>
     * Returns the value as-is.
     * </p>
     *
     * @param value The value to analyse
     * @return The analysed value.
     * @throws InferenceException if unable to analyse the value
     */
    @Override
    public Integer analyse(Integer value) throws InferenceException {
        return value;
    }

}
