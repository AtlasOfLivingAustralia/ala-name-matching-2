package au.org.ala.vocab;

import lombok.Getter;
import org.gbif.dwc.terms.AlternativeNames;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;

/**
 * Geography level vocabulary
 */
public enum GeographyType implements Term, AlternativeNames {
    municipality(DwcTerm.municipality),
    county(DwcTerm.county),
    stateProvince(DwcTerm.stateProvince),
    country(DwcTerm.country),
    continent(DwcTerm.continent),
    island(DwcTerm.island),
    islandGroup(DwcTerm.islandGroup),
    waterBody(DwcTerm.waterBody, "sea", "ocean", "gulf", "bay"),
    region(null),
    other(null);

    private static final String PREFIX = "gt";
    private static final String NS = "http://ala.org.au/vocabulary/1.0/geographyType/";
    private static final URI NS_URI = URI.create(NS);

    static {
        TermFactory.instance().registerTermEnum(GeographyType.class, PREFIX);
    }

    @Getter
    private final Term dwcTerm;
    @Getter
    private final String[] alternatives;

    GeographyType(Term dwcTerm, String... alternatives) {
        this.dwcTerm = dwcTerm;
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
