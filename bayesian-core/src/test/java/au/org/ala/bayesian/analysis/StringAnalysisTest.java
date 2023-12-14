package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Fidelity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for {@StringAnalysis}
 */
public class StringAnalysisTest {
    private StringAnalysis analysis;

    @Before
    public void setUp() throws Exception {
        this.analysis = new StringAnalysis();
    }

    @Test
    public void testType1() throws Exception {
        assertEquals(String.class, this.analysis.getType());
    }

    @Test
    public void testStoreType1() throws Exception {
        assertEquals(String.class, this.analysis.getStoreType());
    }

    @Test
    public void testAnalyse1() throws Exception {
        assertEquals("hello", this.analysis.analyse("hello"));
        assertEquals(null, this.analysis.analyse(null));
    }

    @Test
    public void testFromString1() throws Exception {
        assertEquals("BING", this.analysis.fromString("BING", null));
        assertEquals("bong", this.analysis.fromString("bong", null));
        assertEquals(null, this.analysis.fromString(null, null));
        assertEquals(null, this.analysis.fromString("", null));
    }

    @Test
    public void testFromStore1() throws Exception {
        assertEquals("bing", this.analysis.fromStore("bing"));
        assertEquals("BONG", this.analysis.fromStore("BONG"));
        assertEquals(null, this.analysis.fromStore(null));
    }

    @Test
    public void testToStore1() throws Exception {
        assertEquals("bing", this.analysis.toStore("bing"));
        assertEquals("BONG", this.analysis.toStore("BONG"));
        assertEquals(null, this.analysis.toStore(null));
    }

    @Test
    public void testToQuery1() throws Exception {
        assertEquals("bing", this.analysis.toQuery("bing"));
        assertEquals("BONG", this.analysis.toQuery("BONG"));
        assertEquals(null, this.analysis.toQuery(null));
    }

    @Test
    public void testToEquals1() throws Exception {
        Object that = new StringAnalysis();
        assertTrue(this.analysis.equals(that));
        that = new Object();
        assertFalse(this.analysis.equals(that));
    }

    @Test
    public void testHashCode1() throws Exception {
        Object that = new StringAnalysis();
        assertEquals(that.hashCode(), this.analysis.hashCode());
        that = new Object();
        assertNotEquals(that.hashCode(), this.analysis.hashCode());
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertTrue(this.analysis.equivalent("bing", "bing"));
        assertTrue(this.analysis.equivalent("Bong", "BONG"));
        assertFalse(this.analysis.equivalent("bing", "bong"));
        assertNull(this.analysis.equivalent("bong", null));
        assertNull(this.analysis.equivalent(null, "bing"));
    }

    public void testBuildFidelity1() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity("One", "One");
        assertNotNull(fidelity);
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity2() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity("One", "ONE");
        assertNotNull(fidelity);
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity3() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity("One", "two");
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity4() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity("one", "Ome");
        assertNotNull(fidelity);
        assertEquals(0.33333, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity5() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity("Oneee", "One");
        assertNotNull(fidelity);
        assertEquals(0.6, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity6() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity("One", "Oneee");
        assertNotNull(fidelity);
        assertEquals(0.6, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity7() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity(null, "Hello");
        assertNull(fidelity);
    }

    public void testBuildFidelity8() throws Exception {
        Fidelity<String> fidelity = this.analysis.buildFidelity("Hello", null);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }



}
