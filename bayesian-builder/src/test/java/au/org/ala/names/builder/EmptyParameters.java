package au.org.ala.names.builder;

import au.org.ala.bayesian.Parameters;

/**
 * Empty parameter set for testing
 */
public class EmptyParameters implements Parameters {
    @Override
    public void load(double[] vector) {

    }

    @Override
    public double[] store() {
        return new double[0];
    }
}
