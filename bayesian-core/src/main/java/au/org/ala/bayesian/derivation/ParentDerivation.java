package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.bayesian.StoreException;
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
    /** Include the destination classifier in searches for values. True by default */
    @JsonProperty
    private boolean includeSelf = true;

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
     * Include the destination classifier in searches for sources to copy from?
     *
     * @return As specified by the derivation
     */
    @Override
    public boolean isIncludeSelf() {
        return this.includeSelf;
    }

    /**
     * Test to see whether the found document matches the condition.
     *
     * @param foundVar The variable containing the test document
     * @param classifierVar The variable containing the original document
     * @param observablesClass The class holding observables definitions
     * @param parentsVar The parent list variable
     *
     * @return code for testing the document.
     */
    @Override
    public String generateCondition(String foundVar, String classifierVar, String observablesClass, String parentsVar) throws StoreException {
        Object value = this.condition.getObservable().getAnalysis().fromString(this.condition.getValue().toString());
        String valueStatement = null;
        if (value == null)
            valueStatement = "x == null";
        else if (value instanceof Number)
            valueStatement = value.toString() + " == x";
        else if (value instanceof Enum)
            valueStatement = value.getClass().getName() + "." + ((Enum<?>) value).name() + " == x";
        else
            valueStatement = "\"" + value.toString() + "\".equals(x)";
        return (this.condition.isPositive() ? "" : "!") + foundVar + ".getAll(" + observablesClass + "." + this.condition.getObservable().getJavaVariable() + ").stream().anyMatch(x -> " + valueStatement + ")";
    }

}
