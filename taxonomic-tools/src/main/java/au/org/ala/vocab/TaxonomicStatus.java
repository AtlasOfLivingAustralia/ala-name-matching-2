package au.org.ala.vocab;

import lombok.Getter;
import org.gbif.dwc.terms.AlternativeNames;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;

/**
 * Taxonomic status vocabulary
 */
public enum TaxonomicStatus implements Term, AlternativeNames {
    accepted(true, false, false, false, false, false, false),
    unreviewed(true, false, false, false, false, true, false),
    inferredAccepted(true, true, false, false, false, true, true),
    homotypicSynonym(false, true, false, false, false, false, false),
    objectiveSynonym(false, true, false, false, false, false, false),
    heterotypicSynonym(false, true, false, false, false,false, false),
    subjectiveSynonym(false, true, false, false, false, false, false),
    synonym(false, true, false, false, false, false, false),
    proParteSynonym(false, true, false, false, false, false, false),
    unreviewedSynonym(false, true, false, false, false, true, false),
    inferredSynonym(false, true, false, false, false, true, true),
    misapplied(false, false, true, false, false, false, false),
    miscellaneousLiterature(false, false, false, false, false, false, false),
    excluded(false, false, false, true, false, false, false),
    inferredExcluded(false, false, false, true, false, true, true),
    invalid(false, false, false, false, true, true, true),
    inferredInvalid(false, false, false, false, true, true, true),
    unplaced(false, false, false, false, true, false, false),
    inferredUnplaced(false, false, false, false, true, true, true),
    incertaeSedis(true, false, false, false, true, false, false),
    speciesInquirenda(true, false, false, false, true, false, false),
    unknown(false, false, false, false, true, true, true);

    private static final String PREFIX = "ts";
    private static final String NS = "http://ala.org.au/vocabulary/1.0/taxonomicStatus/";
    private static final URI NS_URI = URI.create(NS);

    static {
        TermFactory.instance().registerTermEnum(TaxonomicStatus.class, PREFIX);
    }

    @Getter
    private final boolean acceptedFlag;
    @Getter
    private final boolean synonymFlag;
    @Getter
    private final boolean misappliedFlag;
    @Getter
    private final boolean excludedFlag;
    @Getter
    private final boolean doubtfulFlag;
    @Getter 
    private final boolean unreviewedFlag;
    @Getter
    private final boolean inferredFlag;
    @Getter
    private final String[] alternatives;

    TaxonomicStatus(boolean accepted, boolean synonym, boolean misapplied, boolean excluded, boolean doubtful, boolean unreviewed, boolean inferrred, String... alternatives) {
        this.acceptedFlag = accepted;
        this.synonymFlag = synonym;
        this.misappliedFlag = misapplied;
        this.excludedFlag = excluded;
        this.doubtfulFlag = doubtful;
        this.unreviewedFlag = unreviewed;
        this.inferredFlag = inferrred;
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

    /**
     * Is this a mainstream "placed" taxonomic entry?
     *
     * @return True for a "mainstream" accepted or synonym taxonomic statux
     */
    public boolean isPlaced() {
        return this.isAcceptedFlag() || this.isSynonymFlag();
    }

    /**
     * Is this a redirecting synonym-like taxonomic entry?
     *
     * @return True for a synonym-like status
     */
    public boolean isSynonymLike() {
        return !this.isAcceptedFlag() && (this.isSynonymFlag() || this.isMisappliedFlag() || this.isExcludedFlag());
    }
}
