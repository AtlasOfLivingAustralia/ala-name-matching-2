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
        assertNull(analysis.fromString(null, null));
        assertNull(analysis.fromString("", null));
        assertNull(analysis.fromString("  ", null));
        assertEquals("Bacteria", analysis.fromString("Bacteria", null));
        assertEquals("ANIMALIA", analysis.fromString("ANIMALIA", null));
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
        assertFalse(analysis.equivalent("Chromista", "Protista"));
        assertFalse(analysis.equivalent("plantae", "Viridiplantae"));
        assertFalse(analysis.equivalent("Archaea", "bacteria"));
        assertFalse(analysis.equivalent("Protozoa", "Chromista"));
        assertFalse(analysis.equivalent("Plantae", "Chromista"));
        assertFalse(analysis.equivalent("Fungi", "Chromista"));
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
        assertFalse(analysis.equivalent("Fungi", "FINGI"));
        assertFalse(analysis.equivalent("CRAMISTA", "Chromista"));
        assertFalse(analysis.equivalent("PLANTI", "Chromista"));
        assertFalse(analysis.equivalent("PLANTI", "PRATACA"));
        assertFalse(analysis.equivalent("Virus", "Viruses"));
    }


    @Test
    public void testEquivalent6() throws Exception {
        assertFalse(analysis.equivalent("BACTIRA", "FINGI"));
        assertFalse(analysis.equivalent("CRAMISTA", "Bacteria"));
        assertFalse(analysis.equivalent("PLANTI", "Animalia"));
    }


}
