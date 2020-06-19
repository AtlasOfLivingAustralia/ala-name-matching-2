package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Derivation;
import au.org.ala.bayesian.Observable;
import au.org.ala.util.TaxonNameSoundEx;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collection;

/**
 * Derive a new name based on the taxamatch soundex algorithm.
 */
public class SoundexDerivation extends CopyDerivation {
    /** The name of the instance variable for the soundex object */
    public static final String INSTANCE_VAR = "soundex";

    /** The rank observable for context */
    @JsonProperty
    private Observable rank;

    /**
     * Construct a new derivation.
     */
    public SoundexDerivation() {
    }

    /**
     * Construct a derivation from a source.
     *
     * @param source The source
     */
    public SoundexDerivation(Observable source) {
        super(source);
    }

    /**
     * Get any extra observable needed to compute things.
     *
     * @return The extra variable, or null for none (defaults to null)
     */
    @Override
    public Observable getExtra() {
        return this.rank;
    }

    /**
     * Generate the piece of code that represents getting the extra rank information
     *
     * @param documentVar The document variable
     * @return The ancillary variable generator
     */
    @Override
    public String getExtra(String documentVar) {
        return documentVar + ".get(\"" + this.rank.getField() + "\")";
    }

    @Override
    public Collection<Variable> getBuilderVariables() {
        return Arrays.asList(new Variable(TaxonNameSoundEx.class, INSTANCE_VAR));
    }

    @Override
    public String getTransform(String var, String extra, String documentVar) {
        return "this." + INSTANCE_VAR + ".treatWord(" + var + ", " + extra + ")";
    }

}
