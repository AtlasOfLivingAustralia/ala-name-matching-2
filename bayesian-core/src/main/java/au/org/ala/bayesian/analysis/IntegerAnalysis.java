package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The default integer analysis
 */
public class IntegerAnalysis extends Analysis<Integer, Integer, Integer, Object> {
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
     * Get the class of object that this analyser stores.
     *
     * @return The integer class
     */
    @Override
    public Class<Integer> getStoreType() {
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
    public Integer analyse(Integer value) throws InferenceException {
        return value;
    }

    /**
     * Convert this object for storage
     *
     * @param value The value to convert
     * @return The storage value (null should return null)
     */
    @Override
    public Integer toStore(Integer value) {
        return value;
    }

    /**
     * Convert this object for query
     *
     * @param value The value to convert
     * @return The storage value (null should return null)
     */
    @Override
    public Integer toQuery(Integer value) {
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
    public Integer fromStore(Integer value) {
        return value;
    }

    /**
     * Compute a fidelity measure for this type of object.
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<Integer> buildFidelity(Integer original, Integer actual) throws InferenceException {
        return original == null ? null : new SimpleFidelity<>(
                original,
                actual,
                actual == null ?
                        0.0 :
                        1.0 - Math.min(1.0, Math.abs(original - actual) / this.getFidelityScale())
        );
    }

    /**
     * How far away an actual value can be from an original value before zero
     * fidelity is reported.
     * <p>
     * This value defaults to 1.0 but can be overridden by subclasses.
     * </p>
     *
     * @return The fidelity scale
     */
    @JsonIgnore
    public double getFidelityScale() {
        return 1.0;
    }

    /**
     * Parse this value and return a suitably interpreted object.
     *
     * @param value The value
     * @param context Unused context
    * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public Integer fromString(String value, Object context) throws StoreException {
        if (value == null || value.isEmpty())
            return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new StoreException("Unable to parse integer " + value);
        }
    }
}
