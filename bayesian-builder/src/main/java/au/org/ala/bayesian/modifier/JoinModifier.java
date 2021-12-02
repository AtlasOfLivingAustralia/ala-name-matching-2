package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import au.org.ala.names.builder.BuilderException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copy a value into a target.
 * <p>
 * Multiple sources are permitted.
 * If there is more than one source, the first non-null value will be used.
 * </p>
 */
public class JoinModifier extends BaseModifier {
    /** The source of the name. These need to be in order for building composites */
    @JsonProperty
    @Getter
    protected List<Observable> sources;
    /** The target of the name */
    @JsonProperty
    @Getter
    protected Observable target;

    protected JoinModifier() {
    }

    public JoinModifier(String id, List<Observable> sources, Observable target, boolean clearDerived) {
        super(id, clearDerived);
        this.sources = sources;
        this.target = target;
    }

    /**
     * Get the base variables that this will alter
     *
     * @return The target
     */
    @Override
    public Set<Observable> getModified() {
        return Collections.singleton(this.target);
    }

    /**
     * The set of observables that need to be present for this modification to succeed.
     *
     * @return The set of observavles that need to be present
     * @see #getAnyCondition()
     */
    @Override
    public Set<Observable> getConditions() {
        return new HashSet<>(this.sources);
    }

    /**
     * Can we proceed with any conditonal variable being true or must we have all of them?
     *
     * @return False since we need all variables present
     */
    @Override
    public boolean getAnyCondition() {
        return false;
    }

    /**
     * Generate code that will perform the modification.
     *
     * @param compiler The source network
     * @param from The name of the variable that holds the classification to modify
     * @param to   The name of the variable that holds the resulting classification
     * @return The code needed to modify the classification
     *
     * @throws BayesianException if unable to generate the code
     */
    @Override
    public void generateModification(NetworkCompiler compiler, String from, String to, List<String> statements) throws BayesianException {
        statements.add(
                to + "." + this.target.getJavaVariable() + " = " +
                        this.sources.stream().map(s -> from + "." + s.getJavaVariable()).collect(Collectors.joining(" + \" \" + ")) +
                        ";"
        );
    }
}
