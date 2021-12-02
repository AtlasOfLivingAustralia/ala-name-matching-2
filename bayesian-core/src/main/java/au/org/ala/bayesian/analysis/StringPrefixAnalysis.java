package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Analysis;
import au.org.ala.bayesian.InferenceException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * Analyse against the beginning of a string
 */
public class StringPrefixAnalysis extends StringAnalysis {
    /** The minimum number of characters to match before accepting a prefix */
    @JsonProperty
    @Getter
    @Setter
    private int minimum = 4;

    /**
     * Construct with a minimum length
     *
     * @param minimum The minimum length
     */
    public StringPrefixAnalysis(int minimum) {
        this.minimum = minimum;
    }


    /**
     * Construct with a default minimun length of 4.
     */
    public StringPrefixAnalysis() {
        this(4);
    }

    /**
     * Generate a hash code for this analysis object.
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.minimum;
    }

    /**
     * Equality test for this analysis object.
     *
     * @param obj The object to test against
     * @return True if these are the same analyser.
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && ((StringPrefixAnalysis) obj).minimum == this.minimum;
    }

    /**
     * Test for equivalence.
     * <p>
     * If either value1 or value2 is null, the result is null.
     * Otherwise, case-insensitive starts with is used.
     * If either string is the prefix of the other, they are equivalent,
     * provided that either both strings are less than the minimum and the same length or both
     * are larger than the minimum.
     * </p>
     *
     * @param value1 The first value to test
     * @param value2 The second value to test
     * @return Null if not comparable, true if equivalent, false otherwise.
     * @throws InferenceException if unable to determine equivalence
     */
    @Override
    public Boolean equivalent(String value1, String value2) throws InferenceException {
        if (value1 == null || value2 == null)
            return null;
        value1 = value1.toLowerCase();
        value2 = value2.toLowerCase();
        if (value1.length() < this.minimum && value2.length() > value1.length())
            return false;
        if (value2.length() < this.minimum && value1.length() > value2.length())
            return false;
        return value1.startsWith(value2) || value2.startsWith(value1);
    }

    /**
     * Get the parameters used to construct this analysis object.
     *
     * @return Return a list with the minimum match value
     */
    @Override
    @JsonIgnore
    public List<Object> getConstructorParameters() {
        List<Object> params = super.getConstructorParameters();
        params.add(this.minimum);
        return params;
    }
}
