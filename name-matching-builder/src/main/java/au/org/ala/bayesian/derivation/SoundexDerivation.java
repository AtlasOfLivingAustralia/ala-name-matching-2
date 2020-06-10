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

    @Override
    public Collection<Variable> getBuilderVariables() {
        return Arrays.asList(new Variable(TaxonNameSoundEx.class, INSTANCE_VAR));
    }

    @Override
    public String getTransform(String var, String documentVar) {
        return "this." + INSTANCE_VAR + ".soundEx(" + var + ")";
    }

}
