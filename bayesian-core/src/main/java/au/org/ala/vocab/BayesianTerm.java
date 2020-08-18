package au.org.ala.vocab;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;

/**
 * Terms used in building networks and indexes.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2016 CSIRO
 */
public enum BayesianTerm implements Term {
    /** A category weight - how likely it is to be a matching category */
    weight,
    /** The full name of a category */
    fullName,
    /** An alternate name for a category */
    altName,
    /** A term that should be copied */
    copy,
    /** An observable that contains additional classification values */
    additional,
    /** Is this a root category? */
    isRoot,
    /** Is this a synonym category? */
    isSynonym,
    /** The class used to build an index */
    builderClass,
    /** The class used for parameters */
    parametersClass,
    /** The class used for analysis */
    analyserClass,
    /** An analysis method - a method on an analyser that can be used to process a value */
    analysisMethod,
    /** An equivalence method - a method on an analyser that can be used to decide equality */
    equalityMethod,
    /** Record type describing document metadata */
    Metadata;

    public static final String NS = "http://id.ala.org.au/bayesian/1.0/";
    public static final URI NAMESPACE = URI.create(NS);
    public static final String PREFIX = "bayesian:";

    @Override
    public String qualifiedName() {
        return NS + this.simpleName();
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    @Override
    public URI namespace() {
        return NAMESPACE;
    }

    @Override
    public String simpleName() {
        return this.name();
    }

    public String toString() {
         return PREFIX + name();
    }

    @Override
    public boolean isClass() {
        return Character.isUpperCase(this.simpleName().charAt(0));
    }

    static {
        TermFactory factory = TermFactory.instance();
        for (Term term : BayesianTerm.values()) {
            factory.registerTerm(term);
        }
    }

}
