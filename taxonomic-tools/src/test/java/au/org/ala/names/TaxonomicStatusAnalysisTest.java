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
    public void testToString1() throws Exception {
        assertNull(analysis.toString(null));
        assertEquals("accepted", analysis.toString(TaxonomicStatus.accepted));
        assertEquals("inferredAccepted", analysis.toString(TaxonomicStatus.inferredAccepted));
        assertEquals("inferredAccepted", analysis.toString(TaxonomicStatus.inferredAccepted));
        assertEquals("homotypicSynonym", analysis.toString(TaxonomicStatus.homotypicSynonym));
    }

    @Test
    public void testFromString1() throws Exception {
        assertNull(analysis.fromString(null));
        assertNull(analysis.fromString(""));
        assertNull(analysis.fromString("  "));
        assertEquals(TaxonomicStatus.accepted, analysis.fromString("accepted"));
        assertEquals(TaxonomicStatus.accepted, analysis.fromString("ACCEPTED"));
        assertEquals(TaxonomicStatus.accepted, analysis.fromString(" Accepted "));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString("heterotypicSynonym"));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString("HeterotypicSynonym"));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString("heterotypic synonym"));
        assertEquals(TaxonomicStatus.heterotypicSynonym, analysis.fromString(" Heterotypic Synonym "));
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
