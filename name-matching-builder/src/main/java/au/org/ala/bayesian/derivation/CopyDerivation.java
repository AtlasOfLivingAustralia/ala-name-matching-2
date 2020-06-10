package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Derivation;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;

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
     * Get the source observable.
     *
     * @return The source
     */
    public Observable getSource() {
        return this.source;
    }

    /**
     * Generate code to get values from the source,
     *
     * @param documentVar The name of the document variable (a lucene document)
     * @return The code to get the values
     */
    @Override
    public String getValues(String documentVar) {
        return documentVar + ".getValues(\"" + this.source.getField() + "\")";
    }

    /**
     * Copy simply assigns the value variable.
     *
     * @param var         The variable that holds the value to be transformed
     * @param documentVar The name of the document variable (a lucene document)
     * @return The code to get the values
     */
    @Override
    public String getTransform(String var, String documentVar) {
        return var;
    }
}
