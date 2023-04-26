package au.org.ala.util;

import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class EMLMetadataParserTest {
    private EMLMetadataParser parser;

    @Before
    public void setUp() throws Exception {
        this.parser = new EMLMetadataParser();
    }

    @Test
    public void testParse1() throws Exception {
        Metadata metadata = this.parser.parse(this.getClass().getResource("/sample-1/eml.xml"), null);
        assertNotNull(metadata);
        assertEquals("ALA-Combined", metadata.getIdentifier());
        assertEquals("ALA Combined Taxonomy", metadata.getTitle());
        assertEquals("2020-05-27T05:13:16Z", DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.ofInstant(metadata.getCreated().toInstant(), ZoneId.of("UTC"))));
        assertEquals("Atlas of Living Australia", metadata.getCreator());
        assertNull(metadata.getPublisher());
        assertNotNull(metadata.getSources());
        assertEquals(4, metadata.getSources().size());
        assertEquals("AFD-20191231", metadata.getSources().get(0).getIdentifier());
    }

    @Test
    public void testParse2() throws Exception {
        Metadata metadata = this.parser.parse(this.getClass().getResource("/sample-2/eml.xml"), null);
        assertNotNull(metadata);
        assertEquals("ALA-Combined", metadata.getIdentifier());
        assertEquals("ALA Combined Taxonomy", metadata.getTitle());
        assertEquals("2022-01-13T05:31:50Z", DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.ofInstant(metadata.getCreated().toInstant(), ZoneId.of("UTC"))));
        assertEquals("Atlas of Living Australia", metadata.getCreator());
        assertNull(metadata.getPublisher());
        assertNotNull(metadata.getSources());
        assertEquals(34, metadata.getSources().size());
        assertEquals("ABRSL-20210719-20211230", metadata.getSources().get(0).getIdentifier());
    }
}

