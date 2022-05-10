package au.org.ala.names;

import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.analysis.EnumAnalysis;
import au.org.ala.bayesian.fidelity.SimpleFidelity;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.gbif.nameparser.api.Rank;
import org.gbif.utils.file.csv.CSVReader;
import org.gbif.utils.file.csv.CSVReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Analysis based on taxonomic rank.
 */
public class RankAnalysis extends EnumAnalysis<Rank> {
    private static final Logger logger = LoggerFactory.getLogger(RankAnalysis.class);

    private static final Map<String, Rank> RANK_MAP = Collections.unmodifiableMap(buildInitialRankMap());

    // Add a rank name
    private static void addRank(String key, Rank value, Map<String, Rank> map) {
        key = StringUtils.trimToNull(key);
        if (key == null)
            return;
        key = key.toUpperCase();
        Rank existing = map.get(key);
        if (existing != null) {
            if (existing != value) {
                logger.info("Rank map already contains " + key + " for " + existing + ", ingnoring mapping to " + value);
            }
            return;
        }
        map.put(key, value);
    }

    // Initialise the rank names
    private static Map<String, Rank> buildInitialRankMap() {
        Map<String, Rank> rankMap = new HashMap<>();
        try {
            CSVReader reader = CSVReaderFactory.build(RankAnalysis.class.getResourceAsStream("ranks.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String val;
                String[] row = reader.next();
                if (row.length < 6)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                val = row[0].trim().toUpperCase();
                Rank rank = Rank.valueOf(val);
                addRank(val, rank, rankMap);
                val = StringUtils.trimToNull(row[4]);
                addRank(val, rank, rankMap);
                if (val != null && val.endsWith(".")) {
                    addRank(val.substring(0, val.length() - 1), rank, rankMap);
                }
                for (int i = 6; i < row.length; i++)
                    addRank(row[i], rank, rankMap);
            }
        } catch (IOException ex) {
            logger.error("Unable to read rank map", ex);
        }
        logger.info("Initialised rank dictionary with " + rankMap.size() + " entries");
        return rankMap;
    }

    public RankAnalysis() {
        super(Rank.class);
    }

    /**
     * Turn unusable ranks into null.
     *
     * @param value The rank
     *
     * @return The tidied rank
     */
    private Rank tidy(Rank value) {
        return value == Rank.OTHER || value == Rank.UNRANKED ? null : value;
    }

    /**
     * Tidy up unusable ranks.
     *
     * @param value The value to analyse
     *
     * @return The tidied value
     */
    @Override
    public Rank analyse(Rank value) throws InferenceException {
        return super.analyse(this.tidy(value));
    }

    /**
     * Tidy up unusable ranks.
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     */
    @Override
    public String toStore(Rank value) {
        return super.toStore(this.tidy(value));
    }

    /**
     * Tidy up unusable ranks.
     *
     * @param value The value to convert
     * @return The converted value
     */
    @Override
    public String toQuery(Rank value) {
        return super.toQuery(this.tidy(value));
    }

    /**
     * Compute a fidelity measure for this type of object.
     * <p>
     * We accept ranks within a linnaean range as more or less equivalent
     * </p>
     *
     * @param original The original value
     * @param actual   The actual value
     * @return The computed fidelity
     */
    @Override
    public Fidelity<Rank> buildFidelity(Rank original, Rank actual) throws InferenceException {
        if (original == null)
            return null;
        double fidelity = 0.0;
        if (actual != null) {
            int oid = RankIDAnalysis.idFromRank(original);
            int aid = RankIDAnalysis.idFromRank(actual);
            double scale = oid > 7000 && aid > 7000 ? 4000.0 : 1000.0;
            fidelity = 1.0 - Math.min(1.0, Math.abs(oid - aid) / scale);
        }
        return new SimpleFidelity<>(original, actual, fidelity);
    }

    /**
     * Parse this value and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     */
    @Override
    public Rank fromString(String value) {
        if (value == null || value.isEmpty())
            return null;
        Rank rank = RANK_MAP.get(value.trim().toUpperCase());
        return this.tidy(rank);
    }

    /**
     * Test for equivalence.
     * <p>
     * Incomparable ranks return null.
     * Otherwise, equivalence is decided on whether the value of one rank overlaps
     * the range of the other.
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     * @return Null if not comparable, true if equivalent, false otherwise.
     */
    @Override
    public Boolean equivalent(Rank value1, Rank value2) {
        if (value1 == null || value2 == null )
            return null;
        if (value1 == value2)
            return true;
        if (value1.isUncomparable() || value2.isUncomparable())
            return null;
        int id1 = RankIDAnalysis.RANK_MAP.getOrDefault(value1, -1);
        int id2 = RankIDAnalysis.RANK_MAP.getOrDefault(value2, -1);
        Range<Integer> range1 = RankIDAnalysis.RANGE_MAP.get(value1);
        Range<Integer> range2 = RankIDAnalysis.RANGE_MAP.get(value2);
        if (range1 != null && range1.contains(id2))
            return true;
        return range2 != null && range2.contains(id1);
    }

    /**
     * Jackson serializer for ranks
     */
    public static class Serializer extends StdSerializer<Rank> {
        private static final RankAnalysis ANALYSIS = new RankAnalysis();

        public Serializer() {
            super(Rank.class);
        }

        @Override
        public void serialize(Rank rank, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(ANALYSIS.toStore(rank));
        }
    }


    /**
     * Jackson deserializer for ranks
     */
    public static class Deserializer extends StdDeserializer<Rank> {
        private static final RankAnalysis ANALYSIS = new RankAnalysis();

        public Deserializer() {
            super(Rank.class);
        }

        @Override
        public Rank deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String value = jsonParser.getText();

            if (value == null || value.isEmpty())
                return null;
            Rank rank = ANALYSIS.fromString(value);
            if (rank == null)
                throw new JsonParseException(jsonParser, "Invalid rank " + value);
            return rank;
        }
    }

}
