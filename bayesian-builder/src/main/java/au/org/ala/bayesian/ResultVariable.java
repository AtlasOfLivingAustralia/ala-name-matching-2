package au.org.ala.bayesian;

public class ResultVariable extends Variable {
    /** The observable this result is associated with */
    private final Observable observable;


    public ResultVariable(String id) {
        super(id);
        this.observable = null;
    }

    public ResultVariable(String prefix, Observable observable) {
        super(prefix + "$" + observable.getJavaVariable());
        this.observable = observable;
    }
}
