package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;

public class EnumAnalysis<E extends Enum<E>> extends Analysis<E> {
    private Class<E> clazz;

    /**
     * Construct for an enumeration
     *
     * @param clazz
     */
    public EnumAnalysis(Class<E> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get the class of object that this analyser handles.
     *
     * @return The class of analyser object
     */
    @Override
    public Class<E> getType() {
        return this.clazz;
    }

    /**
     * Analyse this object, providing any special interpretation
     * required.
     * <p>
     * By default, an enum is an enum.
     * Subclasses can adjust entries as appropriate.
     * </p>
     *
     * @param value The value to analyse
     * @return The analysed value.
     */
    @Override
    public E analyse(E value) throws InferenceException {
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
    public String toString(E value) throws StoreException {
        if (value == null)
            return null;
        return value.name().toLowerCase();
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * By default, tries the enum name to all upper case and
     * if that doesn't work, tries the name.
     * </p>
     *
     * @param value The value
     * @return The parsed value
     */
    @Override
    public E fromString(String value) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return E.valueOf(this.clazz, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
        }
        try {
            return E.valueOf(this.clazz, value);
        } catch (IllegalArgumentException ex) {
        }
        return null;
    }

    /**
     * Test for equivalence.
     * <p>
     * At the moment, two enums being equal is equivalent.
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     *
     * @return Null if not comparable, true if equivalent, false otherwise.
     */
    @Override
    public Boolean equivalent(E value1, E value2) {
        if (value1 == null || value2 == null)
            return null;
        return value1 == value2;
    }
}
