package au.org.ala.names;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.derivation.CopyDerivation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

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
    private Observable rank;

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
        return "this." + INSTANCE_VAR + ".treatWord((String) " + var + ", " + extra + ")";
    }

    @Override
    public String generateClassificationTransform() {
        final String extra;
        if (this.rank != null) {
            if (org.gbif.api.vocabulary.Rank.class.isAssignableFrom(this.rank.getType()))
                extra = "this." + this.rank.getJavaVariable() + " == null ? null : org.gbif.nameparser.api.Rank.valueOf(this." + this.rank.getJavaVariable() + ".name())";
            else if (org.gbif.nameparser.api.Rank.class.isAssignableFrom(this.rank.getType()))
                extra = "this." + this.rank.getJavaVariable();
            else if (String.class.isAssignableFrom(this.rank.getType()))
                extra = "this." + this.rank.getJavaVariable() + " == null ? null : org.gbif.nameparser.api.Rank.valueOf(this." + this.rank.getJavaVariable() + ")";
            else
                extra = "this." + this.rank.getJavaVariable() + " == null ? null : org.gbif.nameparser.api.Rank.valueOf(this." + this.rank.getJavaVariable() + ".toString())";
        } else
            extra = "null";
        return this.getSources().stream().map(s -> "this." + INSTANCE_VAR + ".treatWord(this." + s.getJavaVariable() + ", " + extra + ")").collect(Collectors.joining(" + "));
    }

}
