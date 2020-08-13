package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.util.SimpleClassifier;
import au.org.ala.vocab.ALATerm;
import com.google.common.base.Enums;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.*;
import org.gbif.nameparser.util.NameFormatter;

import java.util.Arrays;

public class AlaNameAnalyser implements EvidenceAnalyser<AlaLinnaeanClassification> {
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
    public void analyse(Classifier classifier, Issues issues) throws StoreException {
        final NameParser parser = PARSER.get();
        final String scientificName = classifier.get(AlaLinnaeanFactory.scientificName);
        final String taxonRank = classifier.get(AlaLinnaeanFactory.taxonRank);
        final String nomenclaturalCode = classifier.get(AlaLinnaeanFactory.nomenclaturalCode);
        Rank rank = taxonRank == null ? Rank.UNRANKED : Enums.getIfPresent(Rank.class, taxonRank).or(Rank.UNRANKED);
        NomCode nomCode = nomenclaturalCode == null ? null : Arrays.stream(NomCode.values()).filter(c -> nomenclaturalCode.equalsIgnoreCase(c.getAcronym())).findFirst().orElse(null);
        try {
            ParsedName name = parser.parse(scientificName, rank, nomCode);
            rank = name.getRank();
            if (rank != null && rank != Rank.UNRANKED) {
                if (Rank.SPECIES.higherThan(rank)) {
                    rank = Rank.INFRASPECIFIC_NAME;
                    classifier.replace(AlaLinnaeanFactory.taxonRank, rank.name());
                } else if (!classifier.has(AlaLinnaeanFactory.taxonRank))
                    classifier.add(AlaLinnaeanFactory.taxonRank, rank.name());
            }
            if (!classifier.has(AlaLinnaeanFactory.scientificNameAuthorship) && name.hasAuthorship()) {
                issues.add(ALATerm.canonicalMatch);
                classifier.replace(AlaLinnaeanFactory.scientificNameAuthorship, NameFormatter.authorshipComplete(name));
                classifier.replace(AlaLinnaeanFactory.scientificName, NameFormatter.canonicalWithoutAuthorship(name));
            }
            if (!classifier.has(AlaLinnaeanFactory.specificEpithet) && name.getSpecificEpithet() != null)
                classifier.add(AlaLinnaeanFactory.specificEpithet, name.getSpecificEpithet());
            if (!classifier.has(AlaLinnaeanFactory.genus) && rank != null && !rank.higherThan(Rank.GENUS) && name.getGenus() != null)
                classifier.add(AlaLinnaeanFactory.genus, name.getGenus());
            if (!classifier.has(AlaLinnaeanFactory.cultivarEpithet) && name.getCultivarEpithet() != null)
                classifier.add(AlaLinnaeanFactory.cultivarEpithet, name.getCultivarEpithet());
            if (!classifier.has(AlaLinnaeanFactory.phraseName) && name.getCultivarEpithet() != null)
                classifier.add(AlaLinnaeanFactory.phraseName, name.getCultivarEpithet());
        } catch (UnparsableNameException e) {
            // Ignore this, we'll just have to roll along with an unrecognisable name
        }
    }

    /**
     * Analyse the information in a classification and extend the classification
     * as required.
     *
     * @param classification The classification
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException     if an error occurs updating the classifier
     */
    @Override
    public void analyse(AlaLinnaeanClassification classification, Issues issues) throws InferenceException, StoreException {
        SimpleClassifier classifier = new SimpleClassifier();
        classification.translate(classifier);
        this.analyse(classifier, issues);
        classification.populate(classifier, true);
    }
}
