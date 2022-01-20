package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A condition that can be applied to a modifier
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Condition {
    /**
     * Generate a positive check that matches this condition for a classification.
     *
     * @param compiler The network compiler
     * @param var The classification variable to test
     *
     * @return The built condition, null for no condition
     */
    abstract public String buildCheck(NetworkCompiler compiler, String var);

    /**
     * Generate a positive check that matches this condition.
     *
     * @param compiler The network compiler
     * @param var The variable that holds the classifier
     * @param observables The class that holds observable definitions
     *
     * @return The built condition, null for no condition
     */
    abstract public String buildClassifierCheck(NetworkCompiler compiler, String var, String observables);
}
