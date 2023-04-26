package au.org.ala.names;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.derivation.CopyDerivation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.gbif.nameparser.api.NameType;
import org.gbif.nameparser.api.Rank;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Derive a new name based on the taxamatch soundex algorithm.
 */
public class TaxonNameSoundexDerivation extends CopyDerivation {
    /** The name of the instance variable for the soundex object */
    public static final String INSTANCE_VAR = "nameSoundex";

    /** The rank for context */
    @JsonProperty
    @Getter
    private Rank rank;
    /** The name type observable */
    @JsonProperty
    @Getter
    private Observable<NameType> nameType = null;
    /** The name type to use if not specified in the observable */
    @JsonProperty
    @Getter
    private NameType defaultNameType = NameType.SCIENTIFIC;
    /** Treat this as an epithet only */
    @JsonProperty
    @Getter
    private boolean epithet = false;

    /**
     * Construct a new derivation.
     */
    public TaxonNameSoundexDerivation() {
    }

    /**
     * Construct a derivation from a source.
     *
     * @param condition Any condition
     * @param sources The source
     */
    public TaxonNameSoundexDerivation(Condition condition, List<Observable<?>> sources) {
        super(condition, sources);
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
     * Get any extra observable needed to compute things.
     *
     * @return The name type observable, if it exists
     */
    @JsonIgnore
    public Observable<NameType> getExtra() {
        return this.nameType;
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
        return Collections.singletonList(new Variable(TaxonNameSoundEx.class, INSTANCE_VAR));
    }

    @Override
    public Collection<Variable> getClassificationVariables() {
        return Collections.singletonList(new Variable(TaxonNameSoundEx.class, INSTANCE_VAR));
    }

    @Override
    public String generateBuilderTransform(String var, String extra2, String documentVar) {
        final String extra1 = this.rank == null ? "null" : "org.gbif.nameparser.api.Rank." + this.rank.name();
        extra2 = this.nameType == null ? "org.gbif.nameparser.api.NameType." + this.defaultNameType.name() : extra2;
        final String extra3 = this.epithet ? "true" : "false";
        return "this." + INSTANCE_VAR + ".treatWord((String) " + var + ", " + extra1 + ", " + extra2 + ", " + extra3 + ")";
    }

    @Override
    public String generateClassificationTransform() {
        final String extra1 = this.rank == null ? "null" : "org.gbif.nameparser.api.Rank." + this.rank.name();
        final String extra2 = this.nameType == null ? "org.gbif.nameparser.api.NameType." + this.defaultNameType.name() : "this." + this.nameType.getJavaVariable();
        final String extra3 = this.epithet ? "true" : "false";
        return this.getSources().stream().map(s -> "this." + INSTANCE_VAR + ".treatWord(this." + s.getJavaVariable() + ", " + extra1 + ", " + extra2 + ", " + extra3 + ")").collect(Collectors.joining(" + "));
    }

}
