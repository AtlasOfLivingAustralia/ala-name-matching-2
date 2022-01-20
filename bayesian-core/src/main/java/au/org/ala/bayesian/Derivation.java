package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

/**
 * A derivation for an observable.
 * <p>
 * CompilerDerivations are used to generate code that will derive the observable.
 * </p>
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property="@class")
abstract public class Derivation {
    /**
     * Is this a compiled derivation?
     * <p>
     * Compiled derivations have their code directly inserted into the generated classes.
     * Otherwise, an instance is used to derive a value.
     * </p>
     *
     * @return True if this is a compiled derivation
     */
    abstract public boolean isCompiled();

    /**
     * Get the list of variables that provide input to this derivatiobn.
     *
     * @return The set of sources.
     */
    @JsonIgnore
    abstract public Set<Observable> getInputs();

}
