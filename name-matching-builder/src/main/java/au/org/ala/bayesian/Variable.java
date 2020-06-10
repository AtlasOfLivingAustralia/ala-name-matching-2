package au.org.ala.bayesian;

abstract public class Variable {
    /** The variable identifier */
    private String id;

    /**
     * Construct for an identifier.
     *
     * @param id The variable identifier
     */
    public Variable(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
