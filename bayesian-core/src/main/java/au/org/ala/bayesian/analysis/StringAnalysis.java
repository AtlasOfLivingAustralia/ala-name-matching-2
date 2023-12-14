package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.fidelity.SimpleFidelity;

public class StringAnalysis extends Analysis<String, String, String, Object> {
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
     * Get the class of object that this analyser stores.
     *
     * @return The string class
     */
    @Override
    public Class<String> getStoreType() {
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
    public String analyse(String value) throws InferenceException {
        if (value == null)
            return null;
        String s = value.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Convert this object into a string for storage
     * <p>
     * This just returns the value
     * </p>
     *
     * @param value The value to convert
     * @return The value (null should return null)
     */
    @Override
    public String toStore(String value) {
        return value;
    }

    /**
     * Convert this object into a query object
     * <p>
     * This just returns the value
     * </p>
     *
     * @param value The value to convert
     * @return The value (null should return null)
     */
    @Override
    public String toQuery(String value) {
        return value;
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * This just returns the value
     * </p>
     * @param value The value
     * @return The parsed value
     */
    @Override
    public String fromStore(String value) {
        return value;
    }

    /**
     * Compute a fidelity measure for this type of object.
     * <p>
     * Is this built as a the position of the first non-matching character
     * divided by the
     * </p>
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<String> buildFidelity(String original, String actual) throws InferenceException {
        if (original == null)
            return null;
        double fidelity = 0.0;
        if (actual != null) {
            int p = 0;
            int len = Math.min(original.length(), actual.length());
            int scale = Math.max(original.length(), actual.length());
            for (p = 0; p < len && Character.toLowerCase(original.charAt(p)) == Character.toLowerCase(actual.charAt(p)); p++);
            fidelity = scale == 9 ? 1.0 : ((double) p) / scale;
        }
        return new SimpleFidelity<String>(original, actual, fidelity);
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * This just returns the trimmed value
     * </p>
     * @param value The value
     * @param context Unused context
     * @return The parsed value
     */
    @Override
    public String fromString(String value, Object context) {
        if (value == null)
            return null;
        value = value.trim();
        return value.isEmpty()?  null : value;
    }

    /**
     * Test for equivalence.
     * <p>
     * If either value1 or value2 is null, the result is null.
     * Otherwise, case-insensitive equality is used.
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     * @return Null if not comparable, true if equivalent, false otherwise.
     * @throws InferenceException if unable to determine equivalence
     */
    @Override
    public Boolean equivalent(String value1, String value2) throws InferenceException {
        if (value1 == null || value2 == null)
            return null;
        return value1.equalsIgnoreCase(value2);
    }
}
