package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Derivation;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * A null derivation from a source to a target.
 * <p>
 * This effectively renames the observation, letting it be tested against
 * another value.
 * </p>
 */
public class CopyDerivation extends Derivation {
    /** The source observable for this derivation */
    @JsonProperty
    @Getter
    private Observable source;

    /**
     * Empty constructor
     */
    public CopyDerivation() {
    }

    /**
     * Construct for a source
     * @param source
     */
    public CopyDerivation(Observable source) {
        this.source = source;
    }

    /**
     * Generate code to get values from the source,
     *
     * @param documentVar The name of the document variable (a lucene document)
     * @return The code to get the values
     */
    @Override
    public String generateValues(String documentVar, String observablesClass) {
        return documentVar + ".getAll(" + observablesClass + "." + this.source.getJavaVariable() + ")";
    }

    /**
     * Generate the piece of code that transforms the values required
     * for derivation.
     * <p>
     * This throws an {@link UnsupportedOperationException} since it is not possible to copy for a classification.
     * </p>
     *
     * @return The code to get the values
     */
    @Override
    public String generateClassificationTransform() {
        throw new UnsupportedOperationException("Copy derivations do not apply to classifications");
    }

    /**
     * Copy simply assigns the value variable.
     *
     * @param var         The variable that holds the value to be transformed
     * @param extra       The variable that holds extra data
     * @param documentVar The name of the document variable (a lucene document)
     * @return The code to get the values
     */
    @Override
    public String generateBuilderTransform(String var, String extra, String documentVar) {
        return var;
    }
}
