package au.org.ala.names;

import org.junit.Test;

import static org.junit.Assert.*;

public class RankIDAnalysisTest {
    RankIDAnalysis analysis = new RankIDAnalysis();

    @Test
    public void testType1() {
        assertEquals(Integer.class, analysis.getType());
    }

    @Test
    public void testAnalyse1() throws Exception{
        assertNull(analysis.analyse(null));
    }

    @Test
    public void testAnalyse2() throws Exception {
        assertEquals(1000, (int) analysis.analyse(1000));
        assertEquals(5000, (int) analysis.analyse(5000));
        assertEquals(-1, (int) analysis.analyse(-1));
    }

    @Test
    public void testAnalyse3() throws Exception {
        assertEquals(5600, (int) analysis.analyse(5600));
        assertEquals(2500, (int) analysis.analyse(2500));
    }

    @Test
    public void testToStore1() throws Exception {
        assertNull(analysis.toStore(null));
        assertEquals(4000, (int) analysis.toStore(4000));
        assertEquals(5300, (int) analysis.toStore(5300));
    }

    @Test
    public void testFromString1() throws Exception {
        assertNull(analysis.fromString(null, null));
        assertNull(analysis.fromString("", null));
        assertNull(analysis.fromString("  ", null));
        assertEquals(3000, (int) analysis.fromString("class", null));
        assertEquals(3000, (int) analysis.fromString("CLASS", null));
        assertEquals(5000, (int) analysis.fromString("Family", null));
        assertEquals(7000, (int) analysis.fromString("sp", null));
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertNull(analysis.equivalent(null, 5000));
        assertNull(analysis.equivalent(5000, null));
    }

    @Test
    public void testEquivalent2() throws Exception {
        assertTrue(analysis.equivalent(5000, 5000));
        assertTrue(analysis.equivalent(7000, 7000));
    }

    @Test
    public void testEquivalent3() throws Exception {
        assertTrue(analysis.equivalent(5000, 5400));
        assertTrue(analysis.equivalent(6000, 6300));
        assertTrue(analysis.equivalent(6000, 5200));
        assertTrue(analysis.equivalent(8000, 8400));
    }

    @Test
    public void testEquivalent4() throws Exception {
        assertFalse(analysis.equivalent(5000, 6000));
        assertFalse(analysis.equivalent(3000, 5200));
        assertFalse(analysis.equivalent(6200, 5100));
     }


}
