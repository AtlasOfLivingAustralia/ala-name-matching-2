package au.org.ala.util;

import au.org.ala.bayesian.Observable.Multiplicity;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serializer for observable multiplicity.
 */
public class MulitplicitySerializer extends StdSerializer<Multiplicity> {
    public MulitplicitySerializer() {
        super(Multiplicity.class);
    }

    @Override
    public void serialize(Multiplicity multiplicity, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(multiplicity.getLabel());
    }
}