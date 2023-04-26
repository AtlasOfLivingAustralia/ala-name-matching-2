package au.org.ala.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.gbif.dwc.terms.Term;

import java.io.IOException;

/**
 * A serializer for string that writes empty strings as nulls
 */
public class EmptyStringSerializer extends StdSerializer<String> {
    public EmptyStringSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value == null || value.isEmpty())
            jsonGenerator.writeNull();
        else
            jsonGenerator.writeString(value);
    }
}
