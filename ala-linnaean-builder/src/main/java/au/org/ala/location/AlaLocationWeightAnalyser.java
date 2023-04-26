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
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.nameparser.api.Rank;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ALA-specific weight analyser.
 * <p>
 * This contains several parts.
 * A default weight based on the score allocated by the merging process (or 1 by default).
 * A lookup weight based on tables of data in a DwCA.
 * </p>
 * <p>
 * If <code>weightAnalyser.areasOfInterest</code> is passed to the index builder CLI
 * (set via <code>-p weightAnalyser.areaOfInterest=URL</code> then a CSV file from that location,
 * with locationIO,WKT,boost as the first three columns is read.
 * This will apply the first matching boost for a location with the same locationID somewhere
 * (ie. in islandID or countryID as well as locationID)
 * or a decumalLatitude/Longitude within the WKT.
 * </p>
 */
public class AlaLocationWeightAnalyser implements WeightAnalyser, Annotator {
    private static final Logger logger = LoggerFactory.getLogger(AlaLocationWeightAnalyser.class);

    // WGS84 SRID
    private static final int SRID = 4326;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), SRID);
    /**
     * Optional area of interest
     */
    private List<AreaOfInterest> areasOfInterest;

    /**
     * Construct a weight analyser for a configuration
     *
     * @param configuration The configuration
     * @throws Exception If unable to build the analyser
     */
    public AlaLocationWeightAnalyser(IndexBuilderConfiguration configuration) throws Exception {
        this.areasOfInterest = new ArrayList<>();
        String aoi = configuration.getParameters().get("weightAnalyser.areasOfInterest");
        if (aoi != null) {
            CSVReader reader = new CSVReader(new InputStreamReader(new URL(aoi).openStream()));
            WKTReader wktRreader = new WKTReader(GEOMETRY_FACTORY);
            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                String locationID = StringUtils.stripToNull(line[0]);
                String wkt = StringUtils.stripToNull(line[1]);
                Geometry geometry = wkt == null ? null : wktRreader.read(wkt);
                double boost = Double.parseDouble(line[2]);
                this.areasOfInterest.add(new AreaOfInterest(locationID, geometry, boost));
            }
        }
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
        Double lat = classifier.get(AlaLocationFactory.decimalLatitude);
        Double lon = classifier.get(AlaLocationFactory.decimalLongitude);
        Point point = null;
        if (lat != null && lon != null) {
            point = GEOMETRY_FACTORY.createPoint(new Coordinate(lon, lat));
        }
        for (AreaOfInterest aoi: this.areasOfInterest) {
            if (aoi.match(classifier, point)) {
                weight *= aoi.getBoost();
                break;
            }
        }
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

    @Value
    class AreaOfInterest {
        private String locationID;
        private Geometry geometry;
        private double boost;

        /**
         * Check to see if the classifier matches the area of interest
         *
         * @param classifier The classifier
         * @param point The location point (if present)
         *
         * @return True if inside the area of interest
         */
        public boolean match(Classifier classifier, Point point) {
            if (this.locationID != null) {
                if (this.locationID.equals(classifier.get(AlaLocationFactory.locationId)))
                    return true;
                if (this.locationID.equals(classifier.get(AlaLocationFactory.islandId)))
                    return true;
                if (this.locationID.equals(classifier.get(AlaLocationFactory.islandGroupId)))
                    return true;
                if (this.locationID.equals(classifier.get(AlaLocationFactory.stateProvinceId)))
                    return true;
                if (this.locationID.equals(classifier.get(AlaLocationFactory.countryId)))
                    return true;
                if (this.locationID.equals(classifier.get(AlaLocationFactory.continentId)))
                    return true;
                if (this.locationID.equals(classifier.get(AlaLocationFactory.waterBodyId)))
                    return true;
            }
            if (this.geometry != null && point != null) {
                if (this.geometry.contains(point))
                    return true;
            }
            return false;
        }
    }
}
