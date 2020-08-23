package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.util.SimpleClassifier;
import au.org.ala.vocab.ALATerm;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.*;
import org.gbif.nameparser.util.NameFormatter;
import org.gbif.nameparser.util.RankUtils;

import java.util.Arrays;
import java.util.Objects;

public class AlaNameAnalyser implements Analyser<AlaLinnaeanClassification> {
    private static final ThreadLocal<NameParser> PARSER = ThreadLocal.withInitial(NameParserGBIF::new);

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required.
     *
     * @param classifier The classifier
     *
     * @throws StoreException     if an error occurs updating the classifier
     */
    @Override
    public void analyse(AlaLinnaeanClassification classification) throws StoreException {
        final NameParser parser = PARSER.get();
        final String scientificName = classification.scientificName;
        final String taxonRank = classification.taxonRank;
        final String nomenclaturalCode = classification.nomenclaturalCode;
        Rank rank = RankUtils.inferRank(taxonRank);
        if (rank == null)
            rank = Rank.UNRANKED;
        NomCode nomCode = nomenclaturalCode == null ? null : Arrays.stream(NomCode.values()).filter(c -> nomenclaturalCode.equalsIgnoreCase(c.getAcronym())).findFirst().orElse(null);
        try {
            ParsedName name = parser.parse(scientificName, rank, nomCode);
            rank = name.getRank();
            if (classification.scientificNameAuthorship == null && name.hasAuthorship()) {
                classification.getIssues().add(ALATerm.canonicalMatch);
                classification.scientificNameAuthorship = NameFormatter.authorshipComplete(name);
                classification.scientificName = NameFormatter.canonicalWithoutAuthorship(name);
            }
            if (classification.specificEpithet == null && name.getSpecificEpithet() != null)
                classification.specificEpithet = name.getSpecificEpithet();
            if (classification.genus == null && rank != null && !rank.higherThan(Rank.GENUS) && name.getGenus() != null)
                classification.genus = name.getGenus();
            if (classification.cultivarEpithet == null && name.getCultivarEpithet() != null)
                classification.cultivarEpithet = name.getCultivarEpithet();
            if (classification.phraseName == null && name.getPhrase() != null)
                classification.phraseName = name.getPhrase();
        } catch (UnparsableNameException e) {
            // Ignore this, we'll just have to roll along with an unrecognisable name
        }
        if (classification.scientificName == null) {
            if (classification.specificEpithet != null && classification.genus != null) {
                classification.scientificName = classification.genus + " " + classification.specificEpithet;
                rank = Rank.SPECIES;
            } else if (classification.genus != null) {
                classification.scientificName = classification.genus;
                rank = Rank.GENUS;
            } else if (classification.family != null) {
                classification.scientificName = classification.family;
                rank = Rank.FAMILY;
            } else if (classification.order != null) {
                classification.scientificName = classification.order;
                rank = Rank.ORDER;
            } else if (classification.class_ != null) {
                classification.scientificName = classification.class_;
                rank = Rank.CLASS;
            } else if (classification.phylum != null) {
                classification.scientificName = classification.phylum;
                rank = Rank.PHYLUM;
            } else if (classification.kingdom != null) {
                classification.scientificName = classification.kingdom;
                rank = Rank.KINGDOM;
            }
        }
        if (rank != null && rank.notOtherOrUnranked()) {
            if (Rank.SPECIES.higherThan(rank)) {
                rank = Rank.INFRASPECIFIC_NAME;
                classification.taxonRank = rank.name().toLowerCase();
            } else if (classification.taxonRank == null && rank != Rank.UNRANKED & rank != rank.OTHER)
                classification.taxonRank = rank.name().toLowerCase();
        }
    }
}
