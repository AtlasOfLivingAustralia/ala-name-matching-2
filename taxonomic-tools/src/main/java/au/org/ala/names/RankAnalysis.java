package au.org.ala.names;

import au.org.ala.bayesian.analysis.EnumAnalysis;
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
     * Analyse this object, providing any special interpretation
     * required.
     *
     * @param value The value to analyse
     * @return The analysed value.
     */
    @Override
    public Rank analyse(Rank value) {
        if (value == null)
            return null;
        return value.isInfraspecific() ? Rank.INFRASPECIFIC_NAME : value;
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
     * Generally equality.
     * Incomparable ranks return null.
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
        return false;
    }
}
