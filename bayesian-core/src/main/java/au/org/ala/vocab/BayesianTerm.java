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
    /** A unique identifier */
    identifier,
    /** The concept name */
    name,
    /** The full name of a category */
    fullName,
    /** An alternate name for a category */
    altName,
    /** Additional (disambiguating) name information */
    additionalName,
    /** This identifier navigates to the parent category */
    parent,
    /** This identifier navigates to the accepted category for synonyms */
    accepted,
    /** A term that should be copied from the actual category when something is a synonym of something else */
    copy,
    /** An observable that contains additional classification values */
    additional,
    /** Is this a root category? */
    isRoot,
    /** Is this a synonym category? */
    isSynonym,
    /** Is this something with an identifier that alredy exists? */
    identifierCreated,
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
    /** Issue flagging an invalid match */
    invalidMatch,
    /** Default concept type */
    Concept,
    /** Record type describing document metadata */
    Metadata;

    public static final String NS = "http://id.ala.org.au/bayesian/1.0/";
    public static final URI NAMESPACE = URI.create(NS);
    public static final String PREFIX = "bayesian";

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
         return this.prefixedName();
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
