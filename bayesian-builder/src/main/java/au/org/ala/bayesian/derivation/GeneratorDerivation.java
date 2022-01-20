package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Observable;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
abstract public class GeneratorDerivation extends CompiledDerivation {
    /**
     * Get the list of variables that provide input to this derivation.
     *
     * @return We use no sources.
     */
    @Override
    public Set<Observable> getInputs() {
        return Collections.emptySet();
    }

    /**
     * Is this a generator?
     *
     * @return True
     */
    @Override
    public boolean isGenerator() {
        return true;
    }

    /**
     * Does this have a transform?
     *
     * @return This is, technically, a transform
     */
    @Override
    public boolean hasTransform() {
        return false;
    }

    /**
     * Generate the piece of code that generates the variant values required
     * for derivation.
     *
     * @param classifierVar    The name of the classifier variable
     * @param observablesClass The class that holds observable definfitions
     * @return The code to get the values
     */
    @Override
    public String generateVariants(String classifierVar, String observablesClass) {
        throw new UnsupportedOperationException("Generator derivations have no variants");
    }

    /**
     * Generate the piece of code that transforms the values required
     * for derivation.
     *
     * @param var           The variable that holds the value to be transformed
     * @param extra         The (nullable) variable that holds any extra context information
     * @param classifierVar The name of the classifier variable
     * @return The code to get the values
     */
    @Override
    public String generateBuilderTransform(String var, String extra, String classifierVar) {
        throw new UnsupportedOperationException("Generator derivations do not transform");
    }

    /**
     * Generate the piece of code that transforms the values required
     * for derivation.
     *
     * @return No transformation is generated
     */
    @Override
    public String generateClassificationTransform() {
        throw new UnsupportedOperationException("Generator derivations do not transform");
    }
}
