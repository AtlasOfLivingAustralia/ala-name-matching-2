package au.org.ala.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.io.IOException;

/**
 * Deserializer for GBIF terms.
 */
public class TermKeyDeserializer extends KeyDeserializer {
    private TermFactory factory;

    public TermKeyDeserializer() {
        this.factory = TermFactory.instance();
    }

    @Override
    public Object deserializeKey(String value, DeserializationContext deserializationContext) throws IOException {
         if (value == null || value.isEmpty())
            return null;
        try {
            return this.factory.findTerm(value);
        } catch (IllegalArgumentException ex) {
            throw new IOException("Unable to read term for " + value, ex);
        }
    }
}
