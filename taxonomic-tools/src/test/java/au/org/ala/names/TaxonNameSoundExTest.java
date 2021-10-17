package au.org.ala.names;

import org.gbif.nameparser.api.Rank;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaxonNameSoundExTest {
    @Test
    public void testNormalize1() throws Exception {
        assertEquals("ACACIA DEALBATA", TaxonNameSoundEx.normalize("Acacia dealbata"));
        assertEquals("ACACIA DEALBATA", TaxonNameSoundEx.normalize("Acacia sp. dealbata"));
        assertEquals("ACACIA SP NOV DEALBATA", TaxonNameSoundEx.normalize("Acacia sp nov. dealbata"));
    }

    @Test
    public void testNormalize2() throws Exception {
        assertEquals("MUEHLENBECKIA COSTATA", TaxonNameSoundEx.normalize("Muehlenbeckia sp. nov. 'costata'"));
        assertEquals("OLIGOCHAETOCHILUS BELVIDERE D E MURFET", TaxonNameSoundEx.normalize("Oligochaetochilus 'Belvidere' (D E Murfet 3603)"));
        assertEquals("OENOCHROMINAE", TaxonNameSoundEx.normalize("Oenochrominae s. str."));
    }

    @Test
    public void testNormalize3() throws Exception {
        assertEquals("ACACIA AFF BIVENOSA A.R.CHAPMAN", TaxonNameSoundEx.normalize("Acacia aff. bivenosa (A.R.Chapman 601)"));
        assertEquals("CYPERUS AFF HOLOSCHOENUS", TaxonNameSoundEx.normalize("Cyperus sp. aff holoschoenus"));
        assertEquals("NITZSCHIA CF BICAPITATA", TaxonNameSoundEx.normalize("Nitzschia conf. bicapitata"));
    }

    @Test
    public void testNormalize4() throws Exception {
        assertEquals("MUNIDA LEAGORA", TaxonNameSoundEx.normalize("Munida leagora"));
        assertEquals("MUNIDA LEAGORA", TaxonNameSoundEx.normalize("Munida    leagora"));
        assertEquals("MUNIDA LEAGORA", TaxonNameSoundEx.normalize("   Munida\tleagora  "));
    }

    @Test
    public void testTreatWord1() throws Exception {
        assertEquals("ACACA DIALBATA", TaxonNameSoundEx.treatWord("Acacia dealbata", "species"));
        assertEquals("ACACA DIALBATA", TaxonNameSoundEx.treatWord("Acacia sp. dealbata", "species"));
        assertEquals("ACACA SP NAV DIALBATA", TaxonNameSoundEx.treatWord("Acacia sp nov. dealbata", "species"));
    }

    @Test
    public void testTreatWord2() throws Exception {
        assertEquals("MILINBICA CASTATA", TaxonNameSoundEx.treatWord("Muehlenbeckia sp. nov. 'costata'", "species"));
        assertEquals("OLIGACITACILIS BILVIDIRI D I MIRFIT", TaxonNameSoundEx.treatWord("Oligochaetochilus 'Belvidere' (D E Murfet 3603)", "species"));
        assertEquals("ENACRAMINI", TaxonNameSoundEx.treatWord("Oenochrominae s. str.", "species"));
    }

    @Test
    public void testTreatWord3() throws Exception {
        assertEquals("ACACA AF BIVINASA A.R.CAPMAN", TaxonNameSoundEx.treatWord("Acacia aff. bivenosa (A.R.Chapman 601)", "species"));
        assertEquals("CIPIRIS AF ALASINA", TaxonNameSoundEx.treatWord("Cyperus sp. aff holoschoenus", "species"));
        assertEquals("NITCSA CF BICAPITATA", TaxonNameSoundEx.treatWord("Nitzschia cfr. bicapitata", "species"));
    }

    @Test
    public void testTreatWord4() throws Exception {
        assertEquals("ACACA", TaxonNameSoundEx.treatWord("Acacia", Rank.GENUS));
        assertEquals("CIPIRIS", TaxonNameSoundEx.treatWord("Cyperus", Rank.GENUS));
        assertEquals("ENACRAMINI", TaxonNameSoundEx.treatWord("Oenochrominae", Rank.FAMILY));
    }

    @Test
    public void testTreatWord5() throws Exception {
        assertEquals("CANARIM ACITIFALIM ACITIFALA", TaxonNameSoundEx.treatWord("Canarium acutifolium var. acutifolium", Rank.SPECIES));
        assertEquals("FICIS SICIDA", TaxonNameSoundEx.treatWord("Ficus subsect. Sycidium", Rank.SPECIES));
        assertEquals("ASPLINIM BILBIFIRIM GRACILIMA", TaxonNameSoundEx.treatWord("Asplenium bulbiferum ssp. gracillimum", Rank.SPECIES));
    }

}
