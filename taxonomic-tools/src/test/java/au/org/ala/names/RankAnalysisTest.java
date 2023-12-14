package au.org.ala.names;

import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.Rank;
import org.junit.Test;

import static org.junit.Assert.*;

public class RankAnalysisTest {
    RankAnalysis analysis = new RankAnalysis();

    @Test
    public void testType1() {
        assertEquals(Rank.class, analysis.getType());
    }

    @Test
    public void testAnalyse1() throws Exception{
        assertNull(analysis.analyse(null));
        assertNull(analysis.analyse(Rank.UNRANKED));
        assertNull(analysis.analyse(Rank.OTHER));
    }

    @Test
    public void testAnalyse2() throws Exception {
        assertEquals(Rank.SPECIES, analysis.analyse(Rank.SPECIES));
        assertEquals(Rank.CLASS, analysis.analyse(Rank.CLASS));
    }

    @Test
    public void testAnalyse3() throws Exception {
        assertEquals(Rank.SUBSPECIES, analysis.analyse(Rank.SUBSPECIES));
        assertEquals(Rank.VARIETY, analysis.analyse(Rank.VARIETY));
    }

    @Test
    public void testToStore1() throws Exception {
        assertNull(analysis.toStore(null));
        assertEquals("class", analysis.toStore(Rank.CLASS));
        assertEquals("subfamily", analysis.toStore(Rank.SUBFAMILY));
    }

    @Test
    public void testFromString1() throws Exception {
        assertNull(analysis.fromString(null, null));
        assertNull(analysis.fromString("", null));
        assertNull(analysis.fromString("  ", null));
        assertNull(analysis.fromString(null, NomenclaturalCode.BOTANICAL));
        assertNull(analysis.fromString("", NomenclaturalCode.ZOOLOGICAL));
        assertNull(analysis.fromString("  ", NomenclaturalCode.BACTERIAL));
    }

    @Test
    public void testFromString2() throws Exception {
        assertEquals(Rank.CLASS, analysis.fromString("class", null));
        assertEquals(Rank.CLASS, analysis.fromString("CLASS", null));
        assertEquals(Rank.FAMILY, analysis.fromString("Family", null));
        assertEquals(Rank.SPECIES, analysis.fromString("sp", null));
        assertEquals(Rank.DIVISION, analysis.fromString("division", null));
        assertEquals(Rank.CLASS, analysis.fromString("class", NomenclaturalCode.BOTANICAL));
        assertEquals(Rank.CLASS, analysis.fromString("CLASS", NomenclaturalCode.ZOOLOGICAL));
        assertEquals(Rank.FAMILY, analysis.fromString("Family", NomenclaturalCode.ZOOLOGICAL));
        assertEquals(Rank.SPECIES, analysis.fromString("sp", NomenclaturalCode.BACTERIAL));
        assertEquals(Rank.DIVISION, analysis.fromString("division", NomenclaturalCode.BOTANICAL));
    }


    @Test
    public void testFromString3() throws Exception {
        assertEquals(Rank.PHYLUM, analysis.fromString("divisio", null));
        assertEquals(Rank.CLASS, analysis.fromString("classis", null));
        assertEquals(Rank.KINGDOM, analysis.fromString("regnum", null));
        assertEquals(Rank.PHYLUM, analysis.fromString("divisio", NomenclaturalCode.BOTANICAL));
        assertEquals(Rank.CLASS, analysis.fromString("classis", NomenclaturalCode.BOTANICAL));
        assertEquals(Rank.KINGDOM, analysis.fromString("regnum", NomenclaturalCode.BOTANICAL));
    }


    @Test
    public void testFromString4() throws Exception {
        assertEquals(null, analysis.fromString("sect.", null));
        assertEquals(Rank.SECTION_BOTANY, analysis.fromString("sect.", NomenclaturalCode.BOTANICAL));
        assertEquals(Rank.SECTION_ZOOLOGY, analysis.fromString("sect.", NomenclaturalCode.ZOOLOGICAL));
        assertEquals(null, analysis.fromString("sect.", NomenclaturalCode.VIRUS));
    }

    @Test
    public void testFromString5() throws Exception {
        assertEquals(Rank.SUPERSERIES, analysis.fromString("superseries", null));
        assertEquals(Rank.SUPERSERIES, analysis.fromString("superseries", NomenclaturalCode.BOTANICAL));
        assertEquals(Rank.SUPERSERIES, analysis.fromString("superseries", NomenclaturalCode.ZOOLOGICAL));
        assertEquals(null, analysis.fromString("superser.", null));
        assertEquals(null, analysis.fromString("superser.", NomenclaturalCode.BOTANICAL));
        assertEquals(Rank.SUPERSERIES, analysis.fromString("superser.", NomenclaturalCode.ZOOLOGICAL));
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertNull(analysis.equivalent(null, Rank.FAMILY));
        assertNull(analysis.equivalent(Rank.FAMILY, null));
    }

    @Test
    public void testEquivalent2() throws Exception {
        assertTrue(analysis.equivalent(Rank.FAMILY, Rank.FAMILY));
        assertTrue(analysis.equivalent(Rank.SPECIES, Rank.SPECIES));
    }

    @Test
    public void testEquivalent3() throws Exception {
        assertTrue(analysis.equivalent(Rank.FAMILY, Rank.SUBFAMILY));
        assertTrue(analysis.equivalent(Rank.GENUS, Rank.SUBGENUS));
        assertTrue(analysis.equivalent(Rank.GENUS, Rank.INFRAFAMILY));
        assertTrue(analysis.equivalent(Rank.SUBSPECIES, Rank.FORM));
    }

    @Test
    public void testEquivalent4() throws Exception {
        assertFalse(analysis.equivalent(Rank.FAMILY, Rank.GENUS));
        assertFalse(analysis.equivalent(Rank.CLASS, Rank.SUBGENUS));
        assertFalse(analysis.equivalent(Rank.INFRAGENUS, Rank.SUBFAMILY));
     }


}
