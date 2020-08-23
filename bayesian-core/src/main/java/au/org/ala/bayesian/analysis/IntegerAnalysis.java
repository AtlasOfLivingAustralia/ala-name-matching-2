package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;

/**
 * The default integer analysis
 */
public class IntegerAnalysis extends Analysis {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The integer class
     */
    @Override
    public Class<Integer> getType() {
        return Integer.class;
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
    public <C> C analyse(C value) throws InferenceException {
        return value;
    }

    /**
     * Convert this object into a string for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public String toString(Object value) throws StoreException {
        return value == null ? null : value.toString();
    }

    /**
     * Parse this value and return a suitably interpreted object.
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
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new StoreException("Unable to parse integer " + value);
        }
    }
}
