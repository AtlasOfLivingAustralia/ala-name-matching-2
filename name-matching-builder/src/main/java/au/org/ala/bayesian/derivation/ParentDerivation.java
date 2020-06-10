package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Copy values from a parent source of information.
 * <p>
 * This is specialised towards deriving information
 * from stacks of parent taxonomy.
 * </p>
 */
public class ParentDerivation extends CopyDerivation {
    /** The observable to test the parent against */
    @JsonProperty
    private Observation condition;

    /**
     * Create an empty parent source.
     */
    public ParentDerivation() {
    }

    /**
     * Construct a parent source with appropriate values.
     *
     * @param condition The matching observation
     * @param source The source of data
     */
    public ParentDerivation(Observation condition, Observable source) {
        super(source);
        this.condition = condition;
    }

    /**
     * Test to see whether the found document matches the condition.
     *
     * @param foundVar The variable containing the test document
     * @param documentVar The variable containing the original document
     * @param parentsVar The parent list variable
     *
     * @return code for testing the document.
     */
    @Override
    public String getCondition(String foundVar, String documentVar, String parentsVar) {
        return (this.condition.isPositive() ? "" : "!") + "Arrays.stream(" + foundVar + ".getValues(\"" + this.condition.getObservable().getField() + "\")).anyMatch(x -> \"" + this.condition.getValue() + "\".equals(x))";
    }

}
