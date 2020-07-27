package au.org.ala.bayesian;

import lombok.Getter;

import java.util.Objects;

/**
 * A contributor to an inference parameter.
 * <p>
 * This is a particular variable having a particular value
 * </p>
 *
 */
public class Contributor {
    /** The observable */
    @Getter
    private Observable observable;
    /** Whether the observable matches or not */
    @Getter
    private boolean match;

    /**
     * Construct a contributor.
     *
     * @param observable The contributing observer
     * @param match The matching
     */
    public Contributor(Observable observable, boolean match) {
        this.observable = observable;
        this.match = match;
    }

    /**
     * Get a formula entry for this outcome.
     *
     * @return The observable id, with a not marker in front if not a match
     */
    public String getFormula() {
        if (this.isMatch())
            return this.observable.getLabel();
        return "\u00ac" + this.observable.getLabel();
    }

    /**
     * Equality test.
     * @param o The other object
     *
     * @return True if o is a contributor with the same observable and match criterion
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contributor that = (Contributor) o;
        return match == that.match &&
                observable.equals(that.observable);
    }

    /**
     * Get the hash code.
     *
     * @return The has code derived from the observable and
     */
    @Override
    public int hashCode() {
        return Objects.hash(observable, match);
    }

    @Override
    public String toString() {
        return "Contributor{" +
                observable +
                ", " + match +
                '}';
    }
}
