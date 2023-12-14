package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * A broadener contains the conditions under which a concept is eligable for broadening and
 * the type of things that allow broadening.
 * <p>
 * Broadeners travel up/down the parent/child hierarchy until the broadening condition is no longer met.
 * Terms from these elements are added to the possible match names.
 * </p>
 */
public class Broadener {
    /** This condition identifies a target concept that can br broadened */
    @JsonProperty
    @Getter
    private Condition broaden;
    /** This condition identifies a parent/child of the target that can feed additional terms */
    @JsonProperty
    @Getter
    private Condition condition;

    /**
     * Construct an empty broadener
     */
    private Broadener() {
    }

    /**
     * Construct a broadener
     *
     * @param broaden The condition for the target concept
     * @param condition The condition for broadening parent/child concepts
     */
    public Broadener(Condition broaden, Condition condition) {
        this.broaden = broaden;
        this.condition = condition;
    }

}
