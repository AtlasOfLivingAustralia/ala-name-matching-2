package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.Observable;
import au.org.ala.util.CleanedScientificName;
import au.org.ala.util.SimpleClassifier;
import au.org.ala.vocab.ALATerm;
import au.org.ala.vocab.TaxonomicStatus;
import com.google.common.base.Enums;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.*;
import org.gbif.nameparser.util.NameFormatter;
import org.gbif.nameparser.util.RankUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AlaNameAnalyser implements Analyser<AlaLinnaeanClassification> {
    private static final ThreadLocal<NameParser> PARSER = ThreadLocal.withInitial(NameParserGBIF::new);
    private static final Observable NAME_COMPLETE = new Observable(ALATerm.nameComplete);

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
        final String acceptedNameUsageId = classification.acceptedNameUsageId;
        final TaxonomicStatus taxonomicStatus = classification.taxonomicStatus != null ? classification.taxonomicStatus : TaxonomicStatus.unknown;
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
            if (acceptedNameUsageId == null) { // Only do this for non-synonyms to avoid dangling speciesID and classID
                if (classification.specificEpithet == null && name.getSpecificEpithet() != null)
                    classification.specificEpithet = name.getSpecificEpithet();
                if (classification.genus == null && rank != null && !rank.higherThan(Rank.GENUS) && name.getGenus() != null)
                    classification.genus = name.getGenus();
            }
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

    /**
     * Build a collection of base names for the classification.
     * <p>
     * If a classification can be referred to in multiple ways, this method
     * builds the various ways of referring to the classification.
     * </p>
     *
     * @param classifier The classification
     * @return All the names that refer to the classification
     */
    @Override
    public Set<String> analyseNames(Classifier classifier) throws InferenceException, StoreException {
        Set<String> allNames = new HashSet<>();

        Rank rank = classifier.has(AlaLinnaeanFactory.taxonRank) ? classifier.get(AlaLinnaeanFactory.taxonRank) : Rank.UNRANKED;
        final NomenclaturalCode nomenclaturalCode = classifier.get(AlaLinnaeanFactory.nomenclaturalCode);
        final NomCode nomCode = nomenclaturalCode == null ? null : Enums.getIfPresent(NomCode.class, nomenclaturalCode.name()).orNull();
        Set<String> allScientificNameAuthorship = classifier.getAll(AlaLinnaeanFactory.scientificNameAuthorship);
        Set<String> allScientificNames = classifier.getAll(AlaLinnaeanFactory.scientificName);
        Set<String> allCompleteNames = classifier.getAll(NAME_COMPLETE);
        // Get name if not directly specified
        if (allScientificNames.isEmpty() && classifier.has(AlaLinnaeanFactory.genus) && classifier.has(AlaLinnaeanFactory.specificEpithet)) {
            allScientificNames = classifier.getAll(AlaLinnaeanFactory.genus).stream().flatMap(g -> classifier.getAll(AlaLinnaeanFactory.specificEpithet).stream().map(s -> g + " " + s)).collect(Collectors.toSet());
            rank = rank == Rank.UNRANKED ? Rank.SPECIES : rank;
        }
        if (allScientificNames.isEmpty() && classifier.has(AlaLinnaeanFactory.genus)) {
            allScientificNames = classifier.getAll(AlaLinnaeanFactory.genus);
            rank = rank == Rank.UNRANKED ? Rank.GENUS : rank;
        }
        if (allScientificNames.isEmpty() && classifier.has(AlaLinnaeanFactory.family)) {
            allScientificNames = classifier.getAll(AlaLinnaeanFactory.family);
            rank = rank == Rank.UNRANKED ? Rank.FAMILY : rank;
        }
        if (allScientificNames.isEmpty() && classifier.has(AlaLinnaeanFactory.order)) {
            allScientificNames = classifier.getAll(AlaLinnaeanFactory.order);
            rank = rank == Rank.UNRANKED ? Rank.ORDER : rank;
        }
        if (allScientificNames.isEmpty() && classifier.has(AlaLinnaeanFactory.class_)) {
            allScientificNames = classifier.getAll(AlaLinnaeanFactory.class_);
            rank = rank == Rank.UNRANKED ? Rank.CLASS : rank;
        }
        if (allScientificNames.isEmpty() && classifier.has(AlaLinnaeanFactory.phylum)) {
            allScientificNames = classifier.getAll(AlaLinnaeanFactory.phylum);
            rank = rank == Rank.UNRANKED ? Rank.PHYLUM : rank;
        }
        if (allScientificNames.isEmpty() && classifier.has(AlaLinnaeanFactory.kingdom)) {
            allScientificNames = classifier.getAll(AlaLinnaeanFactory.kingdom);
            rank = rank == Rank.UNRANKED ? Rank.KINGDOM : rank;
        }

        if (allScientificNames.isEmpty())
            throw new InferenceException("No scientific name for " + classifier.get(AlaLinnaeanFactory.taxonId));
        for (String name: allScientificNames) {
            CleanedScientificName n = new CleanedScientificName(name);
            allNames.add(n.getName());
            allNames.add(n.getBasic());
            allNames.add(n.getNormalised());

            // Add parsed versions of the name
            try {
                ParsedName pn = PARSER.get().parse(name, rank, nomCode);
                n = new CleanedScientificName(pn.canonicalName());
                if (!allNames.contains(n.getNormalised())) {
                    allNames.add(n.getNormalised());
                    classifier.add(AlaLinnaeanFactory.scientificName, n.getNormalised());
                }
                n = new CleanedScientificName(pn.canonicalNameMinimal());
                if (!allNames.contains(n.getNormalised())) {
                    allNames.add(n.getNormalised());
                    classifier.add(AlaLinnaeanFactory.scientificName, n.getNormalised());
                }
                if (pn.getInfragenericEpithet() != null) {
                    n = new CleanedScientificName(pn.getInfragenericEpithet());
                    if (!allNames.contains(n.getNormalised())) {
                        allNames.add(n.getNormalised());
                        classifier.add(AlaLinnaeanFactory.scientificName, n.getNormalised());
                    }
                }
            } catch (UnparsableNameException e) {
                // Ignore unparsable names
            }
        }

        // Add name/author pairs
        if (allCompleteNames.isEmpty() && !allScientificNameAuthorship.isEmpty()) {
            allCompleteNames = allScientificNames.stream().flatMap(n -> allScientificNameAuthorship.stream().map(a -> n + " " + a)).collect(Collectors.toSet());
        }
        for (String name: allCompleteNames) {
            CleanedScientificName n = new CleanedScientificName(name);
            allNames.add(n.getName());
            allNames.add(n.getBasic());
            allNames.add(n.getNormalised());
        }
        return allNames;
    }
}
