package au.org.ala.names;

import org.junit.Test;

import static org.gbif.api.vocabulary.NomenclaturalStatus.*;
import static org.junit.Assert.*;


public class NomStatusAnalysisTest {
    NomStatusAnalysis analysis = new NomStatusAnalysis();

    @Test
    public void testType1() {
        assertEquals(NomStatus.class, analysis.getType());
    }

    @Test
    public void testAnalyse1() throws Exception{
        assertNull(analysis.analyse(null));
    }

    @Test
    public void testAnalyse2() throws Exception {
        assertEquals(new NomStatus(ABORTED), analysis.analyse(new NomStatus(ABORTED)));
        assertEquals(new NomStatus(CONFUSED), analysis.analyse(new NomStatus(CONFUSED)));
        assertEquals(new NomStatus(DENIED), analysis.analyse(new NomStatus(DENIED)));
    }

    @Test
    public void testToStore1() throws Exception {
        assertNull(analysis.toStore(null));
        assertEquals("nom. cons.", analysis.toStore(new NomStatus(CONSERVED)));
        assertEquals("nom. nud.", analysis.toStore(new NomStatus(NUDUM)));
        assertEquals("nom. obl.", analysis.toStore(new NomStatus(FORGOTTEN)));
        assertEquals("nom. inval.", analysis.toStore(new NomStatus(INVALID)));
        assertEquals("validly_published", analysis.toStore(new NomStatus(VALIDLY_PUBLISHED)));
    }


    @Test
    public void testToStore2() throws Exception {
        assertNull(analysis.toStore(null));
        assertEquals("nom. illeg., nom. cons.", analysis.toStore(new NomStatus(ILLEGITIMATE, CONSERVED)));
        assertEquals("nom. ambig., nom. nud.", analysis.toStore(new NomStatus(AMBIGUOUS, NUDUM)));
     }

    @Test
    public void testFromString1() throws Exception {
        assertNull(analysis.fromString(null, null));
        assertNull(analysis.fromString("", null));
        assertNull(analysis.fromString("  ", null));
        assertEquals(new NomStatus(CONSERVED), analysis.fromString("CONSERVED", null));
        assertEquals(new NomStatus(CONSERVED), analysis.fromString("conserved", null));
    }

    @Test
    public void testFromString2() throws Exception {
        assertEquals(new NomStatus(NEW_SPECIES), analysis.fromString("sp. nov.", null));
        assertEquals(new NomStatus(REPLACEMENT), analysis.fromString("nomen novum", null));
        assertEquals(new NomStatus(REPLACEMENT), analysis.fromString("nom. nov.", null));
        assertEquals(new NomStatus(ILLEGITIMATE), analysis.fromString("nom. illeg.", null));
        assertEquals(new NomStatus(ILLEGITIMATE), analysis.fromString(" nom. illeg. ", null));
        assertEquals(new NomStatus(ILLEGITIMATE), analysis.fromString("Nom. Illeg.", null));
        assertEquals(new NomStatus(ILLEGITIMATE), analysis.fromString(" Nom   Illeg ", null));
    }


    @Test
    public void testFromString3() throws Exception {
        assertEquals(new NomStatus(ALTERNATIVE, CONSERVED), analysis.fromString("nom. altern., nom. cons.", null));
        assertEquals(new NomStatus(ILLEGITIMATE, SUPERFLUOUS), analysis.fromString("nom. illeg., nom. superfl.", null));
    }

    @Test
    public void testEquivalent1() throws Exception {
        assertNull(analysis.equivalent(null, new NomStatus(ILLEGITIMATE)));
        assertNull(analysis.equivalent(new NomStatus(NEW_SPECIES), null));
    }

    @Test
    public void testEquivalent2() throws Exception {
        assertTrue(analysis.equivalent(new NomStatus(AMBIGUOUS), new NomStatus(AMBIGUOUS)));
        assertTrue(analysis.equivalent(new NomStatus(DENIED), new NomStatus(DENIED)));
        assertTrue(analysis.equivalent(new NomStatus(ALTERNATIVE, CONFUSED), new NomStatus(ALTERNATIVE, CONFUSED)));
    }

    @Test
    public void testEquivalent3() throws Exception {
        assertFalse(analysis.equivalent(new NomStatus(AMBIGUOUS), new NomStatus(DENIED)));
        assertFalse(analysis.equivalent(new NomStatus(CONFUSED), new NomStatus(DOUBTFUL)));
        assertFalse(analysis.equivalent(new NomStatus(ALTERNATIVE, CONFUSED), new NomStatus(CONFUSED, ALTERNATIVE)));
    }


}
