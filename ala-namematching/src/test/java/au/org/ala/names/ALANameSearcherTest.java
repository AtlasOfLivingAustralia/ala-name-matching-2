package au.org.ala.names;

import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.Match;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.nameparser.api.Rank;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

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
        assertEquals(0.99961, result.getProbability().getPosterior(), 0.00001);
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
    public void testSoundexSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Poodytes gramina";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://biodiversity.org.au/afd/taxa/061fef09-7c9d-4b6d-9827-4da13a350dc6", result.getMatch().taxonId);
        assertEquals(0.99961, result.getProbability().getPosterior(), 0.00001);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME));
    }

    @Test
    public void testMisappliedName1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corybas macranthus";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("https://id.biodiversity.org.au/instance/apni/51400952", result.getMatch().taxonId);
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
    public void testSynonymAsHomonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Abelia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertEquals("https://id.biodiversity.org.au/node/apni/2892114", result.getMatch().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM));
    }

    @Test
    public void testSynonymAsHomonym2() throws Exception {
            AlaLinnaeanClassification template = new AlaLinnaeanClassification();
            template.scientificName = "Bracteolatae";
            template.taxonRank = Rank.SERIES;
            Match<AlaLinnaeanClassification> result = this.searcher.search(template);
    }


}
