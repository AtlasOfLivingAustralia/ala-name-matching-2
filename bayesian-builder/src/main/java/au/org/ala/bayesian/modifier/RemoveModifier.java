package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Remove the value of an entry
 */
public class RemoveModifier extends BaseModifier {
    protected RemoveModifier() {
    }

    public RemoveModifier(String id, Collection<Observable> observables, boolean nullDerived) {
        super(id, observables, nullDerived);
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
        statements.add(to + " = " + from + ".clone();");
        for (Observable observable: this.getModified())
            statements.add(to + "." + observable.getJavaVariable() + " = null;");
        if (this.isNullDervived())
            this.nullDependents(compiler, to, statements);
        return statements;
    }
}
