package au.org.ala.bayesian;

import au.org.ala.bayesian.analysis.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An analysis object for an observable.
 * <p>
 * Analysis objects take a value an performs any simple processing or
 * intepretation that the value needs before
 * </p>
 *
 * @param <C> The type of object this analyser handles.
 * @param <S> The type of object this analyser reads/writes.
 * @param <Q> The type of object this analyser uses to query the store
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
abstract public class Analysis<C, S, Q> {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The class of analyser object
     */
    @JsonIgnore
    abstract public Class<C> getType();

    /**
     * Get the class of object that this analyser stores.
     *
     * @return The class of store object
     */
    @JsonIgnore
    abstract public Class<S> getStoreType();

    /**
     * Analyse this object, providing any special interpretation
     * required.
     *
     * @param value The value to analyse
     *
     * @return The analysed value.
     *
     * @throws InferenceException if unable to analyse the value
     */
    abstract public C analyse(C value) throws InferenceException;

    /**
     * Convert this object into a value for storage
     *
     * @param value The value to convert
     *
     * @return The converted value (null should return null)
     *
     * @throws StoreException if unable to convert to a string
     */
    abstract public S toStore(C value) throws StoreException;


    /**
     * Convert this object into a value for query
     *
     * @param value The value to convert
     *
     * @return The converted value (null should return null)
     *
     * @throws StoreException if unable to convert to a query object
     */
    abstract public Q toQuery(C value) throws StoreException;

    /**
     * Parse a stored value and return a suitably interpreted object.
     *
     * @param value The value
     *
     * @return The parsed value
     *
     * @throws StoreException if unable to interpret the stored value
     */
    abstract public C fromStore(S value) throws StoreException;

    /**
     * Parse this value from a string and return a suitably interpreted object.
     *
     * @param value The value
     *
     * @return The parsed value
     *
     * @throws StoreException if unable to interpret the string
     */
    abstract public C fromString(String value) throws StoreException;

    /**
     * Test for equivalence.
     * <p>
     * By default, if either value1 or value2 is null, the result is null.
     * Otherwise, equality is used.
     * </p>
     * @param value1 The first value to test
     * @param value2 The second value to test
     *
     * @return Null if not comparable, true if equivalent, false otherwise.
     *
     * @throws InferenceException if unable to determine equivalence
     */
    public Boolean equivalent(C value1, C value2) throws InferenceException {
        if (value1 == null || value2 == null)
            return null;
        return value1.equals(value2);
    }

    /**
     * Get a default analysis object for a class.
     *
     * @param clazz The class
     *
     * @return A default analyser for this class
     *
     * @throws IllegalArgumentException if unable to determine the analyser from the class
     *
     * @param <C> The type of object this analyses
     */
    public static <C, S, Q> Analysis<C, S, Q> defaultAnalyser(Class<C> clazz) throws IllegalArgumentException {
        if (clazz == LocalDateAnalysis.class)
            return (Analysis<C, S, Q>) new LocalDateAnalysis();
        if (clazz == Double.class)
            return (Analysis<C, S, Q>) new DoubleAnalysis();
        if (clazz == Integer.class)
            return (Analysis<C, S, Q>) new IntegerAnalysis();
        if (clazz == String.class)
            return (Analysis<C, S, Q>) new StringAnalysis();
        if (Enum.class.isAssignableFrom(clazz))
            return (Analysis<C, S, Q>) new EnumAnalysis(clazz);
        throw new IllegalArgumentException("Unable to build default analysis object for " + clazz);
    }

    /**
     * Generate a hash code for this analysis object.
     * <p>
     * By default, analysis objects have no state and are interchangable.
     * Override this method for objects with state.
     * </p>
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    /**
     * Equality test for this analysis object.
     * <p>
     * By default, analysis objects have no state and
     * are equal if the class is equal.
     * </p>
     *
     * @return True if these are the same analyser.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return this.getClass().equals(obj.getClass());
    }
}
