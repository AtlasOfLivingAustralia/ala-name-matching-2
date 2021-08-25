package au.org.ala.names;

import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.Match;
import au.org.ala.vocab.ALATerm;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.nameparser.api.Rank;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class ALANameSearcherTest {
    public static final String INDEX = "/data/lucene/index-20210811";

    private ALANameSearcher searcher;

    @Before
    public void setUp() throws Exception {
        File index = new File(INDEX);
        if (!index.exists())
            throw new IllegalStateException("Index " + index + " not present");
        this.searcher = new ALANameSearcher(index);
    }

    @After
    public void tearDown() throws Exception {
        this.searcher.close();
    }

    @Test
    public void testSimpleSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Poodytes gramineus";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://biodiversity.org.au/afd/taxa/061fef09-7c9d-4b6d-9827-4da13a350dc6", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleSearch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Synemon plana";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://biodiversity.org.au/afd/taxa/a51dca29-50e7-49b4-ae35-5c35a9c4f854", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleSearch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Sargassum podacanthum";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("54105060", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleSearch4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Chenopodium x bontei nothovar. submelanocarpum";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/node/apni/2902250", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleSearch5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Favolus princeps";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/instance/fungi/60071845", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60098663", result.getMatch().acceptedNameUsageId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleSearch6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Stigmodera aurifera Carter";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://biodiversity.org.au/afd/taxa/426ab801-0d5f-4b43-b1b4-55ce7ce7a44e", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/6c212123-fadc-4307-8dd8-ac501bb534ba", result.getMatch().acceptedNameUsageId);
        assertEquals("Stigmodera aurifera", result.getMatch().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testRemoveSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Echinochaete brachypora";
        template.order = "Somethingales";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60098663", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_ORDER), result.getIssues());
    }


    @Test
    public void testRemoveSearch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Scleroderma aurantium";
        template.phylum = "Somethingocota"; // Basidiomycota
        template.class_ = "Agaricomycetes";
        template.order = "Arubbishname"; // Boletales
        template.family = "Sclerodermataceae";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60096937", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.REMOVED_ORDER), result.getIssues());
    }


    @Test
    public void testRemoveSearch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Canarium acutifolium var. acutifolium";
        template.genus = "Canarim"; // Canarium
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/node/apni/2904909", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME), result.getIssues());
    }

    @Test
    public void testSoundexSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Poodytes gramina";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://biodiversity.org.au/afd/taxa/061fef09-7c9d-4b6d-9827-4da13a350dc6", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME));
    }

    @Test
    public void testMisappliedName1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corybas macranthus";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        // The actusl value may change bewteen instances of the index here, since there are multiple possibilities
        assertEquals("https://id.biodiversity.org.au/instance/apni/51400951", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51401037", result.getMatch().acceptedNameUsageId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(TaxonomicStatus.misapplied, result.getMatch().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.MISAPPLIED_NAME));
    }

    @Test
    public void testMisappliedName2() throws Exception {
        //test to ensure that the accepted name is returned when it also exists as a misapplied name.
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bertya rosmarinifolia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/node/apni/2893214", result.getMatch().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME));
    }

    @Test
    public void testMisappliedName3() throws Exception {
        //test to ensure that the accepted name is returned when it also exists as a misapplied name.
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia acuminata Benth.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51285812", result.getMatch().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME));
    }

    @Test
    public void testMisappliedName4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia bivenosa DC.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/node/apni/2912987", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME));
        assertTrue(result.getIssues().contains(ALATerm.canonicalMatch));
    }

    @Test
    public void testSynonymAsHomonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Abelia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://id.biodiversity.org.au/node/apni/2892114", result.getMatch().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM));
    }

    @Ignore("Until rebuild of index")
    @Test
    public void testSynonymAsHomonym2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bracteolatae";
        template.taxonRank = Rank.SERIES;
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://id.biodiversity.org.au/name/apni/89092", result.getMatch().taxonId);
        assertTrue(result.getIssues().isEmpty());
    }

    @Test
    public void endRankMarkerTest1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia sp";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51382879", result.getMatch().taxonId);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.INDETERMINATE_NAME));
    }

    @Test
    public void endRankMarkerTest2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Insecta fam.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://biodiversity.org.au/afd/taxa/17c9fd64-3c07-4df5-a33d-eda1e065e99f", result.getMatch().taxonId);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.INDETERMINATE_NAME));
    }

    @Test
    public void catchAllSpeciesTest() {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "sp";
        try {
            this.searcher.search(template);
            fail("A rank marker should not match to a name");
        } catch (Exception e) {
            assertEquals("Supplied scientific name is a rank marker.", e.getMessage());
        }
    }

    @Test
    public void testHigherOrder1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Varaex timorensis";
        template.family = "Varanidae";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://biodiversity.org.au/afd/taxa/0353f674-a4df-4519-b887-e8256b2238c9", result.getMatch().taxonId);
        assertEquals(Rank.FAMILY, result.getMatch().taxonRank);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.HIGHER_ORDER_MATCH));
    }

    @Test
    public void testSpeciesSplitSynonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corvus orru";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://biodiversity.org.au/afd/taxa/2c5fd509-d4d6-4adb-9566-96280ff9e6af", result.getMatch().taxonId);
        assertEquals(Rank.SPECIES, result.getMatch().taxonRank);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM));
    }

    @Test
    public void testEmbeddedRankMarker1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Flueggea virosa subsp. melanthesoides";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://id.biodiversity.org.au/node/apni/2893899", result.getMatch().taxonId);
        assertEquals(Rank.INFRASPECIFIC_NAME, result.getMatch().taxonRank);
    }


}
