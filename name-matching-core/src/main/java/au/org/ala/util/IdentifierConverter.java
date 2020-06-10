package au.org.ala.util;

import au.org.ala.names.model.Identifiable;

/**
 * Convert an identifiable object into an identifier usable in code generation or
 * as a column name in a store.
 */
public interface IdentifierConverter {
    /**
     * Provide an identifier in the style this converter has become accustomed to.
     *
     * @param id The identifiable to convert into an identifier
     *
     * @return A string that can be used as a variable/column name
     */
    public String convert(Identifiable id);
}
