package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Fidelity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for {@DoubleAnalysis}
 */
public class DoubleAnalysisTest {
    private DoubleAnalysis analysis;

    @Before
    public void setUp() throws Exception {
        this.analysis = new DoubleAnalysis();
    }

    @Test
    public void testType1() throws Exception {
        assertEquals(Double.class, this.analysis.getType());
    }

    @Test
    public void testStoreType1() throws Exception {
        assertEquals(Double.class, this.analysis.getStoreType());
    }

    @Test
    public void testAnalyse1() throws Exception {
        assertEquals(10.0, this.analysis.analyse(10.0), 0.00001);
        assertEquals(null, this.analysis.analyse(null));
    }

    @Test
    public void testFromString1() throws Exception {
        assertEquals(10.0, this.analysis.fromString("10.000"), 0.00001);
        assertEquals(-150.0, this.analysis.fromString("-150.000"), 0.00001);
        assertEquals(null, this.analysis.fromString(null));
        assertEquals(null, this.analysis.fromString(""));
    }

    @Test
    public void testFromStore1() throws Exception {
        assertEquals(10.0, this.analysis.fromStore(10.0), 0.00001);
        assertEquals(-150.0, this.analysis.fromStore(-150.0), 0.00001);
        assertEquals(null, this.analysis.fromStore(null));
    }

    @Test
    public void testToStore1() throws Exception {
        assertEquals(10.0, this.analysis.toStore(10.0), 0.00001);
        assertEquals(-150.0, this.analysis.toStore(-150.0), 0.00001);
        assertEquals(null, this.analysis.toStore(null));
    }

    @Test
    public void testToQuery1() throws Exception {
        assertEquals(10.0, this.analysis.toQuery(10.0), 0.00001);
        assertEquals(-150.0, this.analysis.toQuery(-150.0), 0.00001);
        assertEquals(null, this.analysis.toQuery(null));
    }

    @Test
    public void testToEquals1() throws Exception {
        Object that = new DoubleAnalysis();
        assertTrue(this.analysis.equals(that));
        that = new Object();
        assertFalse(this.analysis.equals(that));
    }

    @Test
    public void testHashCode1() throws Exception {
        Object that = new DoubleAnalysis();
        assertEquals(that.hashCode(), this.analysis.hashCode());
        that = new Object();
        assertNotEquals(that.hashCode(), this.analysis.hashCode());
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertTrue(this.analysis.equivalent(10.0, 10.0));
        assertTrue(this.analysis.equivalent(22.9, 22.9));
        assertFalse(this.analysis.equivalent(10.0, 22.9));
        assertNull(this.analysis.equivalent(10.0, null));
        assertNull(this.analysis.equivalent(null, 5.0));
    }

    public void testBuildFidelity1() throws Exception {
        Fidelity<Double> fidelity = this.analysis.buildFidelity(1.0, 1.0);
        assertNotNull(fidelity);
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity2() throws Exception {
        Fidelity<Double> fidelity = this.analysis.buildFidelity(1.0, 10.0);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity3() throws Exception {
        Fidelity<Double> fidelity = this.analysis.buildFidelity(1.0, 1.5);
        assertNotNull(fidelity);
        assertEquals(0.4, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity4() throws Exception {
        Fidelity<Double> fidelity = this.analysis.buildFidelity(null, 1.5);
        assertNull(fidelity);
     }

    public void testBuildFidelity5() throws Exception {
        Fidelity<Double> fidelity = this.analysis.buildFidelity(1.0, null);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

}
