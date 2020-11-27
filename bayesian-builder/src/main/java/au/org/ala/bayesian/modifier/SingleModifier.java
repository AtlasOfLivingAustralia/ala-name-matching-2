package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.Modifier;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.Set;

/**
 * A modifier that alters a single variable.
 */
abstract public class SingleModifier extends Modifier {
    /** The observable to modify */
    @JsonProperty
    @Getter
    private Observable observable;

    protected SingleModifier() {
    }

    public SingleModifier(String id, Observable observable) {
        super(id);
        this.observable = observable;
    }

    /**
     * Get the single base variable this will alter
     *
     * @return A singleton set with the observable in it.
     */
    @Override
    public Set<Observable> getModified() {
        return Collections.singleton(this.observable);
    }
}
