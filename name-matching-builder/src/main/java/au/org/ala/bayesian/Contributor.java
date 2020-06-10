package au.org.ala.bayesian;

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
    private Observable observable;
    /** Whether the observable matches or not */
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
     * Get the observable for this contributor
     *
     * @return The observable
     */
    public Observable getObservable() {
        return observable;
    }

    /**
     * Is this a match (true) or non-match (false)
     *
     * @return True if the contributor should match the evidence
     */
    public boolean isMatch() {
        return match;
    }

    /**
     * Get a formula entry for this outcome.
     *
     * @return The observable id, with a not marker in front if not a match
     */
    public String getFormula() {
        if (this.isMatch())
            return this.observable.getId();
        return "\u00ac" + this.observable.getId();
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
