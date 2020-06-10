package au.org.ala.bayesian;

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
     * Get the variables that this builder/derivation.
     * <p>
     * By default, this is an empty list.
     * </p>
     *
     * @return The instance variables needed to implement this derivation
     */
    public Collection<Variable> getBuilderVariables() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Get a condition for searching parent documents.
     *
     * @param foundVar The variable containing the test document
     * @param documentVar The variable containing the original document
     * @param parentsVar The parent list variable
     *
     * @return The code for a finder test, or null for no test
     */
    public String getCondition(String foundVar, String documentVar, String parentsVar) {
        return null;
    }

    /**
     * Generate the piece of code that generates the values required
     * for derivation.
     *
     * @param documentVar The name of the document variable (a lucene document)
     *
     * @return The code to get the values
     */
    abstract public String getValues(String documentVar);

    /**
     * Generate the piece of code that transforms the values required
     * for derivation.
     *
     * @param var The variable that holds the value to be transformed
     * @param documentVar The name of the document variable (a lucene document)
     *
     * @return The code to get the values
     */
    abstract public String getTransform(String var, String documentVar);

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
