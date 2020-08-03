package au.org.ala.names;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.EvidenceAnalyser;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import au.org.ala.util.SimpleClassifier;
import com.google.common.base.Enums;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.NameParser;
import org.gbif.nameparser.api.ParsedName;
import org.gbif.nameparser.api.Rank;
import org.gbif.nameparser.api.UnparsableNameException;

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
    public void analyse(Classifier classifier) throws StoreException {
        NameParser parser = PARSER.get();
        String scientificName = classifier.get(AlaLinnaeanFactory.scientificName);
        String taxonRank = classifier.get(AlaLinnaeanFactory.taxonRank);
        Rank rank = taxonRank == null ? Rank.UNRANKED : Enums.getIfPresent(Rank.class, taxonRank).or(Rank.UNRANKED);
        try {
            ParsedName name = parser.parse(scientificName, rank, null);
            rank = name.getRank();
            if (rank != null && rank != Rank.UNRANKED) {
                if (Rank.SPECIES.higherThan(rank)) {
                    rank = Rank.INFRASPECIFIC_NAME;
                    classifier.replace(AlaLinnaeanFactory.taxonRank, rank.name());
                } else if (!classifier.has(AlaLinnaeanFactory.taxonRank))
                    classifier.add(AlaLinnaeanFactory.taxonRank, rank.name());
            }
            if (!classifier.has(AlaLinnaeanFactory.specificEpithet) && name.getSpecificEpithet() != null)
                classifier.add(AlaLinnaeanFactory.specificEpithet, name.getSpecificEpithet());
            if (!classifier.has(AlaLinnaeanFactory.genus) && rank != null && !rank.higherThan(Rank.GENUS) && name.getGenus() != null)
                classifier.add(AlaLinnaeanFactory.genus, name.getGenus());
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
    public void analyse(AlaLinnaeanClassification classification) throws InferenceException, StoreException {
        SimpleClassifier classifier = new SimpleClassifier();
        classification.translate(classifier);
        this.analyse(classifier);
        classification.populate(classifier, true);
    }
}
