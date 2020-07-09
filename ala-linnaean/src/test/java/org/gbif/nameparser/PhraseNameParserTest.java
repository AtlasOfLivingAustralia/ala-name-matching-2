

package org.gbif.nameparser;

import au.org.ala.names.model.ALAParsedName;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.api.vocabulary.NameType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for the phrase name parser.
 * <p>
 * Apart from a few tests to make sure that the parser is not screwing up "normal" parsing,
 * these tests concentrate on phrase names and placeholders.
 * </p>
 */
public class PhraseNameParserTest {
    private PhraseNameParser parser;

    @Before
    public void setup() throws Exception {
        this.parser = new PhraseNameParser();
    }

    @Test
    public void testSimpleParse1() throws Exception {
        ParsedName pn = parser.parse("Ozothamnus diosmifolius");
        assertFalse(pn instanceof ALAParsedName);
        assertEquals("Ozothamnus", pn.getGenusOrAbove());
        assertEquals("diosmifolius", pn.getSpecificEpithet());
        assertEquals(NameType.SCIENTIFIC, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("Ozothamnus diosmifolius", pn.canonicalName());
    }

    @Test
    public void testSimpleParse2() throws Exception {
        ParsedName pn = parser.parse("Ozothamnus");
        assertFalse(pn instanceof ALAParsedName);
        assertEquals("Ozothamnus", pn.getGenusOrAbove());
        assertNull(pn.getSpecificEpithet());
        assertEquals(NameType.SCIENTIFIC, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("Ozothamnus", pn.canonicalName());
    }

    @Test
    public void testSpeciesPlaceholder1() throws Exception {
        ParsedName pn = parser.parse("Diaporthe species1");
        assertFalse(pn instanceof ALAParsedName);
        assertEquals("Diaporthe", pn.getGenusOrAbove());
        assertEquals("species1", pn.getSpecificEpithet());
        assertEquals(NameType.PLACEHOLDER, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("Diaporthe species1", pn.canonicalName());
    }

    @Test
    public void testSpeciesPlaceholder2() throws Exception {
        ParsedName pn = parser.parse("Diaporthe species 1");
        assertFalse(pn instanceof ALAParsedName);
        assertEquals("Diaporthe", pn.getGenusOrAbove());
        assertEquals("species-1", pn.getSpecificEpithet());
        assertEquals(NameType.PLACEHOLDER, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("Diaporthe species-1", pn.canonicalName());
    }

    @Test
    public void testSpeciesPlaceholder3() throws Exception {
        ParsedName pn = parser.parse("Diaporthe species-1");
        assertFalse(pn instanceof ALAParsedName);
        assertEquals("Diaporthe", pn.getGenusOrAbove());
        assertEquals("species-1", pn.getSpecificEpithet());
        assertEquals(NameType.PLACEHOLDER, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("Diaporthe species-1", pn.canonicalName());
    }

    @Test
    public void testPhraseName1() throws Exception {
        ParsedName pn = parser.parse("Dryandra sp. 1 (A.S.George 16647)");
        assertTrue(pn instanceof ALAParsedName);
        assertEquals("Dryandra", pn.getGenusOrAbove());
        assertEquals(NameType.INFORMAL, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("(A.S.George 16647)", ((ALAParsedName) pn).getPhraseVoucher());
        assertEquals("1", ((ALAParsedName) pn).getLocationPhraseDescription());
        assertEquals("Dryandra 1", pn.canonicalName());
    }

    @Test
    public void testPhraseName2() throws Exception {
        ParsedName pn = parser.parse("Cryptandra sp. 'West Bald Rock' (W.J.McDonald 925)");
        assertTrue(pn instanceof ALAParsedName);
        assertEquals("Cryptandra", pn.getGenusOrAbove());
        assertEquals(NameType.CULTIVAR, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("(W.J.McDonald 925)", ((ALAParsedName) pn).getPhraseVoucher());
        assertEquals("'West Bald Rock'", ((ALAParsedName) pn).getLocationPhraseDescription());
        assertEquals("Cryptandra cv.", pn.canonicalName());
    }

    @Test
    public void testPhraseName3() throws Exception {
        ParsedName pn = parser.parse("Diuris sp. 'Brigooda' (W.Power s.n. Sept 1954)");
        assertTrue(pn instanceof ALAParsedName);
        assertEquals("Diuris", pn.getGenusOrAbove());
        assertEquals(NameType.CULTIVAR, pn.getType());
        assertNull(pn.getAuthorship());
        assertEquals("(W.Power s.n. Sept 1954)", ((ALAParsedName) pn).getPhraseVoucher());
        assertEquals("'Brigooda'", ((ALAParsedName) pn).getLocationPhraseDescription());
        assertEquals("Diuris cv.", pn.canonicalName());
    }

}
