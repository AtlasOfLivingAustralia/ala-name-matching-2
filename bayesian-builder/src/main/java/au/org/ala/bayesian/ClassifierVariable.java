package au.org.ala.bayesian;

/**
 * A variable that holds the probability that there is a match between a piece of evidence and its expected value.
 */
public class ClassifierVariable extends Variable {
    /** The observable that this evidence is associated with */
    private final Observable observable;

    public ClassifierVariable(Observable observable) {
        super(observable.getJavaVariable());
        this.observable = observable;
    }
}
