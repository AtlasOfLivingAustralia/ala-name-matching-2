package au.org.ala.bayesian;

/**
 * Generate an appropriate output for a network.
 */
abstract public class Generator {
    abstract public void generate(NetworkCompiler compiler) throws Exception;
}
