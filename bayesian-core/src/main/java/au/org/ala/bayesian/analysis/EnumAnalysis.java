package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.fidelity.SimpleFidelity;

import java.util.Objects;

public class EnumAnalysis<E extends Enum<E>, Ctx> extends Analysis<E, String, String, Ctx> {
    private final Class<E> clazz;

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
     * Get the class of object that this analyser handles.
     *
     * @return The class of analyser object
     */
    @Override
    public Class<String> getStoreType() {
        return String.class;
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
     */
    @Override
    public String toStore(E value) {
        if (value == null)
            return null;
        return value.name().toLowerCase();
    }

    /**
     * Convert this object into a value for query
     *
     * @param value The value to convert
     *
     * @return The converted value (null should return null)
     */
    @Override
    public String toQuery(E value) {
        return this.toStore(value);
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
    public E fromStore(String value) {
        return this.fromString(value, null);
    }

    /**
     * Compute a fidelity measure for this type of object.
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<E> buildFidelity(E original, E actual) throws InferenceException {
        return original == null ? null : new SimpleFidelity<>(original, actual, original.equals(actual) ? 1.0 : 0.0);
    }

    /**
     * Parse this value and return a suitably interpreted object.
     * <p>
     * By default, tries the enum name to all upper case and
     * if that doesn't work, tries the name.
     * </p>
     *
     * @param value The value
     * @param context Unused context
     * @return The parsed value
     */
    @Override
    public E fromString(String value, Ctx context) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return E.valueOf(this.clazz, value);
        } catch (IllegalArgumentException ex) {
        }
        try {
            return E.valueOf(this.clazz, value.toUpperCase());
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

    /**
     * Equality test.
     *
     * @param o The other object
     *
     * @return If the same class and for the same class.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EnumAnalysis<?, ?> that = (EnumAnalysis<?, ?>) o;
        return clazz.equals(that.clazz);
    }

    /**
     * Hash code.
     *
     * @return Derived from ther class and the enum class
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), clazz);
    }
}
