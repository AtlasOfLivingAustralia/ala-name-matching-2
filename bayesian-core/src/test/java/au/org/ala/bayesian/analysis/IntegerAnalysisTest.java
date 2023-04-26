package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Fidelity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for {@IntegerAnalysis}
 */
public class IntegerAnalysisTest {
    private IntegerAnalysis analysis;

    @Before
    public void setUp() throws Exception {
        this.analysis = new IntegerAnalysis();
    }

    @Test
    public void testType1() throws Exception {
        assertEquals(Integer.class, this.analysis.getType());
    }

    @Test
    public void testStoreType1() throws Exception {
        assertEquals(Integer.class, this.analysis.getStoreType());
    }

    @Test
    public void testAnalyse1() throws Exception {
        assertEquals(10, this.analysis.analyse(10).intValue());
        assertEquals(null, this.analysis.analyse(null));
    }

    @Test
    public void testFromString1() throws Exception {
        assertEquals(10, this.analysis.fromString("10").intValue());
        assertEquals(-150, this.analysis.fromString("-150").intValue());
        assertEquals(null, this.analysis.fromString(null));
        assertEquals(null, this.analysis.fromString(""));
    }

    @Test
    public void testFromStore1() throws Exception {
        assertEquals(10, this.analysis.fromStore(10).intValue());
        assertEquals(-150, this.analysis.fromStore(-150).intValue());
        assertEquals(null, this.analysis.fromStore(null));
    }

    @Test
    public void testToStore1() throws Exception {
        assertEquals(10, this.analysis.toStore(10).intValue());
        assertEquals(-150, this.analysis.toStore(-150).intValue());
        assertEquals(null, this.analysis.toStore(null));
    }

    @Test
    public void testToQuery1() throws Exception {
        assertEquals(10, this.analysis.toQuery(10).intValue());
        assertEquals(-150, this.analysis.toQuery(-150).intValue());
        assertEquals(null, this.analysis.toQuery(null));
    }

    @Test
    public void testToEquals1() throws Exception {
        Object that = new IntegerAnalysis();
        assertTrue(this.analysis.equals(that));
        that = new Object();
        assertFalse(this.analysis.equals(that));
    }

    @Test
    public void testHashCode1() throws Exception {
        Object that = new IntegerAnalysis();
        assertEquals(that.hashCode(), this.analysis.hashCode());
        that = new Object();
        assertNotEquals(that.hashCode(), this.analysis.hashCode());
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertTrue(this.analysis.equivalent(11, 11));
        assertTrue(this.analysis.equivalent(229, 229));
        assertFalse(this.analysis.equivalent(11, 229));
        assertNull(this.analysis.equivalent(11, null));
        assertNull(this.analysis.equivalent(null, 5));
    }

    public void testBuildFidelity1() throws Exception {
        Fidelity<Integer> fidelity = this.analysis.buildFidelity(1, 1);
        assertNotNull(fidelity);
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity2() throws Exception {
        Fidelity<Integer> fidelity = this.analysis.buildFidelity(1, 10);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity3() throws Exception {
        Fidelity<Integer> fidelity = this.analysis.buildFidelity(1, 2);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity4() throws Exception {
        Fidelity<Integer> fidelity = this.analysis.buildFidelity(null, 1);
        assertNull(fidelity);
    }

    public void testBuildFidelity5() throws Exception {
        Fidelity<Integer> fidelity = this.analysis.buildFidelity(1, null);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }



}
