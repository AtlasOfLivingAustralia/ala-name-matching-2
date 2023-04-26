package au.org.ala.bayesian.analysis;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for {@StringPrefixAnalysis}
 *
 * Other test cases are taken care of by the string analysis tests
 */
public class StringPrefixAnalysisTest {
    private StringPrefixAnalysis analysis;

    @Before
    public void setUp() throws Exception {
        this.analysis = new StringPrefixAnalysis();
    }

    @Test
    public void testHashCode1() {
        assertEquals(this.analysis.hashCode(), new StringPrefixAnalysis().hashCode());
    }

    @Test
    public void testHashCode2() {
        assertNotEquals(this.analysis.hashCode(), new StringAnalysis().hashCode());
    }

    @Test
    public void testHashCode3() {
        assertNotEquals(this.analysis.hashCode(), new StringPrefixAnalysis(5).hashCode());
    }

    @Test
    public void testEquals1() {
        assertEquals(this.analysis, this.analysis);
        assertFalse(this.analysis.equals(null));
        assertEquals(this.analysis, new StringPrefixAnalysis());
    }


    @Test
    public void testEquals2() {
        assertNotEquals(this, new StringAnalysis());
    }

    @Test
    public void testEquals3() {
        assertNotEquals(this, new StringPrefixAnalysis(5));
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertTrue(this.analysis.equivalent("bing", "bing"));
        assertTrue(this.analysis.equivalent("Bong", "BONG"));
        assertFalse(this.analysis.equivalent("bing", "bong"));
        assertNull(this.analysis.equivalent("bong", null));
        assertNull(this.analysis.equivalent(null, "bing"));
        assertNull(this.analysis.equivalent(null, "bing"));
    }


    @Test
    public void testEquivalent2() throws Exception {
        assertTrue(this.analysis.equivalent("bingo", "bing"));
        assertTrue(this.analysis.equivalent("bang", "Bango"));
        assertTrue(this.analysis.equivalent("Begin", "BEGIN IGIN"));
    }


    @Test
    public void testEquivalent3() throws Exception {
        assertFalse(this.analysis.equivalent("bingo", "bin"));
        assertTrue(this.analysis.equivalent("ban", "Ban"));
        assertFalse(this.analysis.equivalent("bi", "Ba"));
        assertTrue(this.analysis.equivalent("b", "B"));
        assertFalse(this.analysis.equivalent("Be", "BEGIN IGIN"));
    }

}
