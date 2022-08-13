package au.org.ala.location;

import au.org.ala.vocab.GeographyType;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeographyTypeAnalysisTest {
    GeographyTypeAnalysis analysis = new GeographyTypeAnalysis();

    @Test
    public void testType1() {
        assertEquals(GeographyType.class, this.analysis.getType());
    }

    @Test
    public void testAnalyse1() throws Exception{
        assertNull(this.analysis.analyse(null));
    }

    @Test
    public void testAnalyse2() throws Exception {
        assertEquals(GeographyType.country, this.analysis.analyse(GeographyType.country));
        assertEquals(GeographyType.stateProvince, this.analysis.analyse(GeographyType.stateProvince));
    }

    @Test
    public void testToStore1() throws Exception {
        assertNull(this.analysis.toStore(null));
        assertEquals("continent", this.analysis.toStore(GeographyType.continent));
        assertEquals("islandGroup", this.analysis.toStore(GeographyType.islandGroup));
    }

    @Test
    public void testFromString1() throws Exception {
        assertNull(this.analysis.fromString(null));
        assertNull(this.analysis.fromString(""));
        assertNull(this.analysis.fromString("  "));
        assertEquals(GeographyType.country, this.analysis.fromString("country"));
        assertEquals(GeographyType.stateProvince, this.analysis.fromString("stateProvince"));
    }

    @Test
    public void testFromString2() throws Exception {
        assertEquals(GeographyType.country, this.analysis.fromString("Country"));
        assertEquals(GeographyType.stateProvince, this.analysis.fromString("STATEPROVINCE"));
        assertEquals(GeographyType.waterBody, this.analysis.fromString("sea"));
    }

    @Test
    public void testFromString3() throws Exception {
        assertEquals(GeographyType.country, analysis.fromString("http://ala.org.au/vocabulary/1.0/geographyType/country"));
        assertNull(analysis.fromString("http://ala.org.au/vocabulary/1.0/geographyType/Country"));
    }

}
