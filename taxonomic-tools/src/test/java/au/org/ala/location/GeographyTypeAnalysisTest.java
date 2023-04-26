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
    
    @Test
    public void testEquivalence1() throws Exception {
        assertTrue(this.analysis.equivalent(GeographyType.municipality, GeographyType.municipality));
        assertTrue(this.analysis.equivalent(GeographyType.county, GeographyType.county));
        assertTrue(this.analysis.equivalent(GeographyType.stateProvince, GeographyType.stateProvince));
        assertTrue(this.analysis.equivalent(GeographyType.country, GeographyType.country));
        assertTrue(this.analysis.equivalent(GeographyType.continent, GeographyType.continent));
        assertTrue(this.analysis.equivalent(GeographyType.island, GeographyType.island));
        assertTrue(this.analysis.equivalent(GeographyType.islandGroup, GeographyType.islandGroup));
        assertTrue(this.analysis.equivalent(GeographyType.waterBody, GeographyType.waterBody));
        assertTrue(this.analysis.equivalent(GeographyType.other, GeographyType.other));
    }
    
    @Test
    public void testEquivalence2() throws Exception {
        assertTrue(this.analysis.equivalent(GeographyType.municipality, GeographyType.county));
        assertTrue(this.analysis.equivalent(GeographyType.county, GeographyType.municipality));
        assertTrue(this.analysis.equivalent(GeographyType.county, GeographyType.stateProvince));
        assertTrue(this.analysis.equivalent(GeographyType.stateProvince, GeographyType.county));
        assertTrue(this.analysis.equivalent(GeographyType.stateProvince, GeographyType.island));
        assertTrue(this.analysis.equivalent(GeographyType.stateProvince, GeographyType.country));
        assertTrue(this.analysis.equivalent(GeographyType.country, GeographyType.stateProvince));
        assertTrue(this.analysis.equivalent(GeographyType.islandGroup, GeographyType.waterBody));
        assertTrue(this.analysis.equivalent(GeographyType.waterBody, GeographyType.islandGroup));
        assertTrue(this.analysis.equivalent(GeographyType.municipality, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.county, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.stateProvince, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.country, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.continent, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.island, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.islandGroup, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.waterBody, GeographyType.other));
        assertTrue(this.analysis.equivalent(GeographyType.other, GeographyType.country));
    }
    
    @Test
    public void testEquivalence3() throws Exception {
        assertFalse(this.analysis.equivalent(GeographyType.municipality, GeographyType.country));
        assertFalse(this.analysis.equivalent(GeographyType.county, GeographyType.continent));
        assertFalse(this.analysis.equivalent(GeographyType.county, GeographyType.waterBody));
        assertFalse(this.analysis.equivalent(GeographyType.stateProvince, GeographyType.continent));
        assertFalse(this.analysis.equivalent(GeographyType.country, GeographyType.island));
        assertFalse(this.analysis.equivalent(GeographyType.country, GeographyType.islandGroup));
        assertFalse(this.analysis.equivalent(GeographyType.island, GeographyType.waterBody));
        assertFalse(this.analysis.equivalent(GeographyType.islandGroup, GeographyType.continent));
        assertFalse(this.analysis.equivalent(GeographyType.island, GeographyType.islandGroup));
        assertFalse(this.analysis.equivalent(GeographyType.islandGroup, GeographyType.island));
        assertFalse(this.analysis.equivalent(GeographyType.continent, GeographyType.waterBody));
        assertFalse(this.analysis.equivalent(GeographyType.waterBody, GeographyType.county));
    }

}
