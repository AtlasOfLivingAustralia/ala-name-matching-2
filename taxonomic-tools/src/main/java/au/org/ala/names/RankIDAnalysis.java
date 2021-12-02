package au.org.ala.names;

import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.analysis.RangeAnalysis;
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
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.gbif.nameparser.api.Rank.*;

public class RankIDAnalysis extends RangeAnalysis {
    private static final Logger logger = LoggerFactory.getLogger(RankIDAnalysis.class);

    /** The map of ranks onto ranges of index values */
    public static final Map<Rank, Integer> RANK_MAP = Collections.unmodifiableMap(buildInitialRankIDMap());
    /** The map of ranks onto ranges of index values */
    public static final Map<Rank, Range<Integer>> RANGE_MAP = Collections.unmodifiableMap(buildInitialRankRangeMap());
    /** The map of ranks onto rank identifiers */
    public static final TreeMap<Integer, Rank> RANK_LOOKUP_MAP = new TreeMap<>(
            RANK_MAP.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getValue,
                    Map.Entry::getKey
            ))
    );

    // Initialise rank IDs
    private static Map<Rank, Integer> buildInitialRankIDMap() {
        Map<Rank, Integer> rankMap = new HashMap<>();
        try {
            CSVReader reader = CSVReaderFactory.build(RankAnalysis.class.getResourceAsStream("ranks.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String val;
                String row[] = reader.next();
                if (row.length < 2)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                val = row[0].trim().toUpperCase();
                Rank rank = Rank.valueOf(val);
                rankMap.put(rank, Integer.parseInt(row[1]));
                val = StringUtils.trimToNull(row[4]);
            }
        } catch (IOException ex) {
            logger.error("Unable to read rank map", ex);
        }
        logger.info("Initialised rank id dictionary with " + rankMap.size() + " entries");
        return rankMap;
    }

    // Initialise range IDs
    private static Map<Rank, Range<Integer>> buildInitialRankRangeMap() {
        Map<Rank, Range<Integer>> rankMap = new HashMap<>();
        try {
            CSVReader reader = CSVReaderFactory.build(RankAnalysis.class.getResourceAsStream("ranks.csv"), "UTF-8", ",", '"', 1);
            while (reader.hasNext()) {
                String val;
                String row[] = reader.next();
                if (row.length < 4)
                    continue;
                if (row[0].startsWith("#"))
                    continue;
                val = row[0].trim().toUpperCase();
                Rank rank = Rank.valueOf(val);
                rankMap.put(rank, Range.between(Integer.parseInt(row[2]), Integer.parseInt(row[3])));
            }
        } catch (IOException ex) {
            logger.error("Unable to read rank map", ex);
        }
        logger.info("Initialised rank range dictionary with " + rankMap.size() + " entries");
        return rankMap;
    }


    /** Helper for dealing with ranks as strings */
    private final RankAnalysis rankAnalysis = new RankAnalysis();


    /**
     * Parse this value from a string and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public Integer fromString(String value) throws StoreException {
        if (value == null)
            return null;
        try {
            int id = Integer.parseInt(value);
            return this.fromStore(id);
        } catch (NumberFormatException ex) {
            Rank rank = this.rankAnalysis.fromString(value);
            if (rank == null)
                return null;
            Integer id = RANK_MAP.get(rank);
            if (id == null)
                throw new StoreException("Unable to find rank id for " + value + " and rank " + rank);
            return id;
        }
    }

    /**
     * Convert this object into a value for query
     *
     * @param value The value to convert
     * @return The converted value (null should return null)
     */
    @Override
    public Range<Integer> toQuery(Integer value) {
        if (value == null)
            return null;
        Rank rank = rankFromID(value);
        return RANGE_MAP.get(rank);
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
    public Boolean equivalent(Integer value1, Integer value2) {
        if (value1 == null || value2 == null)
            return null;
        if (value1 == value2)
            return true;
        if (value1 <= 0 || value2 <= 0)
            return null;
        Rank rank1 = rankFromID(value1);
        Rank rank2 = rankFromID(value2);
        Range<Integer> range1 = RankIDAnalysis.RANGE_MAP.get(rank1);
        Range<Integer> range2 = RankIDAnalysis.RANGE_MAP.get(rank2);
        if (range1 != null && range1.contains(value2))
            return true;
        if (range2 != null && range2.contains(value1))
            return true;
        return false;
    }


    /**
     * Find a matching rank from an identfier.
     *
     * @param id The igentifier
     *
     * @return The closest rank to that identifier
     */
    public static Rank rankFromID(int id) {
        Map.Entry<Integer, Rank> lower = RANK_LOOKUP_MAP.floorEntry(id);
        Map.Entry<Integer, Rank> upper = RANK_LOOKUP_MAP.ceilingEntry(id);
        if (lower == null) {
            return upper == null ? null : upper.getValue();
        }
        if (upper == null)
            return lower.getValue();
        int dl = id - lower.getKey();
        int du = upper.getKey() - id;
        return dl > du ? upper.getValue() : lower.getValue();
    }


    /**
     * Find a matching identifier from a rank.
     *
     * @param rank The rank
     *
     * @return The closest rank to that identifier
     */
    public static Integer idFromRank(Rank rank) {
        if (rank == null)
            return null;
        return RANK_MAP.get(rank);
    }

}
