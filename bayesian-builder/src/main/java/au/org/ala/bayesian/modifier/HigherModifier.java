package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wind a target back to a higher level in the network.
 * <p>
 * This basically works by nulling all the listed observables and copying
 * the source value into the target.
 * </p>
 */
public class HigherModifier extends BaseModifier {
    /** The source of the name */
    @JsonProperty
    @Getter
    protected List<Observable> sources;
    /** The target of the name */
    @JsonProperty
    @Getter
    protected Observable target;

    protected HigherModifier() {
    }

    public HigherModifier(String id, Collection<Observable> observables, boolean nullDerived) {
        super(id, observables, nullDerived);
    }

    /**
     * Get the base variables that this will alter
     *
     * @return All modifiable variables, plus the target.
     */
    @Override
    public Set<Observable> getModified() {
        Set<Observable> modified = new HashSet<>(super.getModified());
        modified.add(this.target);
        return modified;
    }

    /**
     * The set of observables that need to be present for this modification to succeed.
     *
     * @return The set of observavles that need to be present
     * @see #getAnyCondition()
     */
    @Override
    public Collection<Observable> getConditions() {
        return this.sources;
    }

    /**
     * Can we proceed with any conditonal variable being true or must we have all of them?
     *
     * @return True if any variable being present allows the modifier
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
     */
    @Override
    public List<String> generate(NetworkCompiler compiler, String from, String to) {
        List<String> statements = new ArrayList<>();
        this.checkModifiable(compiler, from, statements);
        statements.add(to + " = " + from + ".clone();");
        for (Observable observable: this.getModified())
            statements.add(to + "." + observable.getJavaVariable() + " = null;");
        if (this.isNullDerived())
            this.nullDependents(compiler, to, statements);
        statements.add(to + "." + this.target.getJavaVariable() + " = " +
                this.sources.stream().map(s -> from + "." + s.getJavaVariable()).collect(Collectors.joining(" + \" \" + "))
                + ";");
        return statements;
    }
}
