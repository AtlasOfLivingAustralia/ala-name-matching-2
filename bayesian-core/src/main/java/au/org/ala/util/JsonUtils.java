package au.org.ala.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.gbif.dwc.terms.Term;

/**
 * Common JSON serialisation/deserialisation utilities.
 */
public class JsonUtils {
    /**
     * Create a suitably configured object mapper.
     *
     * @return The object mapper
     */
    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Term.class, new TermDeserializer());
        module.addSerializer(Term.class, new TermSerializer());
        module.addKeyDeserializer(Term.class, new TermKeyDeserializer());
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new StdDateFormat());
        return mapper;
    }
}
