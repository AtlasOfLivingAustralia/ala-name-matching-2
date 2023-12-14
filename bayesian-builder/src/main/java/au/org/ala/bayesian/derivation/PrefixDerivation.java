package au.org.ala.bayesian.derivation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generate multiple prefixes of a source term.
 */
public class PrefixDerivation extends CopyDerivation {
    private static final String INSTANCE_VAR = "prefixer";

    /** The minimum length for prefixes */
    @JsonProperty
    private int min;
    /** The maximum length for prefixes */
    @JsonProperty
    private int max;

    /**
     * This generates multiple values for a single value
     *
     * @return True
     */
    @Override
    public boolean isMultiValued() {
        return true;
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
        return Collections.singletonList(new Variable(PrefixGenerator.class, INSTANCE_VAR));
    }

    @Override
    public String generateBuilderTransform(String var, String extra, String documentVar) {
        return "this." + INSTANCE_VAR + ".generate((String) " + var + ", " + this.min + ", " + this.max + ")";
    }

    @Override
    public String generateClassificationTransform() {
        return this.getSources().stream().map(s -> "this." + s.getJavaVariable()).collect(Collectors.joining(" + "));
    }


    public static class PrefixGenerator {

        public PrefixGenerator() {
        }

        public Set<String> generate(String original, int min, int max) {
            Set<String> values = new HashSet<>();
            if (original == null)
                return values;
            for (int l = min; l <= max; l ++) {
                if (l < original.length())
                    values.add(original.substring(0, l + 1));
            }
            return values;
        }
    }
}
