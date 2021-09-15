package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Derivation;
import au.org.ala.bayesian.Observable;
import org.apache.commons.codec.language.Soundex;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * A simple soundex derivation.
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

    /**
     * Does this have a transform?
     *
     * @return True
     */
    @Override
    public boolean hasTransform() {
        return true;
    }


    @Override
    public Collection<Derivation.Variable> getBuilderVariables() {
        return Arrays.asList(new Derivation.Variable(SoundexGenerator.class, INSTANCE_VAR));
    }

    @Override
    public Collection<Derivation.Variable> getClassificationVariables() {
        return Arrays.asList(new Derivation.Variable(SoundexGenerator.class, INSTANCE_VAR));
    }

    @Override
    public String generateBuilderTransform(String var, String extra, String documentVar) {
        return "this." + INSTANCE_VAR + ".soundex((String) " + var + ")";
    }

    @Override
    public String generateClassificationTransform() {
        return this.getSources().stream().map(s -> "this." + INSTANCE_VAR + ".soundex(this." + s.getJavaVariable() + ")").collect(Collectors.joining(" + "));
    }

}
