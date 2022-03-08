package au.org.ala.names;

import au.org.ala.util.JsonUtils;
import au.org.ala.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.*;

public class AutocompleteTest {
    @Test
    public void testCreate1() throws Exception {
        Autocomplete autocomplete = Autocomplete.builder()
                .name("Eccles")
                .build();
        assertEquals("Eccles", autocomplete.getName());
        assertNull(autocomplete.getTaxonId());
        assertNull(autocomplete.getLeft());
    }

    @Test
    public void testToJson1() throws Exception {
         Autocomplete autocomplete = Autocomplete.builder()
                .name("Eccles")
                .taxonId("ID-1")
                 .left(10)
                 .right(20)
                 .acceptedNameUsageId("ID-2")
                 .vernacularName("Der Famus")
                 .vernacularNames(Arrays.asList("Der Famus", "Der Also Famus"))
                .build();
        ObjectMapper mapper = JsonUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, autocomplete.asMap());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "autocomplete-1.json"), writer.toString());
    }

    @Test
    public void testToJson2() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Ecclesonia goonus";
        classification.genus = "Ecclesonia";
        classification.nomenclaturalCode = NomenclaturalCode.ZOOLOGICAL;
        Autocomplete autocomplete = Autocomplete.builder()
                .score(20.0f)
                .name("Eccles")
                .taxonId("ID-1")
                .left(10)
                .right(20)
                .classification(classification)
                .build();
        ObjectMapper mapper = JsonUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, autocomplete.asMap());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "autocomplete-2.json"), writer.toString());
    }


    @Test
    public void testToJson3() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Ecclesonia goonus";
        classification.genus = "Ecclesonia";
        classification.nomenclaturalCode = NomenclaturalCode.ZOOLOGICAL;
        classification.addIssue(AlaLinnaeanFactory.CONFER_SPECIES_NAME);
        classification.addHint(AlaLinnaeanFactory.kingdom, "Animalia");
        classification.addHint(AlaLinnaeanFactory.kingdom, "Protista");
        classification.addHint(AlaLinnaeanFactory.phylum, "Chordata");
        Autocomplete autocomplete = Autocomplete.builder()
                .score(20.0f)
                .name("Eccles")
                .taxonId("ID-1")
                .left(10)
                .right(20)
                .classification(classification)
                .build();
        ObjectMapper mapper = JsonUtils.createMapper();
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, autocomplete.asMap());
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "autocomplete-3.json"), writer.toString());
    }

}
