package au.org.ala.bayesian.modifier;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test to see if a list of obervables are set.
 */
public class PresentCondition extends Condition {
    /** All observables need to be present, True by default */
    @JsonProperty
    @Getter
    private boolean all = true;
    /** The observables that need to be present */
    @JsonProperty
    @Getter
    @NonNull
    private List<Observable> present;

    /**
     * Default constructor
     */
    public PresentCondition() {
        this.present = new ArrayList<>();
    }

    /**
     * Construct with a list of observables
     *
     * @param all All observables must be present
     * @param present The list of observables that should be set
     */
    public PresentCondition(boolean all, @NonNull List<Observable> present) {
        this.all = all;
        this.present = present;
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
        if (this.present.isEmpty())
            return null;
        return this.present.stream().map(o -> var + "." + o.getJavaVariable() + " != null").collect(Collectors.joining(connector));
    }
}
