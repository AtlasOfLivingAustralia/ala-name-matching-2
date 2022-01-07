package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.NetworkCompiler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Invert a condition.
 */
public class NotCondition extends Condition {
    /** The conditions that need to be tested */
    @JsonProperty
    @Getter
    private Condition condition;

    /**
     * Default constructor
     */
    public NotCondition() {
    }

    /**
     * Construct with a sub-condition
     *
     * @param condition The sub-conditions that should be inverted
     */
    public NotCondition(Condition condition) {
        this.condition = condition;
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
        String clause = this.condition.buildCheck(compiler, var);
        if (clause == null)
            return null;
        return "!(" + clause + ")";
    }

}
