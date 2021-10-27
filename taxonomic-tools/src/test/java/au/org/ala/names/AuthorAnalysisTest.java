package au.org.ala.names;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuthorAnalysisTest {
    private AuthorAnalysis analysis = new AuthorAnalysis();

    @Test
    public void testEquivalence1() throws Exception {
        assertTrue(this.analysis.equivalent("Jones", "Jones"));
        assertTrue(this.analysis.equivalent("Linnaeus", "L."));
        assertTrue(this.analysis.equivalent("A.Cabrera", "Ángel Cabrera"));
    }

    @Test
    public void testEquivalence2() throws Exception {
        assertTrue(this.analysis.equivalent("Jones", "Jonesey")); // Close enough
        assertFalse(this.analysis.equivalent("Linnaeus", "Shwartz"));
        assertFalse(this.analysis.equivalent("Caery", "Karey"));
        assertTrue(this.analysis.equivalent("A.Cabrera", "A.Cabre"));
    }

    @Test
    public void testEquivalence3() throws Exception {
        assertNull(this.analysis.equivalent("Jones", null));
        assertNull(this.analysis.equivalent(null, "L."));
    }

    @Test
    public void testEquivalence4() throws Exception {
        assertTrue(this.analysis.equivalent("Jones", "Jones, 1975"));
        assertTrue(this.analysis.equivalent("Jones", "(Jones)"));
        assertTrue(this.analysis.equivalent("Jones", "(Jones, 1975)"));
        assertTrue(this.analysis.equivalent("(Jones)", "(Jones, 1975)"));
        assertTrue(this.analysis.equivalent("(Jones)", "Jones, 1975"));
   }


    @Test
    public void testEquivalence5() throws Exception {
        assertTrue(this.analysis.equivalent("(Jones, 1975)", "Jones, 1975"));
        assertTrue(this.analysis.equivalent("Jones, 1976", "Jones, 1975"));
        assertTrue(this.analysis.equivalent("Alias, Smith & Jones, 1933", "A.Alias, S.Smith, J.Jones, 1933"));
        assertTrue(this.analysis.equivalent("Alias, Smith & Jones, 1933", "(A.Alias, S.Smith, J.Jones)"));
    }

    @Test
    public void testEquivalence6() throws Exception {
        assertFalse(this.analysis.equivalent("Carpenter, 1881", "L. Agassiz, 1836"));
        assertTrue(this.analysis.equivalent("Carpenter, 1881", "Carpenter (1881)"));
        assertTrue(this.analysis.equivalent("Carpenter, 1881", "Carpenter ms 1881?"));
        assertTrue(this.analysis.equivalent("Carpenter, 1881", "Carpenter 1881 [1882?]"));
    }

}
