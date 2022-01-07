package au.org.ala.bayesian;

import au.org.ala.names.builder.BuilderException;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sun.nio.ch.Net;

/**
 * A condition that can be applied to a modifier
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Condition {
    /**
     * Generate a positive check that matches this condition.
     *
     * @param compiler The network compiler
     * @param var The variable to test
     *
     * @return The built condition, null for no condition
     */
    abstract public String buildCheck(NetworkCompiler compiler, String var);
}
