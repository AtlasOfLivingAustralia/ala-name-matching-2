package au.org.ala.names;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.StoreException;
import com.google.common.base.Enums;
import org.gbif.nameparser.api.Rank;
import org.gbif.nameparser.util.RankUtils;

/**
 * Analysis based on taxonomic rank.
 */
public class RankAnalysis extends Analysis {
    /**
     * Get the class of object that this analyser handles.
     *
     * @return The class of analyser object
     */
    @Override
    public Class<?> getType() {
        return Rank.class;
    }

    /**
     * Analyse this object, providing any special interpretation
     * required.
     *
     * @param value The value to analyse
     * @return The analysed value.
     * @throws InferenceException if unable to analyse the value
     */
    @Override
    public <C> C analyse(C value) throws InferenceException {
        if (value == null)
            return null;
        if (!(value instanceof Rank))
            throw new InferenceException("Expecting Rank, got " + value);
        Rank rank = (Rank) value;
        return (C) (rank.isInfraspecific() ? Rank.INFRASPECIFIC_NAME : rank);
    }

    /**
     * Convert this object into a string for storage
     *
     * @param value The value to convert
     * @return The stringified value (null should return null)
     * @throws StoreException if unable to convert to a string
     */
    @Override
    public String toString(Object value) throws StoreException {
        if (value == null)
            return null;
        if (!(value instanceof Rank))
            throw new StoreException("Expecting Rank, got " + value);
        return ((Rank) value).name().toLowerCase();
    }

    /**
     * Parse this value and return a suitably interpreted object.
     *
     * @param value The value
     * @return The parsed value
     * @throws StoreException if unable to interpret the string
     */
    @Override
    public Object fromString(String value) throws StoreException {
        if (value == null || value.isEmpty())
            return null;
        Rank rank = Enums.getIfPresent(Rank.class, value.toUpperCase()).orNull();
        if (rank == null)
            rank = RankUtils.inferRank(value);
        return rank;
    }

    /**
     * Test for equivalence.
     * <p>
     * At the moment, two ranks being equal is equivalent.
     * On the future, allow a little more slop to accomodate differences of
     * opinion about ranks.
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     *
     * @return Null if not comparable, true if equivalent, false otherwise.
     * @throws InferenceException if unable to determine equivalence
     */
    @Override
    public Boolean equivalent(Object value1, Object value2) throws InferenceException {
        if (value1 == null || value2 == null)
            return null;
        Rank rank1 = (Rank) value1;
        Rank rank2 = (Rank) value2;
        return rank1.equals(rank2);
    }
}
