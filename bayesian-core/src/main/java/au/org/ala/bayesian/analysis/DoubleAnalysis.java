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
public class DoubleAnalysis extends Analysis<Double, Double, Double> {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The double class
     */
    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    /**
     * Get the class of object that this analyser handles.
     *
     * @return The double class
     */
    @Override
    public Class<Double> getStoreType() {
        return Double.class;
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
    public Double analyse(Double value) throws InferenceException {
        return value;
    }

    /**
     * Convert this object for storage
     *
     * @param value The value to convert
     * @return The stori value (null should return null)
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public Double toStore(Double value) throws StoreException {
        return value;
    }

    /**
     * Convert this object into a value for query
     *
     * @param value The value to convert
     *
     * @return The converted value (null should return null)
     */
    @Override
    public Double toQuery(Double value) {
        return value;
    }

    /**
     * Convert this object form storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public Double fromStore(Double value) throws StoreException {
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
    public Fidelity<Double> buildFidelity(Double original, Double actual) throws InferenceException {
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
     * @return The fidelity scaling factor
     */
    @JsonIgnore
    public double getFidelityScale() {
        return 1.0;
    }

    /**
     * Parse this value and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public Double fromString(String value) throws StoreException {
        if (value == null || value.isEmpty())
            return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new StoreException("Unable to parse double " + value);
        }
    }
}
