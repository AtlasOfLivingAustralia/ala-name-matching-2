package au.org.ala.bayesian;

import au.org.ala.util.IdentifierConverter;
import au.org.ala.util.SimpleIdentifierConverter;
import au.org.ala.util.TermIdentifierConverter;
import lombok.Getter;

/**
 * Different source of external content.
 * <p>
 * These contain formation on how to format/manage things for that external source.
 * </p>
 */
public enum ExternalContext {
    LUCENE(new TermIdentifierConverter("[a-zA-Z0-9_]", "_", true)),
    JAVA_VARIABLE(SimpleIdentifierConverter.JAVA_VARIABLE),
    JAVA_CONSTANT(SimpleIdentifierConverter.JAVA_CONSTANT);

    /** How to convert whatever identifier into something acceptable to the external system */
    @Getter
    private IdentifierConverter converter;

    /**
     * Construct with a converter.
     *
     * @param converter The converter
     */
    ExternalContext(IdentifierConverter converter) {
        this.converter = converter;
    }
}
