package au.org.ala.bayesian.analysis;

import au.org.ala.bayesian.Fidelity;
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
        assertEquals(DcTerm.identifier, this.analysis.fromString("dcterms:identifier", null));
        assertEquals(BayesianTerm.altName, this.analysis.fromString("bayesian:altName", null));
        assertEquals(BayesianTerm.altName, this.analysis.fromString("http://ala.org.au/bayesian/1.0/altName", null));
        assertEquals(null, this.analysis.fromString(null, null));
        assertEquals(null, this.analysis.fromString("", null));
    }

    @Test
    public void testFromStore1() throws Exception {
        assertEquals(BayesianTerm.accepted, this.analysis.fromStore("accepted"));
        assertEquals(BayesianTerm.parent, this.analysis.fromStore("bayesian:parent"));
        assertEquals(BayesianTerm.Concept, this.analysis.fromStore("http://ala.org.au/bayesian/1.0/Concept"));
        assertEquals(null, this.analysis.fromStore(""));
        assertEquals(null, this.analysis.fromStore(null));
    }

    @Test
    public void testToStore1() throws Exception {
        assertEquals("http://ala.org.au/bayesian/1.0/isSynonym", this.analysis.toStore(BayesianTerm.isSynonym));
        assertEquals("http://ala.org.au/bayesian/1.0/weight", this.analysis.toStore(BayesianTerm.weight));
        assertEquals(null, this.analysis.toStore(null));
    }

    @Test
    public void testToQuery1() throws Exception {
        assertEquals("http://ala.org.au/bayesian/1.0/additional", this.analysis.toQuery(BayesianTerm.additional));
        assertEquals("http://ala.org.au/bayesian/1.0/copy", this.analysis.toQuery(BayesianTerm.copy));
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


    public void testBuildFidelity1() throws Exception {
        Fidelity<Term> fidelity = this.analysis.buildFidelity(BayesianTerm.analysisMethod, BayesianTerm.analysisMethod);
        assertNotNull(fidelity);
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }

    public void testBuildFidelity2() throws Exception {
        Fidelity<Term> fidelity = this.analysis.buildFidelity(BayesianTerm.analysisMethod, BayesianTerm.isRoot);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }


    public void testBuildFidelity3() throws Exception {
        Fidelity<Term> fidelity = this.analysis.buildFidelity(null, BayesianTerm.isRoot);
        assertNull(fidelity);
    }

    public void testBuildFidelity4() throws Exception {
        Fidelity<Term> fidelity = this.analysis.buildFidelity(BayesianTerm.isSynonym, null);
        assertNotNull(fidelity);
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

}
