package au.org.ala.names;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.derivation.CopyDerivation;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Derive a new name based on the author canonicaliser.
 */
public class AuthorCanonicaliserDerivation extends CopyDerivation {
    /** The name of the instance variable for the soundex object */
    public static final String INSTANCE_VAR = "authorCanonicaliser";

    /**
     * Construct a new derivation.
     */
    public AuthorCanonicaliserDerivation() {
    }

    /**
     * Construct a derivation from a source.
     *
     * @param condition Any associated condition
     * @param source The source
     */
    public AuthorCanonicaliserDerivation(Condition condition, Observable source) {
        super(condition, source);
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
    public Collection<Variable> getBuilderVariables() {
        return Arrays.asList(new Variable(AuthorCanonicaliser.class, INSTANCE_VAR));
    }

    @Override
    public Collection<Variable> getClassificationVariables() {
        return Arrays.asList(new Variable(AuthorCanonicaliser.class, INSTANCE_VAR));
    }

    @Override
    public String generateBuilderTransform(String var, String extra, String documentVar) {
        return "this." + INSTANCE_VAR + ".canonicalise((String) " + var + ")";
    }

    @Override
    public String generateClassificationTransform() {
        return this.getSources().stream().map(s -> "this." + INSTANCE_VAR + ".canonicalise(this." + s.getJavaVariable() + ")").collect(Collectors.joining(" + "));
    }

}
