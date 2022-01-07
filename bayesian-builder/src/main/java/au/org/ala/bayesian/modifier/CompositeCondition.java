package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Construct a condition from sub-conditions using and or or
 */
public class CompositeCondition extends Condition {
    /** All conditions need to be true, True by default */
    @JsonProperty
    @Getter
    private boolean all = true;
    /** The conditions that need to be tested */
    @JsonProperty
    @Getter
    @NonNull
    private List<Condition> conditions;

    /**
     * Default constructor
     */
    public CompositeCondition() {
        this.conditions = new ArrayList<>();
    }

    /**
     * Construct with a list of conditions
     *
     * @param all All observables must be present
     * @param conditions The list of sub-conditions that should be met
     */
    public CompositeCondition(boolean all, @NonNull List<Condition> conditions) {
        this.all = all;
        this.conditions = conditions;
    }

    /**
     * Generate a positive check that matches this condition
     *
     * @param compiler The network compiler
     * @param var      The variable to test
     * @return The built condition
     */
    @Override
    public String buildCheck(NetworkCompiler compiler, String var) {
        final String connector = this.all ? " && " : " || ";
        List<String> clauses = this.conditions.stream()
                .map(c -> c.buildCheck(compiler, var))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (clauses.isEmpty())
            return null;
        if (clauses.size() == 1)
            return clauses.get(0);
        return clauses.stream().map(c -> "(" + c + ")").collect(Collectors.joining(connector));
    }

}
