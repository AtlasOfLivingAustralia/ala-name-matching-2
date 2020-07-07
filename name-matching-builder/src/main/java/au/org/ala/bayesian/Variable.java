package au.org.ala.bayesian;

import lombok.Getter;

abstract public class Variable {
    /** The variable identifier */
    @Getter
    private String id;

    /**
     * Construct for an identifier.
     *
     * @param id The variable identifier
     */
    public Variable(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
