package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.Modifier;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A modifier that alters a collection of observables.
 */
abstract public class BaseModifier extends Modifier {
    /** The observable to modify */
    @JsonProperty
    @Getter
    private Set<Observable> observables;
    /** Null derived values, as well (false by default) */
    @JsonProperty
    @Getter
    private boolean nullDervived = false;

    protected BaseModifier() {
    }

    public BaseModifier(String id, Collection<Observable> observables, boolean nullDervived) {
        super(id);
        this.observables = new HashSet<>(observables);
        this.nullDervived = nullDervived;
    }

    /**
     * Get the single base variable this will alter
     *
     * @return A singleton set with the observable in it.
     */
    @Override
    public Set<Observable> getModified() {
        return this.observables;
    }
}
