package au.org.ala.util;

import au.org.ala.bayesian.Observable.Multiplicity;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Deserializer for GBIF terms.
 */
public class MultiplicityDeserializer extends StdDeserializer<Multiplicity> {
    private static final Map<String, Multiplicity> TERMS =
            Arrays.stream(Multiplicity.values()).collect(Collectors.toMap(v -> v.getLabel(), v -> v));

    public MultiplicityDeserializer() {
        super(Multiplicity.class);
     }

    @Override
    public Multiplicity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getText();

        if (value == null || value.isEmpty())
            return null;
        Multiplicity multiplicity = TERMS.get(value);
        if (multiplicity == null)
            throw new JsonParseException(jsonParser, "Invalid multiplicity " + value);
        return multiplicity;
    }
}
