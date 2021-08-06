package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.util.SimpleClassifier;
import au.org.ala.vocab.ALATerm;
import com.google.common.base.Enums;
import org.gbif.api.vocabulary.NomenclaturalCode;
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
     * @param classification The classification
     *
     * @throws StoreException     if an error occurs updating the classification
     */
    @Override
    public void analyse(AlaLinnaeanClassification classification) throws StoreException {
        final NameParser parser = PARSER.get();
        final String scientificName = classification.scientificName;
        Rank rank = classification.taxonRank;
        final NomenclaturalCode nomenclaturalCode = classification.nomenclaturalCode;
        final NomCode nomCode = nomenclaturalCode == null ? null : Enums.getIfPresent(NomCode.class, nomenclaturalCode.name()).orNull();
        if (rank == null)
            rank = Rank.UNRANKED;
        try {
            ParsedName name = parser.parse(scientificName, rank, nomCode);
            rank = name.getRank();
            if (classification.scientificNameAuthorship == null && name.hasAuthorship()) {
                classification.addIssue(ALATerm.canonicalMatch);
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
            classification.addIssue(ALATerm.unparsableName);
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
                classification.taxonRank = rank;
            } else if (classification.taxonRank == null && rank.notOtherOrUnranked())
                classification.taxonRank = rank;
        }
        if (classification.parentNameUsageId != null && classification.parentNameUsageId.equals(classification.taxonId)) {
            classification.addIssue(ALATerm.taxonParentLoop);
            classification.parentNameUsageId = null;
        }
        if (classification.acceptedNameUsageId != null && classification.acceptedNameUsageId.equals(classification.taxonId)) {
            classification.addIssue(ALATerm.taxonAcceptedLoop);
            classification.acceptedNameUsageId = null;
        }
   }
}
