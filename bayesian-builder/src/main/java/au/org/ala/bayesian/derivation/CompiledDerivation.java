package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.BayesianTerm;
import au.org.ala.vocab.OptimisationTerm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * A derivation for an observable that operates by generating code.
 * <p>
 * These are essentially used as code generation templates.
 * Subclassses are used to contain useful parameters.
 * </p>
 */
abstract public class CompiledDerivation extends Derivation {
    /** The condition under which to do the derivation */
    @JsonProperty
    private Condition condition;

    /**
     * Default constructor
     */
    public CompiledDerivation() {
    }

    /**
     * Create with a condition
     *
     * @param condition The condition
     */
    public CompiledDerivation(Condition condition) {
        this.condition = condition;
    }

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
        return Collections.emptyList();
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
        return Collections.emptyList();
    }

    /**
     * Is this derivation only run under certain conditions?
     *
     * @return True if there is a condition attached to the derivation
     */
    public boolean isConditional() {
        return this.condition != null;
    }

    /**
     * Get a condition for executing this derivation
     *
     * @param compiler The network compiler
     * @param classifierVar The variable containing the classifier
     * @param observablesClass The class holding observable defintions
     *
     * @return The code for a finder test, or null for no test
     *
     * @throws BayesianException if unable to access or process the condition
     */
    public String generateCondition(NetworkCompiler compiler, String classifierVar, String observablesClass) throws BayesianException {
        return this.condition == null ? null : this.condition.buildClassifierCheck(compiler, classifierVar, observablesClass);
    }

    /**
     * Does this have a selection
     *
     * @return True if there is a condition attached to the derivation
     */
    public boolean isSelectable() {
        return false;
    }

    /**
     * Test to see whether the found document matches the selecty condition.
     *
     * @param compiler The network compiler
     * @param foundVar The variable containing the test classifier
     * @param classifierVar The variable containing the original classifier
     * @param observablesClass The class holding observables definitions
     * @param parentsVar The parent list variable
     *
     * @return code for testing the document.
     */
    public String generateSelect(NetworkCompiler compiler, String foundVar, String classifierVar, String observablesClass, String parentsVar) throws StoreException {
        return null;
    }

    /**
     * Is this a compiled derivation?
     *
     * @return True, this is a compiled derivation
     */
    @Override
    @JsonIgnore
    public boolean isCompiled() {
        return true;
    }

    /**
     * Is this a generator?
     *
     * @return True if the data is generated from some sort of external source once.
     */
    @JsonIgnore
    public boolean isGenerator() {
        return false;
    }

    /**
     * Does this have a transform?
     *
     * @return True if the data is transformed, rather than copied
     */
    @JsonIgnore
    abstract public boolean hasTransform();

    /**
     * Does this derivation work as an interpreter, meaning that it can be immeduately
     * run before expansion?
     * <p>
     * Something is an interpreter if it is not a generator, if it does not have a base derivation
     * and if its inputs are not inferred or analysed
     * </p>
     *
     * @return True if this variable is an interpreter
     */
    public boolean isPreAnalysis() {
        return !this.isGenerator() && this.getInputs().stream()
                .allMatch(o ->
                        o.getBase() == null &&
                        !o.hasProperty(OptimisationTerm.analysed, true) &&
                        !o.hasProperty(BayesianTerm.synonymName, true) &&
                        !o.hasProperty(BayesianTerm.altName, true) &&
                        !o.hasProperty(BayesianTerm.name, true) &&
                        !o.hasProperty(BayesianTerm.additionalName, true) &&
                        !o.hasProperty(BayesianTerm.fullName, true) &&
                        !o.hasProperty(BayesianTerm.copy, true) &&
                        (
                            o.getDerivation() == null ||
                            o.getDerivation().isCompiled() && !((CompiledDerivation) o.getDerivation()).isPreAnalysis()
                        )
                );
    }

    public boolean isPostAnalysis() {
        return !this.isGenerator() && !this.isPreAnalysis();
    }

    /**
     * Include the destination classifier in searches for sources to copy from?
     *
     * @return False by default
     */
    @JsonIgnore // Override in subclasses if needed
    public boolean isIncludeSelf() {
        return false;
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
     * Get the class of the value required for derivation.
     *
     * @return The value class
     */
    abstract public Class<?> getValueClass();

    /**
     * Generate the piece of code that generates the set value required
     * for derivation.
     *
     * @param classifierVar The name of the classifier variable
     * @param observablesClass The class that holds observable definfitions
     *
     * @return The code to get the values
     */
    abstract public String generateValue(String classifierVar, String observablesClass);

    /**
     * Generate the piece of code that generates the variant values required
     * for derivation.
     *
     * @param classifierVar The name of the classifier variable
     * @param observablesClass The class that holds observable definfitions
     *
     * @return The code to get the values
     */
    abstract public String generateVariants(String classifierVar, String observablesClass);

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
