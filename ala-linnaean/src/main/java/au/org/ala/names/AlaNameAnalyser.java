package au.org.ala.names;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.EvidenceAnalyser;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import au.org.ala.util.SimpleClassifier;
import org.gbif.api.exception.UnparsableException;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.api.vocabulary.Rank;
import org.gbif.nameparser.PhraseNameParser;
import org.gbif.nameparser.RankUtils;

public class AlaNameAnalyser extends EvidenceAnalyser<AlaLinnaeanClassification> {
    private static ThreadLocal<PhraseNameParser> PARSER = new ThreadLocal<PhraseNameParser>() {
      @Override
        protected PhraseNameParser initialValue() {
            return new PhraseNameParser();
        }
    };

    /**
     * Analyse the information in a classifier and extend the classifier
     * as required.
     *
     * @param classifier The classifier
     *
     * @throws InferenceException if an error occurs during inference
     * @throws StoreException     if an error occurs updating the classifier
     */
    @Override
    public void analyse(Classifier classifier) throws InferenceException, StoreException {
        super.analyse(classifier);
        PhraseNameParser parser = PARSER.get();
        String scientificName = classifier.get(AlaLinnaeanObservables.scientificName);
        String taxonRank = classifier.get(AlaLinnaeanObservables.taxonRank);
        Rank rank = null;
        try {
            if (taxonRank != null)
                rank = Rank.valueOf(taxonRank);
        } catch (IllegalArgumentException ex) {
        }
        try {
            ParsedName name = parser.parse(scientificName, rank);
            rank = name.getRank();
            if (rank != null && Rank.SPECIES.higherThan(rank)) {
                rank = Rank.INFRASPECIFIC_NAME;
                classifier.replace(AlaLinnaeanObservables.taxonRank, rank.name());
            } else if (!classifier.has(AlaLinnaeanObservables.taxonRank) && rank != null)
                classifier.add(AlaLinnaeanObservables.taxonRank, rank.name());
            if (!classifier.has(AlaLinnaeanObservables.specificEpithet) && name.getSpecificEpithet() != null)
                classifier.add(AlaLinnaeanObservables.specificEpithet, name.getSpecificEpithet());
            if (!classifier.has(AlaLinnaeanObservables.genus) && rank != null && !rank.higherThan(Rank.GENUS) && name.getGenusOrAbove() != null)
                classifier.add(AlaLinnaeanObservables.genus, name.getGenusOrAbove());
        } catch (UnparsableException ex) {
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
        super.analyse(classification);
        SimpleClassifier classifier = new SimpleClassifier();
        classification.translate(classifier);
        this.analyse(classifier);
        classification.populate(classifier, true);
    }
}
