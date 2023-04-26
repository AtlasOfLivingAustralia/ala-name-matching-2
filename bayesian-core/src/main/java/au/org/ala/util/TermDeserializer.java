package au.org.ala.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.IOException;

/**
 * Deserializer for GBIF terms.
 */
public class TermDeserializer extends StdDeserializer<Term> {
    private final TermFactory factory;

    public TermDeserializer() {
        super(Term.class);
        this.factory = TermFactory.instance();
    }

    @Override
    public Term deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getText();

        if (value == null || value.isEmpty())
            return null;
        try {
            return this.factory.findTerm(value);
        } catch (IllegalArgumentException ex) {
            throw new JsonParseException(jsonParser, "Unable to read term for " + value, ex);
        }
    }
}
