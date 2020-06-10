package au.org.ala.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.gbif.dwc.terms.Term;

import java.io.IOException;

/**
 * A serializer for GBIF terms. Writes the term as a URI.
 */
public class TermSerializer extends StdSerializer<Term> {
    public TermSerializer() {
        super(Term.class);
    }

    @Override
    public void serialize(Term term, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(term.qualifiedName());
    }
}
