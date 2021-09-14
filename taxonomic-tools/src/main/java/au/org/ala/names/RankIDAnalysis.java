package au.org.ala.names;

import au.org.ala.bayesian.StoreException;
import au.org.ala.bayesian.analysis.RangeAnalysis;
import org.apache.commons.lang3.Range;
import org.gbif.nameparser.api.Rank;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.gbif.nameparser.api.Rank.*;

public class RankIDAnalysis extends RangeAnalysis {
    /** The map of ranks onto ranges of index values */
    public static final Map<Rank, Integer> RANK_MAP = new HashMap<>();
    /** The map of ranks onto ranges of index values */
    public static final Map<Rank, Range<Integer>> RANGE_MAP = new HashMap<>();
    /** The map of ranks onto rank identifiers */
    public static final TreeMap<Integer, Rank> RANK_LOOKUP_MAP = new TreeMap<>();

    static {
        RANK_MAP.put(DOMAIN, 100);
        RANK_MAP.put(REALM, 700);
        RANK_MAP.put(SUBREALM, 800);
        RANK_MAP.put(SUPERKINGDOM, 900);
        RANK_MAP.put(KINGDOM, 1000);
        RANK_MAP.put(SUBKINGDOM, 1100);
        RANK_MAP.put(INFRAKINGDOM, 1200);
        RANK_MAP.put(SUPERPHYLUM, 1900);
        RANK_MAP.put(PHYLUM, 2000);
        RANK_MAP.put(SUBPHYLUM, 2100);
        RANK_MAP.put(INFRAPHYLUM, 2200);
        RANK_MAP.put(SUPERCLASS, 2900);
        RANK_MAP.put(CLASS, 3000);
        RANK_MAP.put(SUBCLASS, 3100);
        RANK_MAP.put(INFRACLASS, 3200);
        RANK_MAP.put(SUBTERCLASS, 3220);
        RANK_MAP.put(PARVCLASS, 3240);
        RANK_MAP.put(SUPERDIVISION, 3280);
        RANK_MAP.put(DIVISION, 3300);
        RANK_MAP.put(SUBDIVISION, 3320);
        RANK_MAP.put(INFRADIVISION, 3340);
        RANK_MAP.put(SUPERLEGION, 3380);
        RANK_MAP.put(LEGION, 3400);
        RANK_MAP.put(SUBLEGION, 3420);
        RANK_MAP.put(INFRALEGION, 3440);
        RANK_MAP.put(SUPERCOHORT, 3480);
        RANK_MAP.put(COHORT, 3500);
        RANK_MAP.put(SUBCOHORT, 3520);
        RANK_MAP.put(INFRACOHORT, 3540);
        RANK_MAP.put(GIGAORDER, 3800);
        RANK_MAP.put(MAGNORDER, 3820);
        RANK_MAP.put(GRANDORDER, 3840);
        RANK_MAP.put(MIRORDER, 3860);
        RANK_MAP.put(SUPERORDER, 3900);
        RANK_MAP.put(ORDER, 4000);
        RANK_MAP.put(NANORDER, 4020);
        RANK_MAP.put(HYPOORDER, 4040);
        RANK_MAP.put(MINORDER, 4060);
        RANK_MAP.put(SUBORDER, 4100);
        RANK_MAP.put(INFRAORDER, 4200);
        RANK_MAP.put(PARVORDER, 4300);
        RANK_MAP.put(MEGAFAMILY, 4800);
        RANK_MAP.put(GRANDFAMILY, 4820);
        RANK_MAP.put(SUPERFAMILY, 4900);
        RANK_MAP.put(EPIFAMILY, 4920);
        RANK_MAP.put(FAMILY, 5000);
        RANK_MAP.put(SUBFAMILY, 5100);
        RANK_MAP.put(INFRAFAMILY, 5200);
        RANK_MAP.put(SUPERTRIBE, 5280);
        RANK_MAP.put(TRIBE, 5300);
        RANK_MAP.put(SUBTRIBE, 5320);
        RANK_MAP.put(INFRATRIBE, 5340);
        RANK_MAP.put(SUPRAGENERIC_NAME, 5900);
        RANK_MAP.put(GENUS, 6000);
        RANK_MAP.put(SUBGENUS, 6100);
        RANK_MAP.put(INFRAGENUS, 6200);
        RANK_MAP.put(SUPERSECTION, 6280);
        RANK_MAP.put(SECTION, 6300);
        RANK_MAP.put(SUBSECTION, 6320);
        RANK_MAP.put(SUPERSERIES, 6380);
        RANK_MAP.put(SERIES, 6400);
        RANK_MAP.put(SUBSERIES, 6420);
        RANK_MAP.put(INFRAGENERIC_NAME, 6500);
        RANK_MAP.put(SPECIES_AGGREGATE, 6600);
        RANK_MAP.put(SPECIES, 7000);
        RANK_MAP.put(INFRASPECIFIC_NAME, 7100);
        RANK_MAP.put(GREX, 7200);
        RANK_MAP.put(SUBSPECIES, 8000);
        RANK_MAP.put(CULTIVAR_GROUP, 8100);
        RANK_MAP.put(CONVARIETY, 8110);
        RANK_MAP.put(INFRASUBSPECIFIC_NAME, 8200);
        RANK_MAP.put(PROLES, 8220);
        RANK_MAP.put(NATIO, 8240);
        RANK_MAP.put(ABERRATION, 8260);
        RANK_MAP.put(MORPH, 8280);
        RANK_MAP.put(VARIETY, 8300);
        RANK_MAP.put(SUBVARIETY, 8320);
        RANK_MAP.put(FORM, 8400);
        RANK_MAP.put(SUBFORM, 8420);
        RANK_MAP.put(PATHOVAR, 8500);
        RANK_MAP.put(BIOVAR, 8510);
        RANK_MAP.put(CHEMOVAR, 8520);
        RANK_MAP.put(MORPHOVAR, 8530);
        RANK_MAP.put(PHAGOVAR, 8540);
        RANK_MAP.put(SEROVAR, 8550);
        RANK_MAP.put(CHEMOFORM, 8560);
        RANK_MAP.put(FORMA_SPECIALIS, 8580);
        RANK_MAP.put(CULTIVAR, 8600);
        RANK_MAP.put(STRAIN, 8700);
        RANK_MAP.put(OTHER, 0);
        RANK_MAP.put(UNRANKED, -1);

        RANGE_MAP.put(DOMAIN, Range.between(1, 1999));
        RANGE_MAP.put(REALM, Range.between(1, 1999));
        RANGE_MAP.put(SUBREALM, Range.between(1, 1999));
        RANGE_MAP.put(SUPERKINGDOM, Range.between(1, 1999));
        RANGE_MAP.put(KINGDOM, Range.between(1, 1999));
        RANGE_MAP.put(SUBKINGDOM, Range.between(1, 1999));
        RANGE_MAP.put(INFRAKINGDOM, Range.between(1000, 2999));
        RANGE_MAP.put(SUPERPHYLUM, Range.between(1001, 2999));
        RANGE_MAP.put(PHYLUM, Range.between(1001, 2999));
        RANGE_MAP.put(SUBPHYLUM, Range.between(1001, 2999));
        RANGE_MAP.put(INFRAPHYLUM, Range.between(1001, 3999));
        RANGE_MAP.put(SUPERCLASS, Range.between(2001, 3999));
        RANGE_MAP.put(CLASS, Range.between(2001, 3999));
        RANGE_MAP.put(SUBCLASS, Range.between(2001, 3999));
        RANGE_MAP.put(INFRACLASS, Range.between(2001, 4999));
        RANGE_MAP.put(SUBTERCLASS, Range.between(2001, 4999));
        RANGE_MAP.put(PARVCLASS, Range.between(2001, 4999));
        RANGE_MAP.put(SUPERDIVISION, Range.between(2001, 4999));
        RANGE_MAP.put(DIVISION, Range.between(2001, 4999));
        RANGE_MAP.put(SUBDIVISION, Range.between(2001, 4999));
        RANGE_MAP.put(INFRADIVISION, Range.between(2001, 4999));
        RANGE_MAP.put(SUPERLEGION, Range.between(2001, 4999));
        RANGE_MAP.put(LEGION, Range.between(2001, 4999));
        RANGE_MAP.put(SUBLEGION, Range.between(2001, 4999));
        RANGE_MAP.put(INFRALEGION, Range.between(2001, 4999));
        RANGE_MAP.put(SUPERCOHORT, Range.between(2001, 4999));
        RANGE_MAP.put(COHORT, Range.between(2001, 4999));
        RANGE_MAP.put(SUBCOHORT, Range.between(2001, 4999));
        RANGE_MAP.put(INFRACOHORT, Range.between(2001, 4999));
        RANGE_MAP.put(GIGAORDER, Range.between(3001, 4999));
        RANGE_MAP.put(MAGNORDER, Range.between(3001, 4999));
        RANGE_MAP.put(GRANDORDER, Range.between(3001, 4999));
        RANGE_MAP.put(MIRORDER, Range.between(3001, 4999));
        RANGE_MAP.put(SUPERORDER, Range.between(3001, 4999));
        RANGE_MAP.put(ORDER, Range.between(3001, 4999));
        RANGE_MAP.put(NANORDER, Range.between(3001, 4999));
        RANGE_MAP.put(HYPOORDER, Range.between(3001, 4999));
        RANGE_MAP.put(MINORDER, Range.between(3001, 4999));
        RANGE_MAP.put(SUBORDER, Range.between(3001, 4999));
        RANGE_MAP.put(INFRAORDER, Range.between(4001, 5999));
        RANGE_MAP.put(PARVORDER, Range.between(4001, 5999));
        RANGE_MAP.put(MEGAFAMILY, Range.between(4001, 5999));
        RANGE_MAP.put(GRANDFAMILY, Range.between(4001, 5999));
        RANGE_MAP.put(SUPERFAMILY, Range.between(4001, 5999));
        RANGE_MAP.put(EPIFAMILY, Range.between(4001, 5999));
        RANGE_MAP.put(FAMILY, Range.between(4001, 5999));
        RANGE_MAP.put(SUBFAMILY, Range.between(4001, 5999));
        RANGE_MAP.put(INFRAFAMILY, Range.between(5001, 6999));
        RANGE_MAP.put(SUPERTRIBE, Range.between(5001, 6999));
        RANGE_MAP.put(TRIBE, Range.between(5001, 6999));
        RANGE_MAP.put(SUBTRIBE, Range.between(5001, 6999));
        RANGE_MAP.put(INFRATRIBE, Range.between(5001, 6999));
        RANGE_MAP.put(SUPRAGENERIC_NAME, Range.between(5001, 6999));
        RANGE_MAP.put(GENUS, Range.between(5001, 6999));
        RANGE_MAP.put(SUBGENUS, Range.between(5001, 6999));
        RANGE_MAP.put(INFRAGENUS, Range.between(6001, 6999));
        RANGE_MAP.put(SUPERSECTION, Range.between(6001, 6999));
        RANGE_MAP.put(SECTION, Range.between(6001, 6999));
        RANGE_MAP.put(SUBSECTION, Range.between(6001, 6999));
        RANGE_MAP.put(SUPERSERIES, Range.between(6001, 6999));
        RANGE_MAP.put(SERIES, Range.between(6001, 6999));
        RANGE_MAP.put(SUBSERIES, Range.between(6001, 6999));
        RANGE_MAP.put(INFRAGENERIC_NAME, Range.between(6001, 6999));
        RANGE_MAP.put(SPECIES_AGGREGATE, Range.between(6001, 6999));
        RANGE_MAP.put(SPECIES, Range.between(6900, 7100));
        RANGE_MAP.put(INFRASPECIFIC_NAME, Range.between(7001, 10000));
        RANGE_MAP.put(GREX, Range.between(7001, 10000));
        RANGE_MAP.put(SUBSPECIES, Range.between(7001, 10000));
        RANGE_MAP.put(CULTIVAR_GROUP, Range.between(7001, 10000));
        RANGE_MAP.put(CONVARIETY, Range.between(7001, 10000));
        RANGE_MAP.put(INFRASUBSPECIFIC_NAME, Range.between(7001, 10000));
        RANGE_MAP.put(PROLES, Range.between(7001, 10000));
        RANGE_MAP.put(NATIO, Range.between(7001, 10000));
        RANGE_MAP.put(ABERRATION, Range.between(7001, 10000));
        RANGE_MAP.put(MORPH, Range.between(7001, 10000));
        RANGE_MAP.put(VARIETY, Range.between(7001, 10000));
        RANGE_MAP.put(SUBVARIETY, Range.between(7001, 10000));
        RANGE_MAP.put(FORM, Range.between(7001, 10000));
        RANGE_MAP.put(SUBFORM, Range.between(7001, 10000));
        RANGE_MAP.put(PATHOVAR, Range.between(7001, 10000));
        RANGE_MAP.put(BIOVAR, Range.between(7001, 10000));
        RANGE_MAP.put(CHEMOVAR, Range.between(7001, 10000));
        RANGE_MAP.put(MORPHOVAR, Range.between(7001, 10000));
        RANGE_MAP.put(PHAGOVAR, Range.between(7001, 10000));
        RANGE_MAP.put(SEROVAR, Range.between(7001, 10000));
        RANGE_MAP.put(CHEMOFORM, Range.between(7001, 10000));
        RANGE_MAP.put(FORMA_SPECIALIS, Range.between(7001, 10000));
        RANGE_MAP.put(CULTIVAR, Range.between(7001, 10000));
        RANGE_MAP.put(STRAIN, Range.between(7001, 10000));
        RANGE_MAP.put(OTHER, Range.between(-10000, 10000));
        RANGE_MAP.put(UNRANKED, Range.between(-10000, 10000));

        RANK_LOOKUP_MAP.putAll(RANK_MAP.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getValue,
                Map.Entry::getKey
        )));
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
}
