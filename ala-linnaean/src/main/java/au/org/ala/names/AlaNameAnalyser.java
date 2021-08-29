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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AlaNameAnalyser implements Analyser<AlaLinnaeanClassification> {
    private static final ThreadLocal<NameParser> PARSER = ThreadLocal.withInitial(NameParserGBIF::new);
    private static final Pattern MARKER_ENDING = Pattern.compile(
           "(^|\\s+)(" + String.join("|",
                   Arrays.stream(Rank.values())
                           .map(r -> r.getMarker())
                           .filter(Objects::nonNull)
                           .map(m -> m.endsWith(".") ? m.substring(0, m.length() - 1) : m)
                           .collect(Collectors.toList()
                           )
           ) + ")\\.?$");

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required.
     *
     * @param classification The classification
     * @param strict Impose strict limits on analysus
     *
     * @throws StoreException     if an error occurs updating the classification
     */
    @Override
    public void analyse(AlaLinnaeanClassification classification, boolean strict) throws StoreException, InferenceException {
        final NameParser parser = PARSER.get();
        final String scientificName = classification.scientificName;
        final String acceptedNameUsageId = classification.acceptedNameUsageId;
        final TaxonomicStatus taxonomicStatus = classification.taxonomicStatus != null ? classification.taxonomicStatus : TaxonomicStatus.unknown;
        Rank rank = classification.taxonRank;
        final NomenclaturalCode nomenclaturalCode = classification.nomenclaturalCode;
        final NomCode nomCode = nomenclaturalCode == null ? null : Enums.getIfPresent(NomCode.class, nomenclaturalCode.name()).orNull();
        if (rank == null)
            rank = Rank.UNRANKED;


        // Fill out parsed entities
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

        // Remove rank marker, if present, and flag
        if (classification.scientificName != null) {
            Matcher matcher = MARKER_ENDING.matcher(classification.scientificName);
            if (matcher.find()) {
                classification.addIssue(AlaLinnaeanFactory.INDETERMINATE_NAME);
                if (strict) {
                    classification.scientificName = classification.scientificName.substring(0, matcher.start()).trim();
                    classification.taxonRank = null;
                    rank = Rank.UNRANKED;
                    if (classification.scientificName.isEmpty()) {
                        throw new InferenceException("Supplied scientific name is a rank marker.");
                    }
                }
            }
        }

        // Infer rank
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

        // Generalise sub-species ranks
        if (rank != null && rank.notOtherOrUnranked()) {
            if (Rank.SPECIES.higherThan(rank)) {
                rank = Rank.INFRASPECIFIC_NAME;
                classification.taxonRank = rank;
            } else if (classification.taxonRank == null && rank.notOtherOrUnranked())
                classification.taxonRank = rank;
        }

        // Remove loops in taxonomy
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
     * @param name The observable that gives the name
     * @param complete The observable that gives the complete name
     * @param additional The observable that gives additional disambiguation, geneerally complete = name + ' ' + additional
     * @param canonical Only include canonical names

     * @return All the names that refer to the classification
     */
    @Override
    public Set<String> analyseNames(Classifier classifier, Observable name, Optional<Observable> complete, Optional<Observable> additional, boolean canonical) throws InferenceException, StoreException {
        Set<String> allNames = new LinkedHashSet<>();

        Rank rank = classifier.has(AlaLinnaeanFactory.taxonRank) ? classifier.get(AlaLinnaeanFactory.taxonRank) : Rank.UNRANKED;
        final NomenclaturalCode nomenclaturalCode = classifier.get(AlaLinnaeanFactory.nomenclaturalCode);
        final NomCode nomCode = nomenclaturalCode == null ? null : Enums.getIfPresent(NomCode.class, nomenclaturalCode.name()).orNull();
        Set<String> allScientificNameAuthorship = !canonical && additional.isPresent() ? classifier.getAll(additional.get()) : Collections.emptySet();
        Set<String> allScientificNames = classifier.getAll(name);
        Set<String> allCompleteNames = !canonical && complete.isPresent() ? classifier.getAll(complete.get()) : Collections.emptySet();
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
        for (String nm: allScientificNames) {
            CleanedScientificName n = new CleanedScientificName(nm);
            allNames.add(n.getName());
            allNames.add(n.getBasic());
            allNames.add(n.getNormalised());

            // Add parsed versions of the name
            try {
                ParsedName pn = PARSER.get().parse(n.getNormalised(), rank, nomCode);
                if (pn.getState() == ParsedName.State.COMPLETE) {
                    allNames.add(pn.canonicalName());
                    if (pn.getType() == NameType.SCIENTIFIC) {
                        // More minimal version if a recognised linnaean rank
                        if (pn.getRank() != null && pn.getRank().isLinnean()) {
                            allNames.add(pn.canonicalNameMinimal());
                        }
                        // Add infrageneric name without enclosing genus
                        if (pn.getInfragenericEpithet() != null && pn.getRank().isInfrageneric() && pn.getGenus() != null && !pn.getInfragenericEpithet().equals(pn.getGenus())) {
                            allNames.add(pn.getInfragenericEpithet());
                        }
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
        for (String nm: allCompleteNames) {
            CleanedScientificName n = new CleanedScientificName(nm);
            allNames.add(n.getName());
            allNames.add(n.getBasic());
            allNames.add(n.getNormalised());
        }
        return allNames;
    }
}
