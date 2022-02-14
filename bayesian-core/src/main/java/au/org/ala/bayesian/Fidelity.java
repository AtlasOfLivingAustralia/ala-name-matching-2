package au.org.ala.bayesian;

import lombok.Getter;

/**
 * A measure of closeness to an original.
 * <p>
 *     Fidelity measures how close the actual match used on something is to the original template.
 *     It is used as a measure of how much fiddling about needed to be done to get from one item to another.
 * </p>
 *
 * @param <T> The type of object that this fideltity measures.
 */
abstract public class Fidelity<T> {
    /** The original value */
    @Getter
    private T original;
    /** The actual value */
    @Getter
    private T actual;

    /**
     * Construct a base fidelity measure.
     *
     * @param original The original value
     * @param actual The actual value
     */
    public Fidelity(T original, T actual) {
        this.original = original;
        this.actual = actual;
    }

    /**
     * Get the similarity measure between an original and actual value.
     *
     * @return The fidelity value
     */
    abstract public double getFidelity();
}
