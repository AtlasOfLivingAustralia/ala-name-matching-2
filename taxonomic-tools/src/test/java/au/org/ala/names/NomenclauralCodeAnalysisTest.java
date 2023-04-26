package au.org.ala.names;

import org.gbif.api.vocabulary.NomenclaturalCode;
import org.junit.Test;

import static org.junit.Assert.*;

public class NomenclauralCodeAnalysisTest {
    NomenclaturalCodeAnalysis analysis = new NomenclaturalCodeAnalysis();

    @Test
    public void testType1() {
        assertEquals(NomenclaturalCode.class, analysis.getType());
    }

    @Test
    public void testAnalyse1() throws Exception{
        assertNull(analysis.analyse(null));
    }

    @Test
    public void testAnalyse2() throws Exception {
        assertEquals(NomenclaturalCode.BOTANICAL, analysis.analyse(NomenclaturalCode.BOTANICAL));
        assertEquals(NomenclaturalCode.VIRUS, analysis.analyse(NomenclaturalCode.VIRUS));
    }

    @Test
    public void testToStore1() throws Exception {
        assertNull(analysis.toStore(null));
        assertEquals("ICBN", analysis.toStore(NomenclaturalCode.BOTANICAL));
        assertEquals("ICNCP", analysis.toStore(NomenclaturalCode.CULTIVARS));
    }

    @Test
    public void testFromString1() throws Exception {
        assertNull(analysis.fromString(null));
        assertNull(analysis.fromString(""));
        assertNull(analysis.fromString("  "));
        assertEquals(NomenclaturalCode.BOTANICAL, analysis.fromString("botanical"));
        assertEquals(NomenclaturalCode.ZOOLOGICAL, analysis.fromString("ZOOLOGICAL"));
        assertEquals(NomenclaturalCode.BACTERIAL, analysis.fromString("Bacterial"));
    }


    @Test
    public void testFromString2() throws Exception {
        assertEquals(NomenclaturalCode.BOTANICAL, analysis.fromString("ICN"));
        assertEquals(NomenclaturalCode.ZOOLOGICAL, analysis.fromString("ICZN"));
        assertEquals(NomenclaturalCode.BACTERIAL, analysis.fromString("ICNB"));
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertNull(analysis.equivalent(null, NomenclaturalCode.BACTERIAL));
        assertNull(analysis.equivalent(NomenclaturalCode.BACTERIAL, null));
    }

    @Test
    public void testEquivalent2() throws Exception {
        assertTrue(analysis.equivalent(NomenclaturalCode.ZOOLOGICAL, NomenclaturalCode.ZOOLOGICAL));
        assertTrue(analysis.equivalent(NomenclaturalCode.BACTERIAL, NomenclaturalCode.BACTERIAL));
    }

    @Test
    public void testEquivalent3() throws Exception {
        assertFalse(analysis.equivalent(NomenclaturalCode.VIRUS, NomenclaturalCode.BOTANICAL));
        assertFalse(analysis.equivalent(NomenclaturalCode.ZOOLOGICAL, NomenclaturalCode.VIRUS));
     }

    @Test
    public void testEstimateFromKingdom1() throws Exception {
        assertEquals(null, analysis.estimateFromKingdom(null));
        assertEquals(null, analysis.estimateFromKingdom(""));
        assertEquals(null, analysis.estimateFromKingdom("Oopsie"));
    }

    @Test
    public void testEstimateFromKingdom2() throws Exception {
        assertEquals(NomenclaturalCode.ZOOLOGICAL, analysis.estimateFromKingdom("Animalia"));
        assertEquals(NomenclaturalCode.ZOOLOGICAL, analysis.estimateFromKingdom("ANIMALIA"));
        assertEquals(NomenclaturalCode.ZOOLOGICAL,  analysis.estimateFromKingdom("ANIMALA"));
    }

    @Test
    public void testEstimateFromKingdom3() throws Exception {
        assertEquals(NomenclaturalCode.BOTANICAL, analysis.estimateFromKingdom("Plantae"));
        assertEquals(NomenclaturalCode.BOTANICAL, analysis.estimateFromKingdom("  plantae "));
        assertEquals(NomenclaturalCode.BOTANICAL,  analysis.estimateFromKingdom("PLANTI"));
    }

}
