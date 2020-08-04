package au.org.ala.bayesian;

/**
 * A variable that holds the probability that there is a match between a piece of evidence and its expected value.
 */
public class EvidenceVariable extends Variable {
    /** The observable that this evidence is associated with */
    private Observable observable;

    public EvidenceVariable(Observable observable) {
        super("e$" + observable.getJavaVariable());
        this.observable = observable;
    }
}
