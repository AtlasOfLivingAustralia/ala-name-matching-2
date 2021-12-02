package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Modifier;
import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A modifier that alters a collection of observables.
 */
abstract public class BaseModifier extends Modifier {
    /** Null derived values, as well (false by default) */
    @JsonProperty
    @Getter
    protected boolean clearDerived = false;

    protected BaseModifier() {
    }

    public BaseModifier(String id, boolean clearDerived) {
        super(id);
        this.clearDerived = clearDerived;
    }

    /**
     * Generate the code that modifies other variables that need to be
     * <p>
     * By default, this does nothing.
     * </p>
     *
     * @param compiler   The compiled network
     * @param from       The name of the variable that holds the classification to modify
     * @param to         The name of the variable that holds the resulting classification
     * @param statements The list of statements needed to modify the classification
     * @throws BayesianException if unable to genereate code
     */
    @Override
    public void generateConditioning(NetworkCompiler compiler, String from, String to, List<String> statements) throws BayesianException {
        if (this.clearDerived)
            this.nullDependents(compiler, to, statements);
    }
}
