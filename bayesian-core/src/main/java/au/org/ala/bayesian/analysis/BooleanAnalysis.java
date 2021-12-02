package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;

/**
 * The default integer analysis
 */
public class BooleanAnalysis extends Analysis<Boolean, Boolean, Boolean> {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The boolean class
     */
    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    /**
     * Get the class of object that this analyser stores.
     *
     * @return The boolean class
     */
    @Override
    public Class<Boolean> getStoreType() {
        return Boolean.class;
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
    public Boolean analyse(Boolean value) throws InferenceException {
        return value;
    }

    /**
     * Convert this object for storage
     *
     * @param value The value to convert
     * @return The storage value (null should return null)
     */
    @Override
    public Boolean toStore(Boolean value) {
        return value;
    }

    /**
     * Convert this object for query
     *
     * @param value The value to convert
     * @return The storage value (null should return null)
     */
    @Override
    public Boolean toQuery(Boolean value) {
        return value;
    }


    /**
     * Convert this object from storage
     *
     * @param value The value to convert
     * @return The comverted value (null should return null)
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public Boolean fromStore(Boolean value) {
        return value;
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     *     "true", "t", "yes" and "y" of any case all return true.
     *     Everything else returns false
     * </p>
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public Boolean fromString(String value) {
        if (value == null || value.isEmpty())
            return null;
        return value.equalsIgnoreCase("true") ||
                value.equalsIgnoreCase("t") ||
                value.equalsIgnoreCase("yes") ||
                value.equalsIgnoreCase("y");
    }
}
