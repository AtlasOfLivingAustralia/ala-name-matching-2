package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import au.org.ala.names.builder.BuilderException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Set a collection of
 */
public class SetModifier extends BaseModifier {
    /** The observable to set */
    @JsonProperty
    @Getter
    private Observable observable;
    /** The value to set */
    @JsonProperty
    @Getter
    private Object value;

    /**
     * Generate the code that modifies the result.
     * <p>
     * Subclasses are responsible for implementing the actual modification
     * </p>
     *
     * @param compiler   The compiled network
     * @param from       The name of the variable that holds the classification to modify
     * @param to         The name of the variable that holds the resulting classification
     * @param statements The list of statements needed to modify the classification
     * @throws BayesianException if unable to genereate code
     */
    @Override
    public void generateModification(NetworkCompiler compiler, String from, String to, List<String> statements) throws BayesianException {
        Object val = this.value;
        String setter = to + "." + this.observable.getJavaVariable();
        if (val instanceof String && this.observable.getType() != String.class)
            val = this.observable.getAnalysis().fromString((String) val);
        if (val == null) {
            statements.add(setter + " = null;");
        } else if (Number.class.isAssignableFrom(observable.getType())) {
            statements.add(setter + " = " + val + ";");
        } else if (Enum.class.isAssignableFrom(observable.getType())) {
            Enum<?> en = (Enum) val;
            statements.add(setter + " = " + en.getClass().getName() + "." + en.name() + ";");
        } else if (String.class.isAssignableFrom(observable.getType())) {
            statements.add(setter + " = \"" + val + "\";");
        } else {
            throw new BuilderException("Unable to set " + this.observable.getId() + " to " + this.value + " of class " + this.value.getClass());
        }
    }

    /**
     * Get the base variables that this will alter
     *
     * @return A singleton set with the observable in it.
     */
    @Override
    public Set<Observable> getModified() {
        return Collections.singleton(this.observable);
    }

    /**
     * The set of observables that need to be present for this modification to succeed.
     *
     * @return An empty set. We set values regardless.
     *
     * @see #getAnyCondition()
     */
    @Override
    public Set<Observable> getConditions() {
        return Collections.emptySet();
    }

    /**
     * Can we proceed with any conditonal variable being true or must we have all of them?
     *
     * @return True, since we do this no matter what
     */
    @Override
    public boolean getAnyCondition() {
        return true;
    }
}
