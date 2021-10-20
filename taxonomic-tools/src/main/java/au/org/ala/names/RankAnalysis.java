package au.org.ala.names;

import au.org.ala.bayesian.analysis.EnumAnalysis;
import org.apache.commons.lang3.Range;
import org.gbif.nameparser.api.Rank;
import org.gbif.nameparser.util.RankUtils;

/**
 * Analysis based on taxonomic rank.
 */
public class RankAnalysis extends EnumAnalysis<Rank> {
    public RankAnalysis() {
        super(Rank.class);
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
        Rank rank = super.fromString(value);
        if (rank == null)
            rank = RankUtils.inferRank(value);
        return rank;
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
        if (range2 != null && range2.contains(id1))
            return true;
        return false;
    }
}
