package au.org.ala.names;

import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.Match;
import au.org.ala.vocab.ALATerm;
import au.org.ala.vocab.BayesianTerm;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.api.vocabulary.NomenclaturalCode;
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
    public static final String VERNACULAR_INDEX = "/data/lucene/vernacular-20210811";

    private ALANameSearcher searcher;

    @Before
    public void setUp() throws Exception {
        File index = new File(INDEX);
        File vernacular = new File(VERNACULAR_INDEX);
        if (!index.exists())
            throw new IllegalStateException("Index " + index + " not present");
        if (!vernacular.exists())
            throw new IllegalStateException("Vernacular Index " + vernacular + " not present");
        this.searcher = new ALANameSearcher(index, vernacular);
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
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/061fef09-7c9d-4b6d-9827-4da13a350dc6", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Synemon plana";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/a51dca29-50e7-49b4-ae35-5c35a9c4f854", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Sargassum podacanthum";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("54105060", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Chenopodium x bontei nothovar. submelanocarpum";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2902250", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2902250", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARTIALLY_EXCLUDED_NAME, AlaLinnaeanFactory.EXCLUDED_NAME), result.getIssues());
    }

    @Test
    public void testSimpleSearch5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Favolus princeps";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/fungi/60071845", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60098663", result.getAccepted().taxonId);
        assertEquals(0.99946, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Stigmodera aurifera Carter";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/426ab801-0d5f-4b43-b1b4-55ce7ce7a44e", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/6c212123-fadc-4307-8dd8-ac501bb534ba", result.getAccepted().taxonId);
        assertEquals("Stigmodera aurifera", result.getMatch().scientificName);
        assertEquals(0.99997, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    @Test
    public void testSimpleSearch7() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Rhinotia";
        template.taxonRank = Rank.GENUS;
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/03ff8172-bda5-4751-819e-fbfaf8c98c8e", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/03ff8172-bda5-4751-819e-fbfaf8c98c8e", result.getAccepted().taxonId);
        assertEquals("Rhinotia", result.getMatch().scientificName);
        assertEquals(0.99992, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Subject to multiple versions of the name
    @Test
    public void testSimpleSearch8() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51382879", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51382879", result.getAccepted().taxonId);
        assertEquals("Acacia", result.getMatch().scientificName);
        assertEquals(0.99934, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch9() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Myrmecia";
        template.family = "Formicidae";
        template.order = "Hymenoptera";
        template.class_ = "Insecta";
        template.phylum = "Arthropoda";
        template.kingdom = "Animalia";
        template.taxonRank = Rank.GENUS;
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/766f0a13-c31c-46e5-8d36-6cff88292635", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/766f0a13-c31c-46e5-8d36-6cff88292635", result.getAccepted().taxonId);
        assertEquals("Myrmecia", result.getMatch().scientificName);
        assertEquals(0.99992, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch10() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Neobatrachus sudellae";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/953a5af4-2932-4c8b-8f33-850b5f8f3fed", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/953a5af4-2932-4c8b-8f33-850b5f8f3fed", result.getAccepted().taxonId);
        assertEquals("Neobatrachus sudellae", result.getMatch().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch11() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eucalyptus acaciaeformis";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/852785", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2889217", result.getAccepted().taxonId);
        assertEquals("Eucalyptus acaciaeformis", result.getMatch().scientificName);
        assertEquals("Eucalyptus acaciiformis", result.getAccepted().scientificName);
        assertEquals(0.99946, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Ignore("Requires synthetic taxon resolution - TBD")
    @Test
    public void testSimpleSearch12() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Banksia collina";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/838699", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/instance/apni/838699", result.getAccepted().taxonId);
        assertEquals("Banksia collina", result.getAccepted().scientificName);
        assertEquals(0.99946, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch13() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Stephanopis similis";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/24bc164a-85b2-4633-85c5-a3b399daec0a", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/24bc164a-85b2-4633-85c5-a3b399daec0a", result.getAccepted().taxonId);
        assertEquals("Stephanopis similis", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch14() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Fraus latistria";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/2358fcc0-8db2-475d-8da4-fd4bd5e711f2", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/2358fcc0-8db2-475d-8da4-fd4bd5e711f2", result.getAccepted().taxonId);
        assertEquals("Fraus latistria", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch15() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Metrosideros fulgens";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/110385", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/name/apni/110385", result.getAccepted().taxonId);
        assertEquals("Metrosideros fulgens", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch16() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Metrosideros scandens";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/233086", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/name/apni/233086", result.getAccepted().taxonId);
        assertEquals("Metrosideros scandens", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testSimpleSearch17() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Tyto alba";
        template.kingdom = "Animalia";
        template.phylum = "Chordata";
        template.class_ = "Aves";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-54688", result.getMatch().taxonId);
        assertEquals("NZOR-6-54688", result.getAccepted().taxonId);
        assertEquals("Tyto alba", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testMultipleMatches1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Cygnus atratus";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3f66250c-eec1-4f23-8338-26663c929d66", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/3f66250c-eec1-4f23-8338-26663c929d66", result.getAccepted().taxonId);
        assertEquals("Cygnus atratus", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());
    }

    @Test
    public void testRemoveSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Echinochaete brachypora";
        template.order = "Somethingales";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60098663", result.getAccepted().taxonId);
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
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60096937", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.REMOVED_ORDER), result.getIssues());
    }

    @Test
    public void testRemoveSearch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Canarium acutifolium var. acutifolium";
        template.genus = "Canarim"; // Canarium
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/apni/2904909", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME, AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());
    }

    @Test
    public void testSoundexSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Poodytes gramina"; // Poodytes gramineus
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://biodiversity.org.au/afd/taxa/061fef09-7c9d-4b6d-9827-4da13a350dc6", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME));
    }

    @Test
    public void testMisappliedName1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corybas macranthus";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        // The actusl value may change bewteen instances of the index here, since there are multiple possibilities
        assertEquals("https://id.biodiversity.org.au/instance/apni/51400951", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51401037", result.getAccepted().taxonId);
        assertEquals(0.79913, result.getProbability().getPosterior(), 0.00001);
        assertEquals(TaxonomicStatus.misapplied, result.getMatch().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.MISAPPLIED_NAME));
    }

    @Test
    public void testMisappliedName2() throws Exception {
        //test to ensure that the accepted name is returned when it also exists as a misapplied name.
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bertya rosmarinifolia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/apni/2893214", result.getAccepted().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME));
    }

    @Test
    public void testMisappliedName3() throws Exception {
        //test to ensure that the accepted name is returned when it also exists as a misapplied name.
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia acuminata Benth.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51285812", result.getAccepted().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME));
    }

    @Test
    public void testMisappliedName4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia bivenosa DC.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/apni/2912987", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME));
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.CANONICAL_NAME));
    }

    @Test
    public void testSynonymAsHomonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Abelia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2892114", result.getAccepted().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM));
    }

    // This one is a pain, since there's a series and a subsection. Good luck.
    @Test
    public void testSynonymAsHomonym2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bracteolatae";
        template.taxonRank = Rank.SERIES;
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/89092", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME, AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());
    }

    @Test
    public void indeterminateNameTest1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia sp";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51382879", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME), result.getIssues());
    }

    @Test
    public void indeterminateNameTest2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Insecta fam.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/17c9fd64-3c07-4df5-a33d-eda1e065e99f", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME), result.getIssues());
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
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/0353f674-a4df-4519-b887-e8256b2238c9", result.getAccepted().taxonId);
        assertEquals(Rank.FAMILY, result.getAccepted().taxonRank);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.HIGHER_ORDER_MATCH));
    }

    @Test
    public void testSpeciesSplitSynonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corvus orru";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/2c5fd509-d4d6-4adb-9566-96280ff9e6af", result.getAccepted().taxonId);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM));
    }

    @Test
    public void testEmbeddedRankMarker1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Flueggea virosa subsp. melanthesoides";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2893899", result.getAccepted().taxonId);
        assertEquals(Rank.INFRASPECIFIC_NAME, result.getAccepted().taxonRank);
    }

    @Test
    public void testEmbeddedRankMarker2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thelymitra sp. adorata";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51414212", result.getAccepted().taxonId);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
    }

    @Test
    public void testEmbeddedRankMarker3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Grevillea brachystylis subsp. Busselton (G.J.Keighery s.n. 28/8/1985)";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/897499", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2918130", result.getAccepted().taxonId);
        assertEquals(Rank.INFRASPECIFIC_NAME, result.getAccepted().taxonRank);
    }

    @Test
    public void testEmbeddedRankMarker4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Lindernia sp. Pilbara (M.N.Lyons & L.Lewis FV 1069)";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/51306553", result.getAccepted().taxonId);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
    }

    @Test
    public void testExcludedNames1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Cyrtodactylus louisiadensis";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/74ac7082-6138-4eb0-86ba-95535deab180", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e5d517d3-5d04-4ef5-aa58-34e8cab03f96", result.getAccepted().taxonId);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.EXCLUDED_NAME));
    }

    @Test
    public void testExcludedNames2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Zygophyllum sessilifolium";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51311545", result.getAccepted().taxonId);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.EXCLUDED_NAME));
    }

    @Test
    public void testExcludedNames3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Callistemon pungens";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2909631", result.getAccepted().taxonId);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARTIALLY_EXCLUDED_NAME));
    }


    @Test
    public void testExcludedNames4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Carbo ater";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/416d9a5b-b43a-4ed3-9431-b0e6e7a693d4", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/9e27e64f-407b-4050-96ef-4ea4381b1554", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    @Test
    public void testHomonymsWithResolution1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
    }

    @Test
    public void testHomonymsWithResolution2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.kingdom = "Animalia";
        template.phylum = "Chordata";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/52c68649-47d5-4f2e-9730-417fc54fb080", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testHomonymsWithResolution3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.kingdom = "Plantae";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2908051", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testHomonymsWithResolution4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.scientificNameAuthorship = "Blumenbach, 1798";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/52c68649-47d5-4f2e-9730-417fc54fb080", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testHomonymsWithResolution5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.scientificNameAuthorship = "Blumenbach";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/52c68649-47d5-4f2e-9730-417fc54fb080", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testHomonymsWithResolution6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.family = "Marantaceae";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2908051", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testParentChildSynonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Geopelia placida";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3d5c4e0d-5138-46e0-8e14-5acd8fd2c523", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testParentChildSynonym2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corvus orru";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/2c5fd509-d4d6-4adb-9566-96280ff9e6af", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testParentChildSynonym3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Phoma lobeliae";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60083447", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testParentChildSynonym4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Geopelia placida";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3d5c4e0d-5138-46e0-8e14-5acd8fd2c523", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testUncertainName1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Darwinia acerosa?";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2919768", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME), result.getIssues());
    }

    @Test
    public void testUncertainName2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Swainsona cf. luteola";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/51316648", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.CONFER_SPECIES_NAME), result.getIssues());
    }

    @Test
    public void testUncertainName3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Paraminabea aff. aldersladei";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/a03b0914-9e82-427a-b84e-296c935856fa", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME), result.getIssues());
    }



    @Test
    public void testUncertainName4() throws Exception  {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Carex aff. tereticaulis (Lake Omeo)";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("ALA_134351", result.getMatch().taxonId);
        assertEquals("ALA_134351", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());

        // Match onto higher order without further info
        template = new AlaLinnaeanClassification();
        template.scientificName = "Carex aff. tereticaulis";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51282936", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51282936", result.getAccepted().taxonId);
        assertEquals(0.99933, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());

        // Actual taxon
        template = new AlaLinnaeanClassification();
        template.scientificName = "Carex tereticaulis";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2919780", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2919780", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
    }



    @Test
    public void testSensuStrictoMarker1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Oenochrominae s. str.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/537ff8fb-b6c2-4536-9cb8-ad244832c1de", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSensuStrictoMarker2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Pterodroma arminjoniana s. str.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("40041023", result.getMatch().taxonId);
        assertEquals("40041023", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME, AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());
    }


    @Test
    public void testPhraseMatch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Elaeocarpus sp. Rocky Creek";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        // Higher order match as Rocky Creek interpreted as author
        assertEquals("https://id.biodiversity.org.au/node/apni/7176196", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/7176196", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testPhraseMatch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Elaeocarpus sp. Rocky Creek (Hunter s.n. 16 Sep 1993)";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/871103", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2916168", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testPhraseMatch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Pultenaea sp. Olinda (R.Coveny 6616)";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2886985", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testPhraseMatch4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Goodenia sp. Bachsten Creek (M.D.Barrett 685) WA Herbarium";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2890349", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testPhraseMatch5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Baeckea sp. Bungalbin Hill (B.J.Lepschi & L.A.Craven 4586)";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2903711", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testPhraseMatch6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Astroloma sp. Cataby (E.A.Griffin 1022)";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/7178434", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testAcceptedSynonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thelymitra sp. adorata";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        // Has a nom inval version as well
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51414212", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testKingdom1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Animalia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/4647863b-760d-4b59-aaa1-502c8cdf8d3c", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testKingdom2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bacteria";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-73174", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testHomonyms1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Agathis";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
    }


    @Test
    public void testHomonyms2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Agathis";
        template.nomenclaturalCode = NomenclaturalCode.BOTANICAL;
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51299766", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testHomonyms3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Simsia";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
    }

    @Test
    public void testHomonyms4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Simsia";
        template.kingdom = "Plantae";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/837040", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2914974", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testHomonyms5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Serpula";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
    }

    @Test
    public void testHomonyms6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Serpula";
        template.phylum = "Annelida";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/1617ab6e-b426-422d-89d1-b7b86e87381b", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testHomonyms7() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Patellina";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
        template.nomenclaturalCode = NomenclaturalCode.BOTANICAL;
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-17971", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
        template.nomenclaturalCode = NomenclaturalCode.ZOOLOGICAL;
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/8d6a5e0e-03c4-4333-ae22-42daea4d01c6", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSpeciesPlural1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Opuntia spp.";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51269889", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51269889", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME), result.getIssues());
    }

    @Test
    public void testCultivar1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Hypoestes phyllostachya 'Splash'";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2896663", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2896663", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    @Test
    public void testCultivar2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Anigozanthos 'Bush Rebel'";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/4946384", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_RANK), result.getIssues());
    }

    @Test
    public void testCultivar3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acianthus sp. 'Gibraltar Range'";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/50738493", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testCultivar4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Conospermum taxifolium 'Tasmanian form'";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/229673", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_RANK), result.getIssues());
    }

    @Test
    public void testCultivar5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Grevillea sp aff patulifolia 'Kanangra'";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/837807", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2916815", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testCultivar6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Grevillea sp. nov. 'Belowra'";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/837821", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2898070", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testCultivar7() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Oligochaetochilus aff. boormanii 'Coastal'";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/51411288", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51412124", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.REMOVED_RANK), result.getIssues());
    }

    @Test
    public void testVariant1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eolophus roseicapilla";
        Match<AlaLinnaeanClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(), result.getIssues());
        template.scientificName = "Eolophus roseicapillus";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.00001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME), result.getIssues());

    }

    @Test
    public void testVernacular1() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Red Kangaroo";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e6aff6af-ff36-4ad5-95f2-2dfdcca8caff", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e6aff6af-ff36-4ad5-95f2-2dfdcca8caff", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacular2() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Blue Gum"; // Multiple possibilities
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2886090", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2886090", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testVernacular3() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Aka-kōpū-kererū";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2913490", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2913490", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacular4() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Aka-kopu-kereru";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2913490", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2913490", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacular5() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Yellow-tailed Black-Cockatoo";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/145b081d-eca7-4d9b-9171-b97e2d061536", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/145b081d-eca7-4d9b-9171-b97e2d061536", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacular6() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Scarlet Robin";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/a3e5376b-f9e6-4bdf-adae-1e7add9f5c29", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/a3e5376b-f9e6-4bdf-adae-1e7add9f5c29", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacularSameTaxon1() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Wedge-leaved Rattlepod";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2895442", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2895442", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacularSameTaxon2() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Waratah";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51293559", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51293559", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacularRemove1() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Red Kangaroo";
        template.language = "fr";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e6aff6af-ff36-4ad5-95f2-2dfdcca8caff", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e6aff6af-ff36-4ad5-95f2-2dfdcca8caff", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaVernacularFactory.REMOVED_LANGUAGE), result.getIssues());
    }


    @Test
    public void testVernacularMisspelled1() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Red Kangaru";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e6aff6af-ff36-4ad5-95f2-2dfdcca8caff", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e6aff6af-ff36-4ad5-95f2-2dfdcca8caff", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaVernacularFactory.MISSPELLED_VERNACULAR_NAME), result.getIssues());
    }


    @Test
    public void testVernacularMisspelled2() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Aka-kopu-kereroo";
        Match<AlaVernacularClassification> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2913490", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2913490", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaVernacularFactory.MISSPELLED_VERNACULAR_NAME), result.getIssues());
    }

}
