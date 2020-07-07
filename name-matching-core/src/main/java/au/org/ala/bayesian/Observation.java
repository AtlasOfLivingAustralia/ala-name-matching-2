package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An observation or fact about a node in the bayseian network.
 */
public class Observation {
    /** Is this fact true or false */
    @JsonProperty
    @Getter
    private boolean positive;
    /** The observable this fact is associated with */
    @JsonProperty
    @Getter
    private Observable observable;
    /** A single value (optimisation so that we don't bother with a singleton collection) */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String value;
    /** The possible values that this observable can have */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> values;

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
    public Observation(boolean positive, Observable observable, Set<String> values) {
        this.positive = positive;
        this.observable = observable;
        this.values = values;
    }

    /**
     * Construct for one or more values
     *
     * @param positive Is this true or false
     * @param observable The associated network observable
     * @param values The possible values
     */
    public Observation(boolean positive, Observable observable, String... values) {
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
     * Is this a singleton observation (a singhle value, rather than a set of possible values)
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
    public String getValue() throws IllegalStateException {
        if (this.value != null)
            return this.value;
        if (this.values != null && this.values.size() == 1)
            return this.values.iterator().next();
        throw new IllegalStateException("Observation is not a single value");
    }

    /**
     * Get the collection of values.
     *
     * @return
     */
    @JsonIgnore
    public Set<String> getValues() {
        if (this.values != null)
            return this.values;
        if (this.value != null)
            return Collections.singleton(this.value);
        return Collections.EMPTY_SET;
    }
}
