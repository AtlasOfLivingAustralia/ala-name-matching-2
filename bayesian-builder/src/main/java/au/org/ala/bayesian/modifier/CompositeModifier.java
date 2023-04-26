package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Modifier;
import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A modifier that is the composition of other modifiers.
 */
public class CompositeModifier extends BaseModifier {
    @JsonProperty
    @Getter
    private List<Modifier> modifiers;

    /**
     * Default constructor.
     * <p>
     * Creates an empty list of sub-modifiers
     * </p>
     */
    public CompositeModifier() {
        this.modifiers = new ArrayList<>();
    }

    /**
     * Construct with a list of modifiers.
     *
     * @param id The modifier identifier
     * @param modifiers The list of modifiers
     * @param clearDerived Clear derived values
     */
    public CompositeModifier(String id, List<Modifier> modifiers, boolean clearDerived) {
        super(id, clearDerived);
        this.modifiers = modifiers;
    }

    /**
     * The base set of observables this modification will alter.
     *
     * @return The union of the sub-modifiers.
     */
    @Override
    public Set<Observable> getModified() {
        return this.modifiers.stream().map(Modifier::getModified).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    /**
     * The set of observables that need to be present for this modification to succeed.
     *
     * @return The union of all conditions
     *
     * @see #getAnyCondition()
     */
    @Override
    public Set<Observable> getConditions() {
        return this.modifiers.stream().map(Modifier::getConditions).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    /**
     * Can we proceed with any conditonal variable being true or must we have all of them?
     *
     * @return False, we need all sub-conditions to be met.
     */
    @Override
    public boolean getAnyCondition() {
        return false;
    }

    /**
     * Generate the code that modifies the result.
     * <p>
     * Each
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
        for (Modifier m: this.modifiers) {
            m.generateModification(compiler, from, to, statements);
        }
    }

    /**
     * Generate a list of tests to see whether we should run this modifier.
     * <p>
     * One or more of these checks being true means that the modifier will <em>not</em> be used.
     * </p>
     *
     * @param compiler The compiled network
     * @param var      The variable to check
     * @param positive True if we want to use the modifier
     * @return The list of checks to perform
     */
    @Override
    public List<String> buildChecks(NetworkCompiler compiler, String var, boolean positive) {
        return this.modifiers.stream().map(m -> m.buildCheck(compiler, var, positive)).filter(Objects::nonNull).map(c -> "(" + c + ")").collect(Collectors.toList());
    }
}
