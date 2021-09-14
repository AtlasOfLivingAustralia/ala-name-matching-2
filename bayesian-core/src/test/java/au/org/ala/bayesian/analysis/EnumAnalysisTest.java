package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observable.Multiplicity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for {@EnumAnalysis}
 */
public class EnumAnalysisTest {
    private EnumAnalysis<Multiplicity> analysis;

    @Before
    public void setUp() throws Exception {
        this.analysis = new EnumAnalysis<>(Multiplicity.class);
    }

    @Test
    public void testType1() throws Exception {
        assertEquals(Multiplicity.class, this.analysis.getType());
    }

    @Test
    public void testStoreType1() throws Exception {
        assertEquals(String.class, this.analysis.getStoreType());
    }

    @Test
    public void testAnalyse1() throws Exception {
        assertEquals(Multiplicity.OPTIONAL, this.analysis.analyse(Multiplicity.OPTIONAL));
        assertEquals(null, this.analysis.analyse(null));
    }

    @Test
    public void testFromString1() throws Exception {
        assertEquals(Multiplicity.OPTIONAL, this.analysis.fromString("optional"));
        assertEquals(Multiplicity.MANY, this.analysis.fromString("MANY"));
        assertEquals(null, this.analysis.fromString(null));
        assertEquals(null, this.analysis.fromString(""));
    }

    @Test
    public void testFromStore1() throws Exception {
        assertEquals(Multiplicity.OPTIONAL, this.analysis.fromStore("optional"));
        assertEquals(Multiplicity.MANY, this.analysis.fromStore("MANY"));
        assertEquals(null, this.analysis.fromStore(null));
    }

    @Test
    public void testToStore1() throws Exception {
        assertEquals("optional", this.analysis.toStore(Multiplicity.OPTIONAL));
        assertEquals("required", this.analysis.toStore(Multiplicity.REQUIRED));
        assertEquals(null, this.analysis.toStore(null));
    }

    @Test
    public void testToQuery1() throws Exception {
        assertEquals("optional", this.analysis.toQuery(Multiplicity.OPTIONAL));
        assertEquals("required", this.analysis.toQuery(Multiplicity.REQUIRED));
        assertEquals(null, this.analysis.toQuery(null));
    }

    @Test
    public void testToEquals1() throws Exception {
        Object that = new EnumAnalysis(Multiplicity.class);
        assertTrue(this.analysis.equals(that));
        that = new EnumAnalysis(Observable.Style.class);
        assertFalse(this.analysis.equals(that));
        that = new Object();
        assertFalse(this.analysis.equals(that));
    }

    @Test
    public void testHashCode1() throws Exception {
        Object that = new EnumAnalysis(Multiplicity.class);
        assertEquals(that.hashCode(), this.analysis.hashCode());
        that = new EnumAnalysis(Observable.Style.class);
        assertNotEquals(that.hashCode(), this.analysis.hashCode());
        that = new Object();
        assertNotEquals(that.hashCode(), this.analysis.hashCode());
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertTrue(this.analysis.equivalent(Multiplicity.MANY, Multiplicity.MANY));
        assertFalse(this.analysis.equivalent(Multiplicity.REQUIRED, Multiplicity.MANY));
        assertNull(this.analysis.equivalent(Multiplicity.OPTIONAL, null));
        assertNull(this.analysis.equivalent(null,Multiplicity.REQUIRED_MANY));
    }



}
