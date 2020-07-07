package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Derivation;
import au.org.ala.bayesian.Observable;
import au.org.ala.util.TaxonNameSoundEx;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

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
    @Getter
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

    @Override
    public Collection<Variable> getBuilderVariables() {
        return Arrays.asList(new Variable(TaxonNameSoundEx.class, INSTANCE_VAR));
    }

    @Override
    public Collection<Variable> getClassificationVariables() {
        return Arrays.asList(new Variable(TaxonNameSoundEx.class, INSTANCE_VAR));
    }

    @Override
    public String generateBuilderTransform(String var, String extra, String documentVar) {
        if (this.rank == null)
            extra = "\"species\"";
        return "this." + INSTANCE_VAR + ".treatWord(" + var + ", " + extra + ")";
    }

    @Override
    public String generateClassificationTransform() {
        String extra = this.rank == null ? "\"species\"" : "this." + this.rank.getJavaVariable();
        return "this." + INSTANCE_VAR + ".treatWord(this." + this.getSource().getJavaVariable() + ", " + extra + ")";
    }

}
