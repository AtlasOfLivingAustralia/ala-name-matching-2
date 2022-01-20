package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Remove the value of an entry
 */
public class RemoveModifier extends BaseModifier {
    /** The observable to modify */
    @JsonProperty
    @Getter
    protected Set<Observable> observables;

    protected RemoveModifier() {
    }

    public RemoveModifier(String id, Collection<Observable> observables, boolean clearDerived) {
        super(id, clearDerived);
        this.observables = new HashSet<>(observables);
    }

    /**
     * Get the base variables that this will alter
     *
     * @return A singleton set with the observable in it.
     */
    @Override
    public Set<Observable> getModified() {
        return this.observables;
    }

    /**
     * The set of observables that need to be present for this modification to succeed.
     *
     * @return The set of observavles that need to be present
     * @see #getAnyCondition()
     */
    @Override
    public Set<Observable> getConditions() {
        return this.getModified();
    }

    /**
     * Can we proceed with any conditonal variable being true or must we have all of them?
     *
     * @return True if any variable being present allows the modifier
     */
    @Override
    public boolean getAnyCondition() {
        return true;
    }

    /**
     * Generate code that will perform the modification.
     * <p>
     * Set any values to null.
     * </p>
     *
     * @param compiler The source network
     * @param from The name of the variable that holds the classification to modify
     * @param to   The name of the variable that holds the resulting classification
     * @return The code needed to modify the classification
     */
    @Override
    public void generateModification(NetworkCompiler compiler, String from, String to, List<String> statements) {
        for (Observable observable: this.getModified())
            statements.add(to + "." + observable.getJavaVariable() + " = null;");
    }
}
