package au.org.ala.bayesian.derivation;

/**
 * Generate a UUID identifier for something that has no
 */
public class IdentifierGeneratorDerivation extends GeneratorDerivation {
    /** Common generator phrase */
    private static final String GENERATOR = "java.util.UUID.randomUUID().toString()";

    /**
     * Get the class of the value required for derivation.
     *
     * @return The {@link String} class
     */
    public Class<?> getValueClass() {
        return String.class;
    }

    /**
     * Generate the piece of code that generates the set value required
     * for derivation.
     *
     * @param classifierVar    The name of the classifier variable
     * @param observablesClass The class that holds observable definfitions
     * @return The code to get the values
     */
    @Override
    public String generateValue(String classifierVar, String observablesClass) {
        return GENERATOR;
    }
}
