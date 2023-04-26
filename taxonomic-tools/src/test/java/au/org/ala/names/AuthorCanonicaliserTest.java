package au.org.ala.names;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AuthorCanonicaliserTest {
    private AuthorCanonicaliser canonicaliser = new AuthorCanonicaliser();
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCanonicalise1() {
        assertEquals("Latreille 1831", this.canonicaliser.canonicalise("Latreille, 1831"));
    }

    @Test
    public void testCanonicalise2() {
        assertEquals("Whittaker Margulis", this.canonicaliser.canonicalise("Whittaker & Margulis"));
    }

    @Test
    public void testCanonicalise3() {
        assertEquals("Woese +", this.canonicaliser.canonicalise("Woese et al."));
    }

    @Test
    public void testCanonicalise4() {
        assertEquals("CavalierSmith", this.canonicaliser.canonicalise("(ex Stanier 1974) Cavalier-Smith"));
    }

    @Test
    public void testCanonicalise5() {
        assertEquals("Stotler CrandallStotler", this.canonicaliser.canonicalise("Rothm. ex Stotler & Crand.-Stotl."));
    }

    @Test
    public void testCanonicalise6() {
        assertEquals("Jones Richards", this.canonicaliser.canonicalise("M.D.M. Jones & T.A. Richards"));
    }

    @Test
    public void testCanonicalise7() {
        assertEquals("Page Blanton 1985", this.canonicaliser.canonicalise("Page & Blanton, 1985"));
    }

    @Test
    public void testCanonicalise8() {
        assertEquals("Bauer Garnica +", this.canonicaliser.canonicalise("R. Bauer, Garnica, Oberwinkler, Riess, Weiß & Begerow"));
    }

    @Test
    public void testCanonicalise9() {
        assertEquals("Walker Schussler", this.canonicaliser.canonicalise("(C. Walker & A. Schüßler)"));
    }

    @Test
    public void testCanonicalise10() {
        assertEquals("Cronquist Takhtajan +", this.canonicaliser.canonicalise("Cronquist, Takht. & W.Zimm."));
    }

    @Test
    public void testCanonicalise11() {
        assertEquals("Linnaeus", this.canonicaliser.canonicalise("L."));
    }

    @Test
    public void testCanonicalise12() {
        assertEquals("Smirnov + 2005", this.canonicaliser.canonicalise("Smirnov et al., 2005"));
    }

    @Test
    public void testCanonicalise13() {
        assertEquals("Olsufjev Meshcheryakova 1983", this.canonicaliser.canonicalise("(ex Aikimbaev, 1966) Olsufjev and Meshcheryakova, 1983"));
    }

    @Test
    public void testCanonicalise14() {
        assertEquals("Carini 1939", this.canonicaliser.canonicalise("(Carini, 1939) ?"));
    }

    @Test
    public void testCanonicalise15() {
        assertNull(this.canonicaliser.canonicalise("-"));
    }

    // Temp. is treated as a placeholder name by GBIF parser
    @Test
    public void testCanonicalise16() {
        assertEquals("(Temp. & M. Perag.) C. W. Reimer", this.canonicaliser.canonicalise("(Temp. & M. Perag.) C. W. Reimer"));
    }


}