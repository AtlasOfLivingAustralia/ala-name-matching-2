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
    legislated(500, false),
    standard(400, false),
    preferred(300, false),
    common(200, false),
    traditionalKnowledge(200, false),
    local(100, false),
    deprecated(-1, true);

    private static final String PREFIX = "ts";
    private static final String NS = "http://ala.org.au/vocabulary/1.0/vernacularStatus/";
    private static final URI NS_URI = URI.create(NS);
    /** Priority boost for preferred versions of names with this status */
    public static final int PREFERRED_BOOST = 50;

    static {
        TermFactory.instance().registerTermEnum(VernacularStatus.class, PREFIX);
    }

    @Getter
    private int priority;
    @Getter
    private boolean exclude;
    @Getter
    private String[] alternatives;

    private VernacularStatus(int priority, boolean exclude, String... alternatives) {
        this.priority = priority;
        this.exclude = exclude;
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
