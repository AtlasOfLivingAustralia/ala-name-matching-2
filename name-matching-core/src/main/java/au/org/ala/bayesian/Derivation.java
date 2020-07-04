package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * A derivation for an observable.
 * <p>
 * These are essentially used as code generation templates.
 * Subclassses are used to contain useful parameters.
 * </p>
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
abstract public class Derivation {
    /**
     * Get the variables that this builder/derivation uses.
     * <p>
     * By default, this is an empty list.
     * </p>
     *
     * @return The instance variables needed to implement this derivation
     */
    @JsonIgnore
    public Collection<Variable> getBuilderVariables() {
        return Collections.EMPTY_LIST;
    }
    /**
     * Get the variables that this classification/derivation uses.
     * <p>
     * By default, this is an empty list.
     * </p>
     *
     * @return The instance variables needed to implement this derivation
     */
    @JsonIgnore
    public Collection<Variable> getClassificationVariables() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Get a condition for searching parent documents.
     *
     * @param foundVar The variable containing the test document
     * @param classifierVar The variable containing the original classifier
     * @param observablesClass The class holding observable defintions
     * @param parentsVar The parent list variable
     *
     * @return The code for a finder test, or null for no test
     */
    public String generateCondition(String foundVar, String classifierVar, String observablesClass, String parentsVar) {
        return null;
    }

    /**
     * Test to see whether we have any extra observable needed
     *
     * @return The extra observables needed
     */
    public boolean hasExtra() {
        return this.getExtra() != null;
    }

    /**
     * Get any extra observable needed to compute things.
     *
     * @return The extra variable, or null for none (defaults to null)
     */
    @JsonIgnore
    public Observable getExtra() {
        return null;
    }

    /**
     * Generate the piece of code that represents an extra variable.
     *
     * @param classifierVar The classifier variable
     * @param observablesClass The class that holds observable definitions
     *
     * @return The ancillary variable generator
     */
    public String generateExtra(String classifierVar, String observablesClass) {
        Observable extra = this.getExtra();

        return extra == null ? "null" : classifierVar + ".get(" + observablesClass + "." + extra.getJavaVariable() + ")";
    }

    /**
     * Generate the piece of code that generates the values required
     * for derivation.
     *
     * @param classifierVar The name of the classifier variable
     * @param observablesClass The class that holds observable definfitions
     *
     * @return The code to get the values
     */
    abstract public String generateValues(String classifierVar, String observablesClass);

    /**
     * Generate the piece of code that transforms the values required
     * for derivation.
     *
     * @param var The variable that holds the value to be transformed
     * @param extra The (nullable) variable that holds any extra context information
     * @param classifierVar The name of the classifier variable
     *
     * @return The code to get the values
     */
    abstract public String generateBuilderTransform(String var, String extra, String classifierVar);

    /**
     * Generate the piece of code that transforms the values required
     * for derivation.
     *
     * @return The code to get the values
     */
    abstract public String generateClassificationTransform();

    /**
     * Used to model required instance variables.
     */
    public class Variable {
        private Class clazz;
        private String name;

        public Variable(Class clazz, String name) {
            if (clazz == null || name == null)
                throw new IllegalArgumentException("Variable must have both class and name");
            this.clazz = clazz;
            this.name = name;
        }

        public Class getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Variable variable = (Variable) o;
            return clazz.equals(variable.clazz) &&
                    name.equals(variable.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, name);
        }
    }
}
