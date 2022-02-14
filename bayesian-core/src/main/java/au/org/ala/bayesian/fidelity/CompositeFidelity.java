package au.org.ala.bayesian.fidelity;

import au.org.ala.bayesian.Fidelity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fidelity built from multiple sub-components in an object
 *
 * @param <T> The type of object this fidelity reflects
 */
public class CompositeFidelity<T> extends Fidelity<T> {
    @Getter
    private List<Fidelity<?>> components;

    /**
     * Construct with a list of components.
     *
     * @param original The original object
     * @param actual The actual object
     * @param components The component elements
     */
    public CompositeFidelity(T original, T actual, List<Fidelity<?>> components) {
        super(original, actual);
        this.components = components;
    }


    /**
     * Construct with am empty list of components.
     *
     * @param original The original object
     * @param actual The actual object
      */
    public CompositeFidelity(T original, T actual) {
        this(original, actual, new ArrayList<>());
    }

    /**
     * Compute the fidelity measure from the component elements
     * <p>
     *     This is computed as the mean value of the component fidelities.
     *     An empty set of components is assumed to be zero.
     * </p>
     *
     * @return The fidelity value
     */
    @Override
    public double getFidelity() {
        return this.components.stream().mapToDouble(Fidelity::getFidelity).average().orElse(0.0);
    }

    /**
     * Add a component fidelity to the composite.
     *
     * @param component The component (if null then nothing is added)
     */
    public void add(Fidelity<?> component) {
        if (component != null)
            this.components.add(component);
    }
}
