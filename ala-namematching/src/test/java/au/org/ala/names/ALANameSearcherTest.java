package au.org.ala.names;

import au.org.ala.bayesian.Match;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ALANameSearcherTest {
    public static final String INDEX = "/data/lucene/index-20201117";

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
        template.scientificName = "Megalurus gramineus";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("urn:lsid:biodiversity.org.au:afd.taxon:b88430ed-f7d7-482e-a586-f0a02d8e11ce", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleSearch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Synemon plana";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("urn:lsid:biodiversity.org.au:afd.taxon:a51dca29-50e7-49b4-ae35-5c35a9c4f854", result.getMatch().taxonId);
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
        assertEquals("https://id.biodiversity.org.au/instance/apni/769095", result.getMatch().taxonId);
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
        assertEquals("43e1bc65-3580-47db-b269-cdb066ed49e9", result.getMatch().taxonId);
        assertEquals("10911fd1-a2dd-41f1-9c4d-8dff7f118670", result.getMatch().acceptedNameUsageId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleSearch6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Stigmodera aurifera Carter";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("urn:lsid:biodiversity.org.au:afd.name:e89de580-2942-479d-b5ef-5edd60424560", result.getMatch().taxonId);
        assertEquals("urn:lsid:biodiversity.org.au:afd.taxon:2e8ac1d8-5f2b-4fcd-a124-c619c7cab6b0", result.getMatch().acceptedNameUsageId);
        assertEquals("Stigmodera aurifera", result.getMatch().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
    }


    @Test
    public void testRemoveSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Echinochaete brachypora";
        template.phylum = "Somethingocota";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result);
        assertNotNull(result.getMatch());
        assertEquals("10911fd1-a2dd-41f1-9c4d-8dff7f118670", result.getMatch().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(1, result.getIssues().size());
    }

}
