package au.org.ala.names;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.derivation.CopyDerivation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.gbif.nameparser.api.Rank;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Derive a new name based on the taxamatch soundex algorithm.
 */
public class TaxonNameSoundexDerivation extends CopyDerivation {
    /** The name of the instance variable for the soundex object */
    public static final String INSTANCE_VAR = "nameSoundex";

    /** The rank observable for context */
    @JsonProperty
    @Getter
    private Rank rank;

    /**
     * Construct a new derivation.
     */
    public TaxonNameSoundexDerivation() {
    }

    /**
     * Construct a derivation from a source.
     *
     * @param source The source
     */
    public TaxonNameSoundexDerivation(Observable source) {
        super(source);
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
        extra = this.rank == null ? "null" : "org.gbif.nameparser.api.Rank." + this.rank.name();
        return "this." + INSTANCE_VAR + ".treatWord((String) " + var + ", " + extra + ")";
    }

    @Override
    public String generateClassificationTransform() {
        final String extra = this.rank == null ? "null" : "org.gbif.nameparser.api.Rank." + this.rank.name();
        return this.getSources().stream().map(s -> "this." + INSTANCE_VAR + ".treatWord(this." + s.getJavaVariable() + ", " + extra + ")").collect(Collectors.joining(" + "));
    }

}
