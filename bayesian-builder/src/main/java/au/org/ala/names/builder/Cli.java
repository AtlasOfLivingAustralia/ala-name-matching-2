package au.org.ala.names.builder;

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.NetworkFactory;

/**
 * The command line implementation of a builder.
 *
 * @param <C> The classification type used by the builder
 * @param <B> The builder class to use
 * @param <I> The inferencer to use
 * @param <F> The factory to use
 */
public interface Cli<C extends Classification<C>, B extends Builder, I extends Inferencer<C>, F extends NetworkFactory<C, I, F>>{
}
