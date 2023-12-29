package au.org.ala.vocab;

import lombok.Getter;
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
public enum ALATerm implements Term, AlternativeNames {
    // Terms associated with taxonomy
    /** The supplied nomenclatural code */
    verbatimNomenclaturalCode,
    /** The supplied taxonomicStatus */
    verbatimTaxonomicStatus,
    /** The supplied nomenclatural status */
    verbatimNomenclaturalStatus,
    /** The supplied taxon remarks */
    verbatimTaxonRemarks,
    /** A soundex of the scientific name */
    soundexScientificName,
    /** An alternate scientific name (synonym) for a taxon */
    altScientificName,
    /** A soundex of an alternate scientific name */
    soundexAltScientificName,
    /** The name and authorship, with the author correctly placed */
    nameComplete,
    /** A soundex of the scientific name and authorship */
    soundexNameComplete,
    /** The name and authorship, formatted in some way, usually HTML */
    nameFormatted,
    /** The canonical authorship (authorship without variations due to capitalisation and punctiation) */
    canonicalAuthorship,
    /** A synonym of the scientific nasme */
    synonymScientificName,
    /** The type of name (scientific, cultivate etc) @see org.gbif.nameparser.api.NameType */
    nameType,
    /** A broad synonym of the scientific name (one that ranges up and down the ranks a bit) */
    broadSynonymScientificName,
    /** A soundex of the specific epithet */
    soundexSpecificEpithet,
    /** A numerical rank identifier for the taxon rank */
    rankID,
    /** A taxon identifier for the kingdom */
    kingdomID,
    /** An alternate name for a kingdom */
    altKingdom,
    /** A soundex of the kingdom */
    soundexKingdom,
    /** The kingdom including possible alternatives where the exact kingdom in question is subjective  */
    expandedKingdom,
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
    /** The culitvar name */
    cultivarEpithet,
    /** The phrase name */
    phraseName,
    /** The phrase name voucher (ie. person vouching for a name) */
    voucher,
    /** The phrase name nominating party */
    nominatingParty,
    /** The principal taxon identifier, for taxa that may have been re-assigned */
    principalTaxonID,
    /** The principal scientific name, for taxa that may have been re-assigned */
    principalScientificName,

    // Terms associated with location determination
    /** The location identifier for a parent-child relationship */
    parentLocationID,
    /** The location identifier for a synonym relationship */
    acceptedLocationID,
    /** The geography type of a location. @see GeographyType */
    geographyType,
    /** The soundex locality */
    soundexLocality,
    /** The locality identifier */
    localityID,
    /** The soundex state or province */
    soundexStateProvince,
    /** The state or province identifier */
    stateProvinceID,
    /** The soundex country */
    soundexCountry,
    /** The country identifier */
    countryID,
    /** The soundex continent */
    soundexContinent,
    /** The continent identifier */
    continentID,
    /** The soundex island group */
    soundexIslandGroup,
    /** The island group identifier */
    islandGroupID,
    /** The soundex water body */
    soundexWaterBody,
    /** The water body identifier */
    waterBodyID,
    /** Location area (square kilometres assumed */
    area,

    // Generic information
    /** Context labels for concepts. See http://localcontexts.org/ */
    labels,
    /** A value */
    value,
    /** An identifier for a name */
    nameID,
    /** The status of a piece of information (current, superseeded, etc.) */
    status,
    /** A score for concept priority */
    priority,

    // Record classes
    /** Record type describing an unplaced vernacular name */
    UnplacedVernacularName,
    /** Record type describing a variant (different source, spelling etc.) of a taxon */
    TaxonVariant,
    /** Record type describing a variant (different source, spelling etc.) locality name */
    LocationName,
    /** Record type describing a problem or note about a taxon */
    TaxonomicIssue;

    public static final String NS = "http://ala.org.au/terms/1.0/";
    public static final URI NAMESPACE = URI.create(NS);
    public static final String PREFIX = "ala:";

    @Getter
    private final String[] alternatives;

    ALATerm(String... alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public String qualifiedName() {
        return NS + this.simpleName();
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
        TermFactory.instance().registerTermEnum(ALATerm.class, PREFIX);
    }

}
