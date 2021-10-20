package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Derivation;
import au.org.ala.bayesian.Observable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A null derivation from a source to a target.
 * <p>
 * This effectively renames the observation, letting it be tested against
 * another value.
 * </p>
 */
public class CopyDerivation extends Derivation {
    /** The source observables for this derivation */
    @JsonProperty
    @Getter
    private List<Observable> sources;

    /**
     * Empty constructor
     */
    public CopyDerivation() {
    }

    /**
     * Construct for a source
     *
     * @param source The source to copy from
     */
    public CopyDerivation(Observable source) {
        this.sources = Arrays.asList(source);
    }

    /**
     * Get the list of variables that provide input to this derivatiobn.
     *
     * @return The set of sources.
     */
    @Override
    public Set<Observable> getInputs() {
        return new HashSet<>(this.sources);
    }

    /**
     * Does this have a transform?
     *
     * @return False, since no transform
     */
    @Override
    public boolean hasTransform() {
        return false;
    }

    /**
     * Generate code to get values from the source,
     *
     * @param documentVar The name of the document variable (a lucene document)
     * @return The code to get the values
     */
    @Override
    public String generateValue(String documentVar, String observablesClass) {
        StringBuilder builder = new StringBuilder();
        builder.append(documentVar + ".get(");
        builder.append(this.sources.stream().map(s -> observablesClass + "." + s.getJavaVariable()).collect(Collectors.joining(", ")));
        builder.append(")");
        return builder.toString();
    }

    /**
     * Generate code to get values from the source,
     *
     * @param documentVar The name of the document variable (a lucene document)
     * @return The code to get the values
     */
    @Override
    public String generateVariants(String documentVar, String observablesClass) {
        StringBuilder builder = new StringBuilder();
        builder.append(documentVar + ".getAll(");
        builder.append(this.sources.stream().map(s -> observablesClass + "." + s.getJavaVariable()).collect(Collectors.joining(", ")));
        builder.append(")");
        return builder.toString();
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
        throw new UnsupportedOperationException("Copy derivations do not transform");
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
        throw new UnsupportedOperationException("Copy derivations do not transform");
    }
}
