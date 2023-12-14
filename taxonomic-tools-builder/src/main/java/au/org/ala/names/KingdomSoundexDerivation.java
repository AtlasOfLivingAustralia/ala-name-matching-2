package au.org.ala.names;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.derivation.CopyDerivation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.gbif.nameparser.api.NameType;
import org.gbif.nameparser.api.Rank;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Derive kingdom names based on the taxamatch soundex algorithm.
 * <p>
 * Kingdoms can have problems.
 * For some things like protista, it can be difficult to work out whether a supplied name is
 * what one might expect it to be.
 * So we allow for multiple soundex matches across several kingdoms.
 * </p>
 */
public class KingdomSoundexDerivation extends CopyDerivation {
    /** The name of the instance variable for the soundex object */
    public static final String SOUNDEX_VAR = TaxonNameSoundexDerivation.INSTANCE_VAR;
    /** The name of the instance variable for the kingdome generator object */
    public static final String GENERATOR_VAR = "kingdomGenerator";

    /**
     * Construct a new derivation.
     */
    public KingdomSoundexDerivation() {
    }

    /**
     * Construct a derivation from a source.
     *
     * @param condition Any condition
     * @param sources The source
     */
    public KingdomSoundexDerivation(Condition condition, List<Observable<?>> sources) {
        super(condition, sources);
    }

    /**
     * This generates multiple values for the soundex
     * @return
     */
    @Override
    public boolean isMultiValued() {
        return true;
    }

    /**
     * Is this dervivation optional
     *
     * @return True if there is a match option that can be applied
     */
    @JsonIgnore
    @Override
    public boolean isOptional() {
        return true;
    }

    /**
     * Only perform this derivation if fuzzy matching is allowed
     *
     * @param optionsVar The variable holding the match options
     * @return A condition to use or null for no condition
     */
    @Override
    public String buildOptionCondition(String optionsVar) {
        return optionsVar + ".isFuzzyDerivations()";
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
        return Arrays.asList(
                new Variable(TaxonNameSoundEx.class, SOUNDEX_VAR),
                new Variable(KingdomGenerator.class, GENERATOR_VAR)
        );
    }

    @Override
    public Collection<Variable> getClassificationVariables() {
        return Collections.singletonList(new Variable(TaxonNameSoundEx.class, SOUNDEX_VAR));
    }

    @Override
    public String generateBuilderTransform(String var, String extra2, String documentVar) {
        return "this." + GENERATOR_VAR+ ".generate((String) " + var + ", " + SOUNDEX_VAR + ")";
    }

    @Override
    public String generateClassificationTransform() {
        return this.getSources().stream().map(s -> "this." + SOUNDEX_VAR + ".treatWord(this." + s.getJavaVariable() + ", org.gbif.nameparser.api.Rank.KINGDOM, org.gbif.nameparser.api.NameType.SCIENTIFIC, false)").collect(Collectors.joining(" + "));
    }

    public static class KingdomGenerator {
        public List<String> generate(String original, TaxonNameSoundEx soundex) {
            if (original == null)
                return Collections.emptyList();
            List<String> classes = KingdomAnalysis.KINGDOM_CLASS.get(original.toUpperCase());
            if (classes == null)
                classes = Collections.singletonList(original);
            return classes.stream().map(k -> soundex.treatWord(k, Rank.KINGDOM, NameType.SCIENTIFIC, false)).collect(Collectors.toList());
        }
    }

}
