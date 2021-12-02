package au.org.ala.vocab;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;

public enum TestTerms implements Term {
    test1,
    test2,
    test3;

    public static final String NS = "http://ala.org.au/test/1.0/";
    public static final URI NAMESPACE = URI.create(NS);
    public static final String PREFIX = "test:";

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
        for (Term term : TestTerms.values()) {
            factory.registerTerm(term);
        }
    }
}

