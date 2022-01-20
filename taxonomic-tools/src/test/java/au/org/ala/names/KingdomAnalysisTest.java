package au.org.ala.names;

import org.junit.Test;

import static org.junit.Assert.*;

public class KingdomAnalysisTest {
    KingdomAnalysis analysis = new KingdomAnalysis();

    @Test
    public void testType1() {
        assertEquals(String.class, analysis.getType());
    }

    @Test
    public void testAnalyse1() throws Exception{
        assertNull(analysis.analyse(null));
    }

    @Test
    public void testAnalyse2() throws Exception {
        assertEquals("Animalia", analysis.analyse("Animalia"));
        assertEquals("Animalia", analysis.analyse("ANIMALIA"));
        assertEquals("Fungi", analysis.analyse("fungi"));
        assertEquals("Fungi", analysis.analyse("FINGI"));
    }

    @Test
    public void testToStore1() throws Exception {
        assertNull(analysis.toStore(null));
        assertEquals("animalia", analysis.toStore("animalia"));
        assertEquals("Fungi", analysis.toStore("Fungi"));
    }

    @Test
    public void testFromString1() throws Exception {
        assertNull(analysis.fromString(null));
        assertNull(analysis.fromString(""));
        assertNull(analysis.fromString("  "));
        assertEquals("Bacteria", analysis.fromString("Bacteria"));
        assertEquals("ANIMALIA", analysis.fromString("ANIMALIA"));
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertNull(analysis.equivalent(null, "Animalia"));
        assertNull(analysis.equivalent("Chromista", null));
    }

    @Test
    public void testEquivalent2() throws Exception {
        assertTrue(analysis.equivalent("Animalia", "Animalia"));
        assertTrue(analysis.equivalent("Animalia", "ANIMALIA"));
        assertTrue(analysis.equivalent("Flurble", "FLURBLE"));
    }

    @Test
    public void testEquivalent3() throws Exception {
        assertTrue(analysis.equivalent("Chromista", "Protista"));
        assertTrue(analysis.equivalent("plantae", "Viridiplantae"));
        assertTrue(analysis.equivalent("Archaea", "bacteria"));
        assertTrue(analysis.equivalent("Protozoa", "Chromista"));
        assertTrue(analysis.equivalent("Plantae", "Chromista"));
        assertTrue(analysis.equivalent("Fungi", "Chromista"));
    }

    @Test
    public void testEquivalent4() throws Exception {
        assertFalse(analysis.equivalent("Fungi", "Animalia"));
        assertFalse(analysis.equivalent("Plantae", "Virus"));
        assertFalse(analysis.equivalent("Plantae", "Flurble"));
        assertFalse(analysis.equivalent("Fungi", "Bacteria"));
     }

    @Test
    public void testEquivalent5() throws Exception {
        assertTrue(analysis.equivalent("Fungi", "FINGI"));
        assertTrue(analysis.equivalent("CRAMISTA", "Chromista"));
        assertTrue(analysis.equivalent("PLANTI", "Chromista"));
        assertTrue(analysis.equivalent("PLANTI", "PRATACA"));
        assertTrue(analysis.equivalent("Virus", "Viruses"));
    }


    @Test
    public void testEquivalent6() throws Exception {
        assertFalse(analysis.equivalent("BACTIRA", "FINGI"));
        assertFalse(analysis.equivalent("CRAMISTA", "Bacteria"));
        assertFalse(analysis.equivalent("PLANTI", "Animalia"));
    }


}
