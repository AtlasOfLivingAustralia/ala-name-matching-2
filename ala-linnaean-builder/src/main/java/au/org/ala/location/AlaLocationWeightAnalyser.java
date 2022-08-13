package au.org.ala.location;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.AlaLinnaeanFactory;
import au.org.ala.names.NomStatus;
import au.org.ala.names.builder.Annotator;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.builder.LoadStore;
import au.org.ala.names.builder.WeightAnalyser;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.util.Counter;
import au.org.ala.vocab.GeographyType;
import au.org.ala.vocab.TaxonomicStatus;
import com.opencsv.CSVReader;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.nameparser.api.Rank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ALA-specific weight analyser.
 * <p>
 * This contains several parts.
 * A default weight based on the score allocated by the merging process (or 1 by default).
 * A lookup weight based on tables of data in a DwCA.
 * A modifier that prefers Linnaean ranks over assorted mid-rank information.
 * </p>
 */
public class AlaLocationWeightAnalyser implements WeightAnalyser, Annotator {
    private static final Logger logger = LoggerFactory.getLogger(AlaLocationWeightAnalyser.class);

    /**
     * Construct a weight analyser for a configuration
     *
     * @param configuration The configuration
     * @throws Exception If unable to build the analyser
     */
    public AlaLocationWeightAnalyser(IndexBuilderConfiguration configuration) throws Exception {
    }

    /**
     * Compute the base weight of the classifier.
     * <p>
     * The base element is the area of the entity.
     * The area of the Earth is 510 million km^2.
     * Probability calculations tend to go awry at about 0.00001.
     * For this reason, weights need to not be overly accurate and are estimated in units of 1000 km^2
     * </p>
     * <p>
     * In addition, the number of very large entities tends to swamp smaller, more specific
     * elements, even though the human choice is not strictly tied to area.
     * To accomodate that, the logarithm of the area is used.
     * </p>
     *
     * @param classifier The classuifier to weight
     * @return The base weight (must be at least 1.0)
     * @throws BayesianException if unable to compute the weight
     */
    @Override
    public double weight(Classifier classifier) throws BayesianException {
        Double weight = classifier.get(AlaLinnaeanFactory.weight);
        if (weight != null)
            return weight;
        Double area = classifier.get(AlaLocationFactory.area);
        if (area == null || area < 1) {
            GeographyType type = classifier.getOrDefault(AlaLocationFactory.geographyType, GeographyType.other);
            switch (type) {
                case continent:
                    area = 20000000.0; // Mean continent size
                    break;
                case country:
                    area = 600000.0; // Mean country size
                    break;
                case stateProvince:
                    area = 50000.0; // Estimated state size
                    break;
                case island:
                    area = 1000.0;
                    break;
                case islandGroup:
                    area = 10000.0;
                    break;
                case waterBody:
                    area = 1000000.0; // Average sea area, assume oceans have specific areas
                    break;
                case county:
                    area = 1000.0;
                    break;
                case municipality:
                    area = 100.0;
                    break;
                default:
                    area = 1.0;
            }
        }
        return Math.max(Math.log(area / 1000.0) + 1.0, 1.0);
    }

    /**
     * Modify the base weight in the classifier.
     *
     * @param classifier The classuifier
     * @param weight     The base weight
     * @return The modified weight (must be at least 1.0)
     * @throws BayesianException if unable to compute the weight
     */
    @Override
    public double modify(Classifier classifier, double weight) throws BayesianException {
        return Math.max(1.0, weight);
    }

    /**
     * Annotate a classifier with additional information.
     *
     * @param classifier The classifier
     */
    @Override
    public void annotate(Classifier classifier) {
    }
}
