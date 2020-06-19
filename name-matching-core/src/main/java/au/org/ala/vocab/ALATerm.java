package au.org.ala.vocab;

import org.gbif.dwc.terms.AlternativeNames;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.URI;

/**
 * Terms used by the ALA.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2016 CSIRO
 */
public enum ALATerm implements Term {
    /** The supplied nomenclatural code */
    verbatimNomenclaturalCode,
    /** The supplied taxonomicStatus */
    verbatimTaxonomicStatus,
    /** The supplied nomenclatural status */
    verbatimNomenclaturalStatus,
    /** The supplied taxon remarks */
    verbatimTaxonRemarks,
    /** An alternate scientific name (synonym) for a taxon */
    altScientificName,
    /** A soundex of the scientific name */
    soundexScientificName,
    /** The name and authorship, with the author correctly placed */
    nameComplete,
    /** The name and authorship, formatted in some way, usually HTML */
    nameFormatted,
    /** A soundex of the specific epithet */
    soundexSpecificEpithet,
    /** An identifier for non-scientific names */
    nameID,
    /** The status of a piece of information (current, superseeded, etc.) */
    status,
    /** A score for taxon/name priority */
    priority,
    /** A taxon weight - how likely it is to be a matching taxon */
    weight,
    /** A taxon identifier for the kingdom */
    kingdomID,
    /** An alternate name for a kingdom */
    altKingdom,
    /** A soundex of the kingdom */
    soundexKingdom,
    /** A taxon identifier for the phylum */
    phylumID,
    /** An alternate name for a phylum */
    altPhylum,
    /** A soundex of the phylum */
    soundexPhylum,
    /** A taxon identifier for the class */
    classID,
    /** An alternate name for a class */
    altClass,
    /** A soundex of the class */
    soundexClass,
    /** A taxon identifier for the order */
    orderID,
    /** An alternate name for an order */
    altOrder,
    /** A soundex of the order */
    soundexOrder,
    /** A taxon identifier for the family */
    familyID,
    /** An alternate name for a family */
    altFamily,
    /** A soundex of the family */
    soundexFamily,
    /** A taxon identifier for the genus */
    genusID,
    /** An alternate name for a genus */
    altGenus,
    /** A soundex of the genus */
    soundexGenus,
    /** A taxon identifier for the species */
    speciesID,
    /** The subphylum classification */
    subphylum,
    /** The subclass classification */
    subclass,
    /** The suborder classification */
    suborder,
    /** The infraorder classification */
    infraorder,
    /** Context labels for names. See http://localcontexts.org/ */
    labels,
    /** A value */
    value,
    /** The principal taxon identifier, for taxa that may have been re-assigned */
    principalTaxonID,
    /** The principal scientific name, for taxa that may have been re-assigned */
    principalScientificName,
    /** Is this a root taxon? */
    isRoot,
    /** Is this a synonym taxon? */
    isSynonym,
    /** The class used to build an index */
    builderClass,
    /** The class used for parameters */
    parametersClass,
    /** Record type describing document metadata */
    Metadata,
    /** Record type describing an unplaced vernacular name */
    UnplacedVernacularName,
    /** Record type describing a variant (different source, spelling etc.) of a taxon */
    TaxonVariant,
    /** Record type describing a problem or note about a taxon */
    TaxonomicIssue;

    public static final String NS = "http://id.ala.org.au/terms/1.0/";
    public static final URI NAMESPACE = URI.create(NS);
    public static final String PREFIX = "ala:";

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
        for (Term term : ALATerm.values()) {
            factory.registerTerm(term);
        }
    }

}
