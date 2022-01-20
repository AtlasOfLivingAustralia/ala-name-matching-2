package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An observation or fact about a node in the Bayesian network.
 */
public class Observation<T> {
    /** Is this fact true or false */
    @JsonProperty
    @Getter
    private boolean positive;
    /** The observable this fact is associated with */
    @JsonProperty
    @Getter
    private Observable<T> observable;
    /** A single value (optimisation so that we don't bother with a singleton collection) */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T value;
    /** The possible values that this observable can have */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<T> values;

    /**
     * Empty constructor
     */
    public Observation() {
        this.positive = true;
    }

    /**
     * Construct for a set of values
     *
     * @param positive Is this true or false
     * @param observable The associated network observable
     * @param values The possible values
     */
    public Observation(boolean positive, Observable<T> observable, Set<T> values) {
        this.positive = positive;
        this.observable = observable;
        if (values.size() == 0) {
            this.value = null;
            this.values = null;
        } else if (values.size() == 1) {
            this.value = values.iterator().next();
            this.values = null;
        } else {
            this.value = null;
            this.values = values;
        }
    }

    /**
     * Construct for one or more values
     *
     * @param positive Is this true or false
     * @param observable The associated network observable
     * @param values The possible values
     */
    @SafeVarargs
    public Observation(boolean positive, Observable<T> observable, T... values) {
        this.positive = positive;
        this.observable = observable;
        if (values.length == 0)
            this.value = null;
        else if (values.length == 1)
            this.value = values[0];
        else {
            this.values = new HashSet<>(values.length);
            Collections.addAll(this.values, values);
        }
    }

    /**
     * Is this present value (just an observable, no specific value)
     *
     * @return True if this is a singleton
     */
    @JsonIgnore
    public boolean isPresent() {
        return this.value == null && (this.values == null || this.values.size() == 0);
    }

    /**
     * Is this a singleton observation (a single value, rather than a set of possible values)
     *
     * @return True if this is a singleton
     */
    @JsonIgnore
    public boolean isSingleton() {
        return this.value != null || this.values != null && this.values.size() == 1;
    }

    /**
     * Is this a blank observation (no values)
     *
     * @return True if this is a blank
     */
    @JsonIgnore
    public boolean isBlank() {
        return this.value == null && (this.values == null || this.values.size() == 0);
    }


    /**
     * Get the singleton value.
     * <p>
     * This can either be the actual singleton value or a singleton set.
     * </p>
     *
     * @return The singleton value
     *
     * @throws IllegalStateException if a singleton is not available
     */
    @JsonIgnore
    public T getValue() throws IllegalStateException {
        if (this.value != null)
            return this.value;
        if (this.values != null && this.values.size() == 1)
            return this.values.iterator().next();
        throw new IllegalStateException("Observation is not a single value");
    }

    /**
     * Get the collection of values.
     *
     * @return The set of values that correspond to matching the observation
     */
    @JsonIgnore
    public Set<T> getValues() {
        if (this.values != null)
            return this.values;
        if (this.value != null)
            return Collections.singleton(this.value);
        return Collections.emptySet();
    }

    /**
     * Return a positive version of this observation.
     * <p>
     * If the observation is already positive, return this.
     * </p>
     *
     * @return The positive observation
     */
    public Observation<T> asPositive() {
        if (this.isPositive())
            return this;
        Observation<T> positive = new Observation<>();
        positive.observable = this.observable;
        positive.positive = true;
        positive.value = this.value;
        positive.values = this.values;
        return positive;
    }

    /**
     * Return a negative version of this observation.
     * <p>
     * If the observation is already negative, return this.
     * </p>
     *
     * @return The negative observation
     */
    public Observation<T> asNegative() {
        if (!this.isPositive())
            return this;
        Observation<T> negative = new Observation<>();
        negative.observable = this.observable;
        negative.positive = false;
        negative.value = this.value;
        negative.values = this.values;
        return negative;
    }

    /**
     * Build a hash code for this object.
     *
     * @return The computed hash code
     */
    @Override
    public int hashCode() {
        int hash = this.observable.hashCode();
        hash = hash ^ (this.positive ? 1000003 : 10001531);
        // The hash for a single value should be the same, no matter how stored
        if (this.value != null)
            hash = hash ^ this.value.hashCode();
        if (this.values != null)
            hash = hash ^ this.values.hashCode(); // Relies on AbstractSet#hashCode just adding values
        return hash;
    }

    /**
     * Equality test.
     *
     * @param obj The other object
     *
     * @return true if referencing the same observable and same values in the same way.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Observation))
            return false;
        Observation o = (Observation) obj;
        if (!this.observable.equals(o.observable))
            return false;
        if (this.positive != o.positive)
            return false;
        if (Objects.equals(this.value, o.value))
            return false;
        return Objects.equals(this.values, o.values);
    }
}
