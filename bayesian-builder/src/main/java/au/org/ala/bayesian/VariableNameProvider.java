package au.org.ala.bayesian;

import org.jgrapht.io.ComponentNameProvider;

/**
 * Provide a label for variables on the graph.
 */
public class VariableNameProvider implements ComponentNameProvider<Variable> {
    @Override
    public String getName(Variable variable) {
        return variable.getId();
    }
}
