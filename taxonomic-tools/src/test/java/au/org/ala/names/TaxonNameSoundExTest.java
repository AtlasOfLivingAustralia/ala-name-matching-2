package au.org.ala.names;

import org.gbif.nameparser.api.NameType;
import org.gbif.nameparser.api.Rank;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaxonNameSoundExTest {
    @Test
    public void testNormalize1() throws Exception {
        assertEquals("ACACIA DEALBATA", TaxonNameSoundEx.normalize("Acacia dealbata", NameType.SCIENTIFIC));
        assertEquals("ACACIA DEALBATA", TaxonNameSoundEx.normalize("Acacia sp. dealbata", NameType.SCIENTIFIC));
        assertEquals("ACACIA SP NOV DEALBATA", TaxonNameSoundEx.normalize("Acacia sp nov. dealbata", NameType.SCIENTIFIC));
    }

    @Test
    public void testNormalize2() throws Exception {
        assertEquals("MUEHLENBECKIA COSTATA", TaxonNameSoundEx.normalize("Muehlenbeckia sp. nov. 'costata'", NameType.SCIENTIFIC));
        assertEquals("OLIGOCHAETOCHILUS BELVIDERE D E MURFET", TaxonNameSoundEx.normalize("Oligochaetochilus 'Belvidere' (D E Murfet 3603)", NameType.SCIENTIFIC));
        assertEquals("OENOCHROMINAE", TaxonNameSoundEx.normalize("Oenochrominae s. str.", NameType.SCIENTIFIC));
    }

    @Test
    public void testNormalize3() throws Exception {
        assertEquals("ACACIA AFF BIVENOSA A.R.CHAPMAN", TaxonNameSoundEx.normalize("Acacia aff. bivenosa (A.R.Chapman 601)", NameType.SCIENTIFIC));
        assertEquals("CYPERUS SP. AFF HOLOSCHOENUS", TaxonNameSoundEx.normalize("Cyperus sp. aff holoschoenus", NameType.INFORMAL));
        assertEquals("NITZSCHIA CF BICAPITATA", TaxonNameSoundEx.normalize("Nitzschia conf. bicapitata", NameType.INFORMAL));
    }

    @Test
    public void testNormalize4() throws Exception {
        assertEquals("MUNIDA LEAGORA", TaxonNameSoundEx.normalize("Munida leagora", NameType.SCIENTIFIC));
        assertEquals("MUNIDA LEAGORA", TaxonNameSoundEx.normalize("Munida    leagora", NameType.SCIENTIFIC));
        assertEquals("MUNIDA LEAGORA", TaxonNameSoundEx.normalize("   Munida\tleagora  ", NameType.SCIENTIFIC));
    }


    @Test
    public void testNormalize5() throws Exception {
        assertEquals("LIOTES SP. 3", TaxonNameSoundEx.normalize("Liotes sp. 3", NameType.INFORMAL));
        assertEquals("CANARIUM ACUTIFOLIUM ACUTIFOLIUM", TaxonNameSoundEx.normalize("Canarium acutifolium var. acutifolium", NameType.SCIENTIFIC));
    }

    @Test
    public void testTreatWord1() throws Exception {
        assertEquals("ACACA DIALBATA", TaxonNameSoundEx.treatWord("Acacia dealbata", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("ACACA DIALBATA", TaxonNameSoundEx.treatWord("Acacia sp. dealbata", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("ACACA SP NAV DIALBATA", TaxonNameSoundEx.treatWord("Acacia sp nov. dealbata", Rank.SPECIES, NameType.SCIENTIFIC, false));
    }

    @Test
    public void testTreatWord2() throws Exception {
        assertEquals("MILINBICA CASTATA", TaxonNameSoundEx.treatWord("Muehlenbeckia sp. nov. 'costata'", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("OLIGACITACILIS BILVIDIRI D E MIRFIT 3603", TaxonNameSoundEx.treatWord("Oligochaetochilus 'Belvidere' (D E Murfet 3603)", Rank.SPECIES, NameType.INFORMAL, false));
        assertEquals("ENACRAMINI", TaxonNameSoundEx.treatWord("Oenochrominae s. str.", Rank.SPECIES, NameType.SCIENTIFIC, false));
    }

    @Test
    public void testTreatWord3() throws Exception {
        assertEquals("ACACA AF BIVINASA A.R.CAPMAN 601", TaxonNameSoundEx.treatWord("Acacia aff. bivenosa (A.R.Chapman 601)", Rank.SPECIES, NameType.INFORMAL, false));
        assertEquals("CIPIRIS AF HALASINA", TaxonNameSoundEx.treatWord("Cyperus sp. aff holoschoenus", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("NITCSA CF BICAPITATA", TaxonNameSoundEx.treatWord("Nitzschia cfr. bicapitata", Rank.SPECIES, NameType.SCIENTIFIC, false));
    }

    @Test
    public void testTreatWord4() throws Exception {
        assertEquals("ACACA", TaxonNameSoundEx.treatWord("Acacia", Rank.GENUS, NameType.SCIENTIFIC, false));
        assertEquals("CIPIRIS", TaxonNameSoundEx.treatWord("Cyperus", Rank.GENUS, NameType.SCIENTIFIC, false));
        assertEquals("ENACRAMINI", TaxonNameSoundEx.treatWord("Oenochrominae", Rank.FAMILY, NameType.SCIENTIFIC, false));
    }

    @Test
    public void testTreatWord5() throws Exception {
        assertEquals("CANARIM ACITIFALA ACITIFALA", TaxonNameSoundEx.treatWord("Canarium acutifolium var. acutifolium", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("FICIS SICIDA", TaxonNameSoundEx.treatWord("Ficus subsect. Sycidium", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("ASPLINIM BILBIFIRA GRACILIMA", TaxonNameSoundEx.treatWord("Asplenium bulbiferum ssp. gracillimum", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("ASPLINIM BILBIFIRA GRACILIMA", TaxonNameSoundEx.treatWord("Asplenium bulbiferum ssp. gracillimum", Rank.SPECIES, NameType.SCIENTIFIC, false));
    }

    // Make sure kingdom soundexes are valid. If these change, check KingdomAnalysis
    @Test
    public void testTreatWord6() throws Exception {
        assertEquals("PLANTI", TaxonNameSoundEx.treatWord("Plantae", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("VIRIDIPLANTI", TaxonNameSoundEx.treatWord("Viridiplantae", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("FINGI", TaxonNameSoundEx.treatWord("Fungi", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("ANIMALA", TaxonNameSoundEx.treatWord("Animalia", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("VIRIS", TaxonNameSoundEx.treatWord("Virius", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("BACTIRA", TaxonNameSoundEx.treatWord("Bacteria", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("ARCA", TaxonNameSoundEx.treatWord("Archaea", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("UBACTIRA", TaxonNameSoundEx.treatWord("Eubacteria", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("CRAMISTA", TaxonNameSoundEx.treatWord("Chromista", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("PRATACA", TaxonNameSoundEx.treatWord("Protozoa", Rank.KINGDOM, NameType.SCIENTIFIC, false));
        assertEquals("PRATISTA", TaxonNameSoundEx.treatWord("Protista", Rank.KINGDOM, NameType.SCIENTIFIC, false));
    }


    @Test
    public void testTreatWord7() throws Exception {
        assertEquals("ACACA SP. H", TaxonNameSoundEx.treatWord("Acacia sp. H", Rank.SPECIES, NameType.INFORMAL, false));
    }


    @Test
    public void testTreatWord8() throws Exception {
        assertEquals("BILARDIRA CIRILIAPINCTATA", TaxonNameSoundEx.treatWord("Billardiera coeruleo-punctata", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("BILARDIRA CIRILIAPINCTATA", TaxonNameSoundEx.treatWord("Billardiera coeruleopunctata", Rank.SPECIES, NameType.SCIENTIFIC, false));
    }

    @Test
    public void testTreatWord9() throws Exception {
        assertEquals("ACITIFALIM", TaxonNameSoundEx.treatWord("acutifolium", Rank.SPECIES, NameType.SCIENTIFIC, false));
        assertEquals("ACITIFALA", TaxonNameSoundEx.treatWord("acutifolium", Rank.SPECIES, NameType.SCIENTIFIC, true));
    }

}
