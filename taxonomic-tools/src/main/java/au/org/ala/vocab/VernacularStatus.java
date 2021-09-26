package au.org.ala.vocab;

import lombok.Getter;
import org.gbif.dwc.terms.AlternativeNames;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;

/**
 * Vernacular status vocabulary
 */
public enum VernacularStatus implements Term, AlternativeNames {
    legislated,
    standard,
    preferred,
    common,
    traditionalKnowledge,
    local;

    private static final String PREFIX = "ts";
    private static final String NS = "http://id.ala.org.au/vocabulary/1.0/vernacularStatus/";
    private static final URI NS_URI = URI.create(NS);

    static {
        TermFactory.instance().registerTermEnum(VernacularStatus.class, PREFIX);
    }

    @Getter
    private String[] alternatives;

    private VernacularStatus(String... alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public String[] alternativeNames() {
        return this.alternatives;
    }

    @Override
    public String prefix() {
        return PREFIX;
    }

    @Override
    public URI namespace() {
        return NS_URI;
    }

    @Override
    public String simpleName() {
        return this.name();
    }

    @Override
    public boolean isClass() {
        return false;
    }
}
