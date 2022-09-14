package au.org.ala.bayesian;

import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * Options for searching and matching.
 */
@Value
@Builder
@With
public class MatchOptions {
    /** Use all possible options while searching (but do not measure or trace) */
    public static final MatchOptions ALL = MatchOptions.builder().build();
    /** Use no options while searching */
    public static final MatchOptions NONE = MatchOptions.builder()
            .normaliseTemplate(false)
            .canonicalDerivations(false)
            .fuzzyDerivations(false)
            .modifyTemplate(false)
            .modifyConsistency(false)
            .useHints(false)
            .build();

    /**
     * Normalise the values in the template.
     * <p>
     * Input data can be cleaned, canonicalised and made consistent.
     * </p>
     */
    @Builder.Default
    boolean normaliseTemplate = true;
    /**
     * Perform canonical derivations.
     * <p>
     * Include derivations that are directly inferrable from the source data.
     * </p>
     */
    @Builder.Default
    boolean canonicalDerivations = true;
    /**
     * Perform fuzzy derivations.
     * <p>
     * Include derivations that are provide secondary ways of looking at data in the template,
     * for example, soundex versions of terms.
     * </p>
     */
    @Builder.Default
    boolean fuzzyDerivations = true;
    /**
     * Allow modifications to the search template.
     * <p>
     * These modifications indicate large-scale modifications to the search template,
     * such as moving to higher-order terms in the template if the lower-order
     * terms do not work.
     * </p>
     *
     * @see #modifyConsistency
     */
    @Builder.Default
    boolean modifyTemplate = true;
    /**
     * Allow modifications of the template to avoid contradictory information.
     * <p>
     * These modifications do not alter the essence of what is being searched for.
     * Instead, they adjust for inconsitencies in the supplied data.
     * </p>
     *
     * @see #modifyTemplate
     */
    @Builder.Default
    boolean modifyConsistency = true;
    /**
     * Use any hints that have been supplied.
     */
    @Builder.Default
    boolean useHints = true;
    /**
     * Measure performance when making the match
     */
    @Builder.Default
    boolean measure = false;
    /**
     * Trace the inference
     */
    @Builder.Default
    boolean trace = false;
}
