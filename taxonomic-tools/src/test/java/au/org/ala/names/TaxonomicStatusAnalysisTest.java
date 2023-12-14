package au.org.ala.names;

import au.org.ala.vocab.TaxonomicStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaxonomicStatusAnalysisTest {
    TaxonomicStatusAnalysis analysis = new TaxonomicStatusAnalysis();

    @Test
    public void testType1() {
        assertEquals(TaxonomicStatus.class, analysis.getType());
    }

    @Test
    public void testAnalyse1() throws Exception{
        assertNull(analysis.analyse(null));
    }

    @Test
    public void testAnalyse2() throws Exception {
        assertEquals(TaxonomicStatus.accepted, analysis.analyse(TaxonomicStatus.accepted));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.analyse(TaxonomicStatus.heterotypicSynonym));
        assertEquals(TaxonomicStatus.unknown, analysis.analyse(TaxonomicStatus.unknown));
    }

    @Test
    public void testToStore1() throws Exception {
        assertNull(analysis.toStore(null));
        assertEquals("accepted", analysis.toStore(TaxonomicStatus.accepted));
        assertEquals("inferredAccepted", analysis.toStore(TaxonomicStatus.inferredAccepted));
        assertEquals("inferredAccepted", analysis.toStore(TaxonomicStatus.inferredAccepted));
        assertEquals("homotypicSynonym", analysis.toStore(TaxonomicStatus.homotypicSynonym));
    }

    @Test
    public void testFromString1() throws Exception {
        assertNull(analysis.fromString(null, null));
        assertNull(analysis.fromString("", null));
        assertNull(analysis.fromString("  ", null));
        assertEquals(TaxonomicStatus.accepted, analysis.fromString("accepted", null));
        assertEquals(TaxonomicStatus.accepted, analysis.fromString("ACCEPTED", null));
        assertEquals(TaxonomicStatus.accepted, analysis.fromString(" Accepted ", null));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString("heterotypicSynonym", null));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString("HeterotypicSynonym", null));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString("heterotypic synonym", null));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString(" Heterotypic Synonym ", null));
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertNull(analysis.equivalent(null, TaxonomicStatus.accepted));
        assertNull(analysis.equivalent(TaxonomicStatus.inferredExcluded, null));
    }

    @Test
    public void testEquivalent2() throws Exception {
        assertTrue(analysis.equivalent(TaxonomicStatus.synonym, TaxonomicStatus.synonym));
        assertTrue(analysis.equivalent(TaxonomicStatus.objectiveSynonym, TaxonomicStatus.objectiveSynonym));
        assertTrue(analysis.equivalent(TaxonomicStatus.objectiveSynonym, TaxonomicStatus.homotypicSynonym));
        assertTrue(analysis.equivalent(TaxonomicStatus.accepted, TaxonomicStatus.inferredAccepted));
    }

    @Test
    public void testEquivalent3() throws Exception {
        assertFalse(analysis.equivalent(TaxonomicStatus.synonym, TaxonomicStatus.inferredAccepted));
        assertFalse(analysis.equivalent(TaxonomicStatus.inferredExcluded, TaxonomicStatus.inferredAccepted));
    }


}
