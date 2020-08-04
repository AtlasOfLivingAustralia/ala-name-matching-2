package au.org.ala.names.builder;

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Parameters;

/**
 * The command line implementation of a builder.
 *
 * @param <C> The classification type used by the builder
 * @param <P> The parameters class for the builder
 * @param <B> The builder class to use
 * @param <I> The inferencer to use
 * @param <F> The factory to use
 */
public interface Cli<C extends Classification, P extends Parameters, B extends Builder<P>, I extends Inferencer<C, P>, F extends NetworkFactory<C, P, I, F>>{
}
