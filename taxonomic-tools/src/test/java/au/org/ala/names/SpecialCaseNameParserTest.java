package au.org.ala.names;

import au.org.ala.util.TestUtils;
import org.gbif.nameparser.api.NameType;
import org.gbif.nameparser.api.NomCode;
import org.gbif.nameparser.api.ParsedName;
import org.gbif.nameparser.api.Rank;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.*;

public class SpecialCaseNameParserTest {
    @Test
    public void testSpecialCase1() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-1.csv"));
        ParsedName pn = parser.get("Nothing Special");
        assertNull(pn);
    }

    @Test
    public void testSpecialCase2() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-1.csv"));
        ParsedName pn = parser.get("Tephrosia rosea var. Fortescue creeks (M.I.H.Brooker 2186) WA Herbarium");
        assertNotNull(pn);
        assertFalse(pn.isCandidatus());
        assertNull(pn.getCultivarEpithet());
        assertNull(pn.getCode());
        assertNull(pn.getInfragenericEpithet());
        assertNull(pn.getInfraspecificEpithet());
        assertNull(pn.getNotho());
        assertNull(pn.getUninomial());
        assertFalse(pn.getBasionymAuthorship().exists());
        assertFalse(pn.getCombinationAuthorship().exists());
        assertEquals("WA Herbarium", pn.getNominatingParty());
        assertEquals("Tephrosia", pn.getGenus());
        assertEquals("rosea", pn.getSpecificEpithet());
        assertEquals("Fortescue creeks", pn.getPhrase());
        assertEquals(Rank.VARIETY, pn.getRank());
        assertEquals("M.I.H.Brooker 2186", pn.getVoucher());
        assertEquals(NameType.INFORMAL, pn.getType());
    }

    @Test
    public void testSpecialCase3() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-1.csv"));
        ParsedName pn = parser.get("Tephrosia rosea var. Fortescue creeks (M.I.H.Brooker 2186)");
        assertNotNull(pn);
        assertFalse(pn.isCandidatus());
        assertNull(pn.getCultivarEpithet());
        assertNull(pn.getCode());
        assertNull(pn.getInfragenericEpithet());
        assertNull(pn.getInfraspecificEpithet());
        assertNull(pn.getNotho());
        assertNull(pn.getUninomial());
        assertFalse(pn.getBasionymAuthorship().exists());
        assertFalse(pn.getCombinationAuthorship().exists());
        assertNull(pn.getNominatingParty());
        assertEquals("Tephrosia", pn.getGenus());
        assertEquals("rosea", pn.getSpecificEpithet());
        assertEquals("Fortescue creeks", pn.getPhrase());
        assertEquals(Rank.VARIETY, pn.getRank());
        assertEquals("M.I.H.Brooker 2186", pn.getVoucher());
        assertEquals(NameType.INFORMAL, pn.getType());
    }

    @Test
    public void testSpecialCase4() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-1.csv"));
        ParsedName pn = parser.get("Tephrosia rosea var. Fortescue creeks");
        assertNotNull(pn);
        assertFalse(pn.isCandidatus());
        assertNull(pn.getCultivarEpithet());
        assertNull(pn.getCode());
        assertNull(pn.getInfragenericEpithet());
        assertNull(pn.getInfraspecificEpithet());
        assertNull(pn.getNotho());
        assertNull(pn.getUninomial());
        assertFalse(pn.getBasionymAuthorship().exists());
        assertFalse(pn.getCombinationAuthorship().exists());
        assertNull(pn.getNominatingParty());
        assertEquals("Tephrosia", pn.getGenus());
        assertEquals("rosea", pn.getSpecificEpithet());
        assertEquals("Fortescue creeks", pn.getPhrase());
        assertEquals(Rank.VARIETY, pn.getRank());
        assertNull(pn.getVoucher());
        assertEquals(NameType.INFORMAL, pn.getType());
    }

    @Test
    public void testSpecialCase5() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-2.csv"));
        ParsedName pn = parser.get("Verticordia serrata var. Udumung (D.Hunter & B.Yarran 941006)");
        assertNotNull(pn);
        assertFalse(pn.isCandidatus());
        assertNull(pn.getCultivarEpithet());
        assertEquals(NomCode.BOTANICAL, pn.getCode());
        assertNull(pn.getInfragenericEpithet());
        assertNull(pn.getInfraspecificEpithet());
        assertNull(pn.getNotho());
        assertNull(pn.getUninomial());
        assertFalse(pn.getBasionymAuthorship().exists());
        assertFalse(pn.getCombinationAuthorship().exists());
        assertEquals("WA Herbarium", pn.getNominatingParty());
        assertEquals("Verticordia", pn.getGenus());
        assertEquals("serrata", pn.getSpecificEpithet());
        assertEquals("Udmung", pn.getPhrase());
        assertEquals(Rank.VARIETY, pn.getRank());
        assertEquals("D.Hunter & B.Yarran 941006", pn.getVoucher());
        assertEquals(NameType.INFORMAL, pn.getType());
    }

    @Test
    public void testSpecialCase6() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-2.csv"));
        ParsedName pn = parser.get("Eucalyptus de beuzevillei");
        assertNotNull(pn);
        assertFalse(pn.isCandidatus());
        assertNull(pn.getCultivarEpithet());
        assertEquals(NomCode.BOTANICAL, pn.getCode());
        assertNull(pn.getInfragenericEpithet());
        assertNull(pn.getInfraspecificEpithet());
        assertNull(pn.getNotho());
        assertNull(pn.getUninomial());
        assertFalse(pn.getBasionymAuthorship().exists());
        assertFalse(pn.getCombinationAuthorship().exists());
        assertNull(pn.getNominatingParty());
        assertEquals("Eucalyptus", pn.getGenus());
        assertEquals("de beuzevillei", pn.getSpecificEpithet());
        assertNull(pn.getPhrase());
        assertEquals(Rank.SPECIES, pn.getRank());
        assertNull(pn.getVoucher());
        assertEquals(NameType.SCIENTIFIC, pn.getType());
    }

    @Test
    public void testSpecialCase7() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-2.csv"));
        ParsedName pn = parser.get("Eucalyptus de beuzevillei Maiden");
        assertNotNull(pn);
        assertFalse(pn.isCandidatus());
        assertNull(pn.getCultivarEpithet());
        assertEquals(NomCode.BOTANICAL, pn.getCode());
        assertNull(pn.getInfragenericEpithet());
        assertNull(pn.getInfraspecificEpithet());
        assertNull(pn.getNotho());
        assertNull(pn.getUninomial());
        assertTrue(pn.getBasionymAuthorship().exists());
        assertEquals("Maiden", pn.getBasionymAuthorship().toString());
        assertFalse(pn.getCombinationAuthorship().exists());
        assertNull(pn.getNominatingParty());
        assertEquals("Eucalyptus", pn.getGenus());
        assertEquals("de beuzevillei", pn.getSpecificEpithet());
        assertNull(pn.getPhrase());
        assertEquals(Rank.SPECIES, pn.getRank());
        assertNull(pn.getVoucher());
        assertEquals(NameType.SCIENTIFIC, pn.getType());
    }


    @Test
    public void testSpecialCase8() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-2.csv"));
        ParsedName pn = parser.get("Watsonia SuiLan");
        assertNotNull(pn);
        assertFalse(pn.isCandidatus());
        assertEquals("Suilan", pn.getCultivarEpithet());
        assertEquals(NomCode.CULTIVARS, pn.getCode());
        assertNull(pn.getInfragenericEpithet());
        assertNull(pn.getInfraspecificEpithet());
        assertNull(pn.getNotho());
        assertEquals("Watsonia", pn.getUninomial());
        assertFalse(pn.getBasionymAuthorship().exists());
        assertFalse(pn.getCombinationAuthorship().exists());
        assertNull(pn.getNominatingParty());
        assertNull(pn.getGenus());
        assertNull(pn.getSpecificEpithet());
        assertNull(pn.getPhrase());
        assertEquals(Rank.UNRANKED, pn.getRank());
        assertNull(pn.getVoucher());
        assertEquals(NameType.SCIENTIFIC, pn.getType());
    }

    @Test
    public void testStore1() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-1.csv"));
        StringWriter writer = new StringWriter();
        parser.store(writer);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "special-stored-1.csv"), writer.toString());
    }


    @Test
    public void testStore2() throws Exception {
        SpecialCaseNameParser parser = SpecialCaseNameParser.fromCSV(this.getClass().getResource("special-case-2.csv"));
        StringWriter writer = new StringWriter();
        parser.store(writer);
        TestUtils.compareNoSpaces(TestUtils.getResource(this.getClass(), "special-stored-2.csv"), writer.toString());
    }

}
