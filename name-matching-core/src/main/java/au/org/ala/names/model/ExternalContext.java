package au.org.ala.names.model;

import au.org.ala.util.IdentifierConverter;
import au.org.ala.util.SimpleIdentifierConverter;
import au.org.ala.util.TermIdentifierConverter;

/**
 * Different source of external content.
 * <p>
 * These contain formation on how to format/manage things for that external source.
 * </p>
 */
public enum ExternalContext {
    LUCENE(new TermIdentifierConverter("[a-zA-Z0-9_]", "_", true)),
    JAVA_VARIABLE(SimpleIdentifierConverter.JAVA_VARIABLE);

    /** How to convert whatever identifier into something acceptable to the external system */
    private IdentifierConverter converter;

    /**
     * Construct with a converter.
     *
     * @param converter The converter
     */
    ExternalContext(IdentifierConverter converter) {
        this.converter = converter;
    }

    /**
     * Get the identifier converter for this source
     *
     * @return The converter
     */
    public IdentifierConverter getConverter() {
        return converter;
    }
}
