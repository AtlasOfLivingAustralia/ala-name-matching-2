package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;

import java.time.LocalDate;

public class StringAnalysis extends Analysis {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The string class
     */
    @Override
    public Class<String> getType() {
        return String.class;
    }

    /**
     * Analyse this object, providing any special interpretation
     * required.
     * <p>
     * Trim the value.
     * Empty strings also return null.
     * </p>
     *
     * @param value The value to analyse
     * @return The analysed value.
     * @throws InferenceException if unable to analyse the value
     */
    @Override
    public <C> C analyse(C value) throws InferenceException {
        if (value == null)
            return null;
        String s = value.toString().trim();
        return s.isEmpty() ? null : (C) s;
    }

    /**
     * Convert this object into a string for storage
     * <p>
     * This just returns the value
     * </p>
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
     * <p>
     * This just returns the value
     * </p>
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public String fromString(String value) throws StoreException {
        return value;
    }
}
