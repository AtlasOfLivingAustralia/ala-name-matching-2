package au.org.ala.bayesian.fidelity;

import au.org.ala.bayesian.Fidelity;
import lombok.Value;

/**
 * A simple specific value fidelity, computed elsewhere.
 *
 * @param <T> The type of object
 */
@Value
public class SimpleFidelity<T> extends Fidelity<T> {
    /** The fidelity value */
    private double fidelity;

    /**
     * Construct for a computed fidelity value.
     *
     * @param original The original value
     * @param actual The actual value
     * @param fidelity The computed value
     */
    public SimpleFidelity(T original, T actual, double fidelity) {
        super(original, actual);
        this.fidelity = fidelity;
    }
}
