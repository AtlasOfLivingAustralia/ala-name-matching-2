package au.org.ala.bayesian.derivation;

import au.org.ala.bayesian.Condition;
import au.org.ala.bayesian.NetworkCompiler;
import au.org.ala.bayesian.Observable;
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
    /** The condition which to choose a parent */
    @JsonProperty
    private Condition select;
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
     * @param select The parent matcher
     * @param condition The matching observation
     * @param source The source of data
     */
    public ParentDerivation(Condition select, Condition condition, Observable source) {
        super(condition, source);
        this.select = select;
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
     * Does this have a selection
     *
     * @return True if there is a condition attached to the source selection
     */
    public boolean isSelectable() {
        return this.select != null;
    }

    /**
     * Test to see whether the found document matches the condition.
     *
     * @param compiler The network compiler
     * @param foundVar The variable containing the test classifier
     * @param classifierVar The variable containing the original classifier
     * @param observablesClass The class holding observables definitions
     * @param parentsVar The parent list variable
     *
     * @return code for testing the document.
     */
    public String generateSelect(NetworkCompiler compiler, String foundVar, String classifierVar, String observablesClass, String parentsVar) throws StoreException {
        return this.select == null ? null : this.select.buildClassifierCheck(compiler, foundVar, observablesClass);
    }

}
