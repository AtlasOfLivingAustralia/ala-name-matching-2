package au.org.ala.bayesian.analysis;

import au.org.ala.vocab.BayesianTerm;
import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.Term;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for {@TermAnalysis}
 */
public class TermAnalysisTest {
    private TermAnalysis analysis;

    @Before
    public void setUp() throws Exception {
        this.analysis = new TermAnalysis();
        BayesianTerm.values(); // Make sure vocabulary loaded
    }

    @Test
    public void testType1() throws Exception {
        assertEquals(Term.class, this.analysis.getType());
    }

    @Test
    public void testStoreType1() throws Exception {
        assertEquals(String.class, this.analysis.getStoreType());
    }

    @Test
    public void testAnalyse1() throws Exception {
        assertEquals(BayesianTerm.name, this.analysis.analyse(BayesianTerm.name));
        assertEquals(null, this.analysis.analyse(null));
    }

    @Test
    public void testFromString1() throws Exception {
        assertEquals(DcTerm.identifier, this.analysis.fromString("dcterms:identifier"));
        assertEquals(BayesianTerm.altName, this.analysis.fromString("bayesian:altName"));
        assertEquals(BayesianTerm.altName, this.analysis.fromString("http://id.ala.org.au/bayesian/1.0/altName"));
        assertEquals(null, this.analysis.fromString(null));
        assertEquals(null, this.analysis.fromString(""));
    }

    @Test
    public void testFromStore1() throws Exception {
        assertEquals(BayesianTerm.accepted, this.analysis.fromStore("accepted"));
        assertEquals(BayesianTerm.parent, this.analysis.fromStore("bayesian:parent"));
        assertEquals(BayesianTerm.Concept, this.analysis.fromStore("http://id.ala.org.au/bayesian/1.0/Concept"));
        assertEquals(null, this.analysis.fromStore(""));
        assertEquals(null, this.analysis.fromStore(null));
    }

    @Test
    public void testToStore1() throws Exception {
        assertEquals("http://id.ala.org.au/bayesian/1.0/isSynonym", this.analysis.toStore(BayesianTerm.isSynonym));
        assertEquals("http://id.ala.org.au/bayesian/1.0/weight", this.analysis.toStore(BayesianTerm.weight));
        assertEquals(null, this.analysis.toStore(null));
    }

    @Test
    public void testToQuery1() throws Exception {
        assertEquals("http://id.ala.org.au/bayesian/1.0/additional", this.analysis.toQuery(BayesianTerm.additional));
        assertEquals("http://id.ala.org.au/bayesian/1.0/copy", this.analysis.toQuery(BayesianTerm.copy));
        assertEquals(null, this.analysis.toQuery(null));
    }

    @Test
    public void testToEquals1() throws Exception {
        Object that = new TermAnalysis();
        assertTrue(this.analysis.equals(that));
        that = new Object();
        assertFalse(this.analysis.equals(that));
    }

    @Test
    public void testHashCode1() throws Exception {
        Object that = new TermAnalysis();
        assertEquals(that.hashCode(), this.analysis.hashCode());
        that = new Object();
        assertNotEquals(that.hashCode(), this.analysis.hashCode());
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertTrue(this.analysis.equivalent(BayesianTerm.parent, BayesianTerm.parent));
        assertTrue(this.analysis.equivalent(BayesianTerm.fullName, BayesianTerm.fullName));
        assertFalse(this.analysis.equivalent(BayesianTerm.analysisMethod, BayesianTerm.isRoot));
        assertNull(this.analysis.equivalent(BayesianTerm.invalidMatch, null));
        assertNull(this.analysis.equivalent(null, BayesianTerm.analyserClass));
    }



}
