package au.org.ala.util;

import org.junit.Test;

import java.io.StringWriter;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.junit.Assert.*;

public class MetadataTest {
    private static final Date CREATED1 = new Date(1696554000000l);
    private static final Date MODIFIED1 = new Date(1696554100000l);
    private static final String IDENTIFIER1 = "Test-1";
    private static final String IDENTIFIER2 = "Test-2";
    private static final String IDENTIFIER3 = "Test-3";
    private static final String IDENTIFIER4 = "Test-4";

    private static final URI ABOUT1 = URI.create("http://id.test.com/" + IDENTIFIER1);
    private static final URI ABOUT2 = URI.create("http://id.test.com/" + IDENTIFIER2);
    private static final URI ABOUT3 = URI.create("http://id.test.com/" + IDENTIFIER3);
    private static final URI ABOUT4 = URI.create("http://id.test.com/" + IDENTIFIER4);

    private static final String TYPE1 = "application/json";
    private static final String TITLE1 = "title 1";
    private static final String DESCRIPTION1 = "Description 1";
    private static final String CREATOR1 = "A.N. Other";
    private static final String PUBLISHER1 = "Very Random House";
    private static final String FORMAT1 = "JSON";
    private static final String RIGHTS1 = "Copyright (c) 2022";
    private static final String RIGHTSHOLDER1 = "The very important person";
    private static final String LICENCE1 = "https://creativecommons.org/licenses/by/4.0/";
    private static final String VERSION1 = "1.1";
    private static final String CONTACT1 = "A.N.Other, other@nowhere.com";
    private static final URI REFERENCES1 = URI.create("http://id.test.com/resource");
    private static final String KEY1 = "aKey";
    private static final String VALUE1 = "a value";
    private static final String KEY2 = "anotherKey";
    private static final String VALUE2 = "another value";
    private static final String KEY3 = "key3";
    private static final String VALUE3 = "third value";

    @Test
    public void writeTest1() throws Exception {
        Metadata metadata = Metadata.builder()
                .identifier(IDENTIFIER1)
                .about(ABOUT1)
                .contact(CONTACT1)
                .created(CREATED1)
                .creator(CREATOR1)
                .description(DESCRIPTION1)
                .format(FORMAT1)
                .license(LICENCE1)
                .modified(MODIFIED1)
                .publisher(PUBLISHER1)
                .references(REFERENCES1)
                .rights(RIGHTS1)
                .rightsHolder(RIGHTSHOLDER1)
                .title(TITLE1)
                .type(TYPE1)
                .version(VERSION1)
                 .build();
        StringWriter writer = new StringWriter();
        JsonUtils.createMapper().writeValue(writer, metadata);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "metadata-1.json"), writer.toString());
    }

    @Test
    public void writeTest2() throws Exception {
        Metadata metadata = Metadata.builder()
                .identifier(IDENTIFIER1)
                .created(CREATED1)
                .license(LICENCE1)
                .title(TITLE1)
                .build();
        StringWriter writer = new StringWriter();
        JsonUtils.createMapper().writeValue(writer, metadata);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "metadata-2.json"), writer.toString());
    }


    @Test
    public void writeTest3() throws Exception {
        Metadata metadata1 = Metadata.builder().identifier(IDENTIFIER2).about(ABOUT2).build();
        Metadata metadata2 = Metadata.builder().identifier(IDENTIFIER3).about(ABOUT3).build();
        Metadata metadata = Metadata.builder()
                .identifier(IDENTIFIER1)
                .created(CREATED1)
                .license(LICENCE1)
                .title(TITLE1)
                .sources(Arrays.asList(metadata1, metadata2))
                .build();
        StringWriter writer = new StringWriter();
        JsonUtils.createMapper().writeValue(writer, metadata);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "metadata-3.json"), writer.toString());
    }

    @Test
    public void writeTest4() throws Exception {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put(KEY1, VALUE1);
        properties.put(KEY2, VALUE2);
        Metadata metadata = Metadata.builder()
                .identifier(IDENTIFIER1)
                .created(CREATED1)
                .license(LICENCE1)
                .title(TITLE1)
                .properties(properties)
                .build();
        StringWriter writer = new StringWriter();
        JsonUtils.createMapper().writeValue(writer, metadata);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "metadata-4.json"), writer.toString());
    }


    @Test
    public void readTest1() throws Exception {
        Metadata metadata = Metadata.read(this.getClass().getResource("metadata-1.json"));
        assertEquals(ABOUT1, metadata.getAbout());
        assertEquals(CONTACT1, metadata.getContact());
        assertEquals(CREATED1, metadata.getCreated());
        assertEquals(CREATOR1, metadata.getCreator());
        assertEquals(DESCRIPTION1, metadata.getDescription());
        assertEquals(FORMAT1, metadata.getFormat());
        assertEquals(IDENTIFIER1, metadata.getIdentifier());
        assertEquals(LICENCE1, metadata.getLicense());
        assertEquals(MODIFIED1, metadata.getModified());
        assertNull(metadata.getProperties());
        assertEquals(PUBLISHER1, metadata.getPublisher());
        assertEquals(REFERENCES1, metadata.getReferences());
        assertEquals(RIGHTS1, metadata.getRights());
        assertEquals(RIGHTSHOLDER1, metadata.getRightsHolder());
        assertNull(metadata.getSources());
        assertEquals(TITLE1, metadata.getTitle());
        assertEquals(TYPE1, metadata.getType());
        assertEquals(VERSION1, metadata.getVersion());
    }

    @Test
    public void readTest2() throws Exception {
        Metadata metadata = Metadata.read(this.getClass().getResource("metadata-2.json"));
        assertNull(metadata.getAbout());
        assertNull(metadata.getContact());
        assertEquals(CREATED1, metadata.getCreated());
        assertNull(metadata.getCreator());
        assertNull(metadata.getDescription());
        assertNull(metadata.getFormat());
        assertEquals(IDENTIFIER1, metadata.getIdentifier());
        assertEquals(LICENCE1, metadata.getLicense());
        assertNull(metadata.getModified());
        assertNull(metadata.getProperties());
        assertNull(metadata.getPublisher());
        assertNull(metadata.getReferences());
        assertNull(metadata.getRights());
        assertNull(metadata.getRightsHolder());
        assertNull(metadata.getSources());
        assertEquals(TITLE1, metadata.getTitle());
        assertNull(metadata.getType());
        assertNull(metadata.getVersion());
    }

    @Test
    public void readTest3() throws Exception {
        Metadata metadata = Metadata.read(this.getClass().getResource("metadata-3.json"));
        assertNull(metadata.getAbout());
        assertEquals(CREATED1, metadata.getCreated());
        assertNull(metadata.getCreator());
        assertNull(metadata.getDescription());
        assertNull(metadata.getFormat());
        assertEquals(IDENTIFIER1, metadata.getIdentifier());
        assertEquals(LICENCE1, metadata.getLicense());
        assertNull(metadata.getModified());
        assertNull(metadata.getProperties());
        assertNull(metadata.getPublisher());
        assertNull(metadata.getRights());
        assertNull(metadata.getRightsHolder());
        assertNotNull(metadata.getSources());
        assertEquals(2, metadata.getSources().size());
        assertEquals(ABOUT2, metadata.getSources().get(0).getAbout());
        assertEquals(IDENTIFIER2, metadata.getSources().get(0).getIdentifier());
        assertEquals(ABOUT3, metadata.getSources().get(1).getAbout());
        assertEquals(IDENTIFIER3, metadata.getSources().get(1).getIdentifier());
        assertEquals(TITLE1, metadata.getTitle());
        assertNull(metadata.getType());
    }

    @Test
    public void readTest4() throws Exception {
        Metadata metadata = Metadata.read(this.getClass().getResource("metadata-4.json"));
        assertEquals(CREATED1, metadata.getCreated());
        assertEquals(IDENTIFIER1, metadata.getIdentifier());
        assertEquals(LICENCE1, metadata.getLicense());
        assertNotNull(metadata.getProperties());
        assertEquals(2, metadata.getProperties().size());
        assertEquals(VALUE1, metadata.getProperties().get(KEY1));
        assertEquals(VALUE2, metadata.getProperties().get(KEY2));
    }

    @Test
    public void testCurrency1() throws Exception {
        Metadata metadata = Metadata.builder().build();
        assertNull(metadata.getCurrency());
    }

    @Test
    public void testCurrency2() throws Exception {
        Metadata metadata = Metadata.builder().created(CREATED1).build();
        assertEquals(CREATED1, metadata.getCurrency());
    }

    @Test
    public void testCurrency3() throws Exception {
        Metadata metadata = Metadata.builder().modified(MODIFIED1).build();
        assertEquals(MODIFIED1, metadata.getCurrency());
    }

    @Test
    public void testCurrency4() throws Exception {
        Metadata metadata = Metadata.builder().created(CREATED1).modified(MODIFIED1).build();
        assertEquals(MODIFIED1, metadata.getCurrency());
    }

    @Test
    public void testCurrency5() throws Exception {
        Metadata metadata = Metadata.builder().created(MODIFIED1).modified(CREATED1).build();
        assertEquals(MODIFIED1, metadata.getCurrency());
    }

    @Test
    public void testWith1() throws Exception {
        Metadata metadata = Metadata.builder().build();
        Metadata modified = metadata.with("title", TITLE1);
        assertNotSame(metadata, modified);
        assertEquals(TITLE1, modified.getTitle());
    }

    @Test
    public void testWith2() throws Exception {
        Metadata metadata = Metadata.builder().build();
        Metadata modified = metadata.with("about", ABOUT1);
        assertNotSame(metadata, modified);
        assertEquals(ABOUT1, modified.getAbout());
    }

    @Test
    public void testWith3() throws Exception {
        Metadata metadata = Metadata.builder().build();
        Metadata modified = metadata.with("created", CREATED1);
        assertNotSame(metadata, modified);
        assertEquals(CREATED1, modified.getCreated());
    }

    @Test
    public void testWith4() throws Exception {
        Metadata metadata = Metadata.builder().rightsHolder(RIGHTSHOLDER1).build();
        Metadata modified = metadata.with("rightsHolder", null);
        assertNotSame(metadata, modified);
        assertNull(modified.getRightsHolder());
    }

    @Test
    public void testWith5() throws Exception {
        Metadata metadata = Metadata.builder().build();
        Metadata modified = metadata.with("about", ABOUT1.toString());
        assertNotSame(metadata, modified);
        assertEquals(ABOUT1, modified.getAbout());
    }


    @Test
    public void testWith6() throws Exception {
        Metadata metadata = Metadata.builder().build();
        Metadata modified = metadata.with("created", "2023-10-06T11:00:00+10:00");
        assertNotSame(metadata, modified);
        assertEquals(CREATED1, modified.getCreated());
    }

    @Test
    public void testWith7() throws Exception {
        try {
            Metadata metadata = Metadata.builder().build();
            Metadata modified = metadata.with("created", "2023-10-06");
            fail("Expecting DateTimeParseException");
        } catch (DateTimeParseException ex) {
        }
    }

    @Test
    public void testWithAll1() throws Exception {
        Metadata metadata1 = Metadata.builder().title(TITLE1).build();
        Metadata metadata2 = Metadata.builder().description(DESCRIPTION1).build();
        Metadata metadata = metadata1.with(metadata2);
        assertNotNull(metadata);
        assertEquals(TITLE1, metadata.getTitle());
        assertEquals(DESCRIPTION1, metadata.getDescription());
    }

    @Test
    public void testWithAll2() throws Exception {
        Metadata metadata1 = Metadata.builder()
                .identifier(IDENTIFIER2)
                .about(ABOUT2)
                .created(new Date(CREATED1.getTime() + 10000))
                .creator(CREATOR1 + "*")
                .description(DESCRIPTION1 + "*")
                .format(FORMAT1 + "*")
                .license(LICENCE1 + "*")
                .modified(new Date(MODIFIED1.getTime() + 5000))
                .publisher(PUBLISHER1 + "*")
                .rights(RIGHTS1 + "*")
                .rightsHolder(RIGHTSHOLDER1 + "*")
                .title(TITLE1 + "*")
                .type(TYPE1 + "*")
                .version(VERSION1 + "*")
                .build();
        Metadata metadata2 = Metadata.builder()
                .identifier(IDENTIFIER1)
                .about(ABOUT1)
                .created(CREATED1)
                .creator(CREATOR1)
                .description(DESCRIPTION1)
                .format(FORMAT1)
                .license(LICENCE1)
                .modified(MODIFIED1)
                .publisher(PUBLISHER1)
                .rights(RIGHTS1)
                .rightsHolder(RIGHTSHOLDER1)
                .title(TITLE1)
                .type(TYPE1)
                .version(VERSION1)
                .build();
        Metadata metadata = metadata1.with(metadata2);
        assertNotNull(metadata);
        assertEquals(ABOUT1, metadata.getAbout());
        assertEquals(CREATED1, metadata.getCreated());
        assertEquals(CREATOR1, metadata.getCreator());
        assertEquals(DESCRIPTION1, metadata.getDescription());
        assertEquals(FORMAT1, metadata.getFormat());
        assertEquals(IDENTIFIER1, metadata.getIdentifier());
        assertEquals(LICENCE1, metadata.getLicense());
        assertEquals(MODIFIED1, metadata.getModified());
        assertNull(metadata.getProperties());
        assertEquals(PUBLISHER1, metadata.getPublisher());
        assertEquals(RIGHTS1, metadata.getRights());
        assertEquals(RIGHTSHOLDER1, metadata.getRightsHolder());
        assertNull(metadata.getSources());
        assertEquals(TITLE1, metadata.getTitle());
        assertEquals(TYPE1, metadata.getType());
        assertEquals(VERSION1, metadata.getVersion());
    }

    @Test
    public void testWithAll3() throws Exception {
        Metadata source1 = Metadata.builder().identifier(IDENTIFIER2).about(ABOUT2).build();
        Metadata source2 = Metadata.builder().identifier(IDENTIFIER3).about(ABOUT3).build();
        Metadata source3 = Metadata.builder().identifier(IDENTIFIER4).about(ABOUT4).build();
        Metadata metadata1 = Metadata.builder().identifier(IDENTIFIER1).sources(Arrays.asList(source1, source2)).build();
        Metadata metadata2 = Metadata.builder().identifier(IDENTIFIER2).sources(Arrays.asList(source2, source3)).build();
        Metadata metadata = metadata1.with(metadata2);
        assertNotNull(metadata);
        assertEquals(IDENTIFIER2, metadata.getIdentifier());
        assertNotNull(metadata.getSources());
        assertEquals(3, metadata.getSources().size());
        assertEquals(IDENTIFIER3, metadata.getSources().get(0).getIdentifier());
        assertEquals(IDENTIFIER4, metadata.getSources().get(1).getIdentifier());
        assertEquals(IDENTIFIER2, metadata.getSources().get(2).getIdentifier());
    }


    @Test
    public void testWithAll4() throws Exception {
        Map<String, String> properties1 = new LinkedHashMap<>();
        properties1.put(KEY1, VALUE1 + "*");
        properties1.put(KEY2, VALUE2);
        Map<String, String> properties2 = new LinkedHashMap<>();
        properties2.put(KEY1, VALUE1);
        properties2.put(KEY3, VALUE3);
        Metadata metadata1 = Metadata.builder().identifier(IDENTIFIER1).properties(properties1).build();
        Metadata metadata2 = Metadata.builder().identifier(IDENTIFIER2).properties(properties2).build();
        Metadata metadata = metadata1.with(metadata2);
        assertNotNull(metadata);
        assertEquals(IDENTIFIER2, metadata.getIdentifier());
        assertNotNull(metadata.getProperties());
        assertEquals(3, metadata.getProperties().size());
        assertEquals(VALUE1, metadata.getProperties().get(KEY1));
        assertEquals(VALUE2, metadata.getProperties().get(KEY2));
        assertEquals(VALUE3, metadata.getProperties().get(KEY3));
    }


}
