package au.org.ala.names;

import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.Match;
import au.org.ala.bayesian.MatchMeasurement;
import au.org.ala.location.AlaLocationClassification;
import au.org.ala.util.FileUtils;
import au.org.ala.util.JsonUtils;
import au.org.ala.vocab.BayesianTerm;
import au.org.ala.vocab.TaxonomicStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.Rank;
import org.junit.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ALANameSearcherTest {
    public static final String INDEX = "/data/lucene/linnaean-20230725-3";
    public static final String VERNACULAR_INDEX = "/data/lucene/vernacular-20230725-3";
    public static final String LOCATION_INDEX = "/data/lucene/location-20230725-3";
    public static final String SUGGESTER_INDEX = "/data/tmp/suggest-20230725-3";

    private ALANameSearcher searcher;

    // @BeforeClass // Uncomment if you want the suggester index to be built during testing
    public static void setUpClass() throws Exception {
        FileUtils.deleteAll(new File(SUGGESTER_INDEX));
    }

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = JsonUtils.createMapper();
        ALANameSearcherConfiguration config = mapper.readValue(ALANameSearcherTest.class.getResource("searcher-config-test.json"), ALANameSearcherConfiguration.class);
        this.searcher = new ALANameSearcher(config);
    }

    @After
    public void tearDown() throws Exception {
        this.searcher.close();
    }

    @Test
    public void testSimpleSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Poodytes gramineus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/b4c2ec8d-918d-4fb4-8ef9-f6d5d3bbceda", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Synemon plana";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/a51dca29-50e7-49b4-ae35-5c35a9c4f854", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // 20230725-3 matches Sargassum
    @Test
    public void testSimpleSearch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Sargassum podacanthum";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-59134", result.getAccepted().taxonId);
        assertEquals(0.983, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.208, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    // 20230725-3 Matches Chenopodium x bontei Chenopodium x bontei nothovar. submelanocarpum not in index
    @Test
    public void testSimpleSearch4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Chenopodium x bontei nothovar. submelanocarpum";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/947237", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51613011", result.getAccepted().taxonId);
        assertEquals(0.998, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.612, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    @Test
    public void testSimpleSearch5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Favolus princeps";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/fungi/60071845", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60098663", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Inital author slightly wonky
    // Authorship should still result in a 1.0 for the correct author
    @Test
    public void testSimpleSearch6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Stigmodera aurifera Carter";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/0d45a8af-f675-4566-b186-e01d14b16f62", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/066b76e0-168c-4f98-abe5-ca00b52811e7", result.getAccepted().taxonId);
        assertEquals("Stigmodera aurifera", result.getMatch().scientificName);
        assertEquals(0.523, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
        template = new AlaLinnaeanClassification();
        template.scientificName = "Stigmodera aurifera Carter, 1922";
        this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/0d45a8af-f675-4566-b186-e01d14b16f62", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/066b76e0-168c-4f98-abe5-ca00b52811e7", result.getAccepted().taxonId);
        assertEquals("Stigmodera aurifera", result.getMatch().scientificName);
        assertEquals( 0.523, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    @Test
    public void testSimpleSearch7() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Rhinotia";
        template.taxonRank = Rank.GENUS;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/03ff8172-bda5-4751-819e-fbfaf8c98c8e", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/03ff8172-bda5-4751-819e-fbfaf8c98c8e", result.getAccepted().taxonId);
        assertEquals("Rhinotia", result.getMatch().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Subject to multiple versions of the name
    @Test
    public void testSimpleSearch8() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51471290", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51471290", result.getAccepted().taxonId);
        assertEquals("Acacia", result.getMatch().scientificName);
        assertEquals(0.999, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
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
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/8ad335a6-ca85-4027-86a5-ee0aac136a1e", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/8ad335a6-ca85-4027-86a5-ee0aac136a1e", result.getAccepted().taxonId);
        assertEquals("Myrmecia", result.getMatch().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch10() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Neobatrachus sudellae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3b4960a0-ce97-450e-89b7-a360e4280c39", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/3b4960a0-ce97-450e-89b7-a360e4280c39", result.getAccepted().taxonId);
        assertEquals("Neobatrachus sudellae", result.getMatch().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch11() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eucalyptus acaciaeformis";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/852785", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2889217", result.getAccepted().taxonId);
        assertEquals("Eucalyptus acaciaeformis", result.getMatch().scientificName);
        assertEquals("Eucalyptus acaciiformis", result.getAccepted().scientificName);
        assertEquals(0.994, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Ignore("20230725-3 cultivar no longer in index")
    public void testSimpleSearch12() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Banksia canei 'Celia Rosser'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/173312", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/name/apni/173312", result.getAccepted().taxonId);
        assertEquals("Banksia canei 'Celia Rosser'", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch13() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Stephanopis similis";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/4a97fc18-858c-4283-9950-5325a7edb6ce", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/a8cf2164-a09b-4d62-a439-97e29105ea78", result.getAccepted().taxonId);
        assertEquals("Isala similis", result.getAccepted().scientificName);
        assertEquals(0.997, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch14() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Fraus latistria";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/061bc92c-8647-4739-a83d-d3ef16e80c06", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/061bc92c-8647-4739-a83d-d3ef16e80c06", result.getAccepted().taxonId);
        assertEquals("Fraus latistria", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSimpleSearch15() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Metrosideros fulgens";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-117997", result.getMatch().taxonId);
        assertEquals("NZOR-6-117997", result.getAccepted().taxonId);
        assertEquals("Metrosideros fulgens", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }
    @Test
    public void testSimpleSearch16() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eucalyptus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732879", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732879", result.getAccepted().taxonId);
        assertEquals("Eucalyptus", result.getAccepted().scientificName);
        assertEquals(0.982, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Two synonyms leading to syntehticv match
    @Test
    public void testProblemSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Metrosideros scandens";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-86045", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/7699913", result.getAccepted().taxonId);
        assertEquals("Metrosideros", result.getAccepted().scientificName);
        assertEquals(0.773, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.SYNTHETIC_MATCH), result.getIssues());
    }

    @Test
    public void testProblemSearch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Tyto alba";
        template.kingdom = "Animalia";
        template.phylum = "Chordata";
        template.class_ = "Aves";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/02f280f4-220a-4b3e-a64a-e1003277875a", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/6c0d5594-b44c-4779-9155-590c5a4a5f36", result.getAccepted().taxonId);
        assertEquals("Tyto", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.EXCLUDED_NAME), result.getIssues());
    }


    // Removed from 20230725-2 index
    @Test
    public void testProblemSearch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Erythroclonium sonderi";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
    }

    // No mtach for Dasyatididae (actually DASYATIDAE) not removed because there's nothing else to match
    @Test
    public void testProblemSearch4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.genus = "Pateobatis";
        template.family = "Dasyatididae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3a8df129-0a78-4449-9c9b-d31892b91665", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/3a8df129-0a78-4449-9c9b-d31892b91665", result.getAccepted().taxonId);
        assertEquals("Pateobatis", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.778, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_FAMILY, AlaLinnaeanFactory.REMOVED_ORDER), result.getIssues());
    }

    // Homonym caused by two entries with the same source and different accepted parents
    // This is not very good
    @Test
    public void testProblemSearch5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Aphanocapsa spp.";
        template.kingdom = "Protozoa";
        template.class_ = "Synechococcales";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/932fe780-c493-4397-9455-69d0e37159a4", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/932fe780-c493-4397-9455-69d0e37159a4", result.getAccepted().taxonId);
        assertEquals("PROTISTA", result.getAccepted().scientificName);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME, AlaLinnaeanFactory.REMOVED_LOCATION), result.getIssues());
    }


    @Test
    public void testProblemSearch6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Banksia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732900", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732900", result.getAccepted().taxonId);
        assertEquals("Banksia", result.getAccepted().scientificName);
        assertEquals(0.911, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME), result.getIssues());
    }

    @Test
    public void testProblemSearch7() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Ceratiomyxa fruticulosa (O.F.Müll.) T.Macbr., 1899";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60104095", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60104095", result.getAccepted().taxonId);
        assertEquals("Ceratiomyxa fruticulosa", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Invalid character in name or author
    @Test
    public void testProblemSearch8() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Chromis hypsilepis G\ufffdnther";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/bfc16a3f-f295-462b-b3e9-3a419e2a8506", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/bfc16a3f-f295-462b-b3e9-3a419e2a8506", result.getAccepted().taxonId);
        assertEquals("Chromis hypsilepis", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.818, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_AUTHORSHIP, AlaLinnaeanFactory.CANONICAL_NAME, BayesianTerm.illformedData), result.getIssues());
    }

    // Sufficiently mis-spelled
    @Test
    public void testProblemSearch9() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Crypopecten nux"; // Cryptopecten nux
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
    }


    // Names in wrong positions
    @Test
    public void testProblemSearch10() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.order = "Agaricaceae";
        template.family = "Lepiota";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
        template = new AlaLinnaeanClassification();
        template.family = "Agaricaceae";
        template.genus = "Lepiota";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/fungi/60118806", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/fungi/60118806", result.getAccepted().taxonId);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals("Lepiota", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Invalid phylum and kingdom
    @Test
    public void testProblemSearch11() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Cryptochilium nigricans";
        template.kingdom = "Protozoa";
        template.phylum = "Ciliatea";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://www.catalogueoflife.org/data/taxon/3WHR", result.getMatch().taxonId);
        assertEquals("https://www.catalogueoflife.org/data/taxon/3WHR", result.getAccepted().taxonId);
        assertEquals("Cryptochilium", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.315, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
        template.kingdom = "Chromista";
        template.soundexKingdom = null;
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://www.catalogueoflife.org/data/taxon/3WHR", result.getMatch().taxonId);
        assertEquals("https://www.catalogueoflife.org/data/taxon/3WHR", result.getAccepted().taxonId);
        assertEquals("Cryptochilium", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.238, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.EXPANDED_KINGDOM, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_PHYLUM), result.getIssues());
    }

    // Short soundex name
    @Test
    public void testProblemSearch12() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Earias cupreoviridis Walker, 1862";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/b7c4aefe-95f6-41c8-b880-492fe497ba0b", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/b7c4aefe-95f6-41c8-b880-492fe497ba0b", result.getAccepted().taxonId);
        assertEquals("Earias", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.140, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    // Multiple mis-namings; way too much wrong with this
    @Test
    public void testProblemSearch13() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia continua";
        template.scientificNameAuthorship = "Benth.";
        template.kingdom = "Plantae";
        template.class_ = "Flora";
        template.order = "Flora";
        template.family = "Fabaceae (Mimosoideae)";
        template.genus = "Acacia";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2907413", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2907413", result.getAccepted().taxonId);
        assertEquals("Acacia continua", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.867, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(BayesianTerm.illformedData, AlaLinnaeanFactory.REMOVED_FAMILY, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Re-focussed on synonym
    @Test
    public void testProblemSearch14() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acanthus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2892448", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2892448", result.getAccepted().taxonId);
        assertEquals("Acanthus", result.getAccepted().scientificName);
        assertEquals(0.981, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }


    @Test
    public void testProblemSearch15() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Banksia collina";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/838699", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2900678", result.getAccepted().taxonId);
        assertEquals("Banksia spinulosa var. collina", result.getAccepted().scientificName);
        assertEquals(0.918, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Synonym links to different heirarchy
    @Test
    public void testProblemSearch16() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acanthophysium pulvinatum G. Cunn. 1963";
        template.kingdom = "Fungi";
        template.class_ = "Agaricomycetes";
        template.order = "Russulales";
        template.family = "Stereaceae";
        template.genus = "Acanthophysium";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-109917", result.getMatch().taxonId);
        assertEquals("NZOR-6-80058", result.getAccepted().taxonId);
        assertEquals("Dendrothele pulvinata", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    @Test
    public void testProblemSearch17() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Dolicholobium latifolium A.Gray";
        template.kingdom = "Plantae";
        template.class_ = "Magnoliopsida";
        template.order = "Gentianales";
        template.family = "Rubiaceae";
        template.genus = "Dolicholobium";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getAccepted().taxonId);
        assertEquals("Rubiaceae", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.158, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.CANONICAL_NAME, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    @Test
    public void testProblemSearch18() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Clathria 1";
        template.kingdom = "Animalia";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e2416d08-f925-4ead-a1e0-b456c79faa17", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e2416d08-f925-4ead-a1e0-b456c79faa17", result.getAccepted().taxonId);
        assertEquals("Clathria", result.getAccepted().scientificName);
        assertEquals(0.998, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.394, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    // Parent and child of the same name
    @Test
    public void testProblemSearch19() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Greenidea Schouteden, 1905";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/320d2e8b-d0f8-44df-a66c-c1ea01df3570", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/320d2e8b-d0f8-44df-a66c-c1ea01df3570", result.getAccepted().taxonId);
        assertEquals("Greenidea", result.getAccepted().scientificName);
        assertEquals(0.917, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Not finding family
    @Test
    public void testProblemSearch20() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Schradera";
        template.genus = "Schradera";
        template.family = "Rubiaceae";
        template.taxonRank = Rank.GENUS;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getAccepted().taxonId);
        assertEquals("Rubiaceae", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.222, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    // Should find genus. This goes into weird la-la land thanks to synonyms
    @Test
    public void testProblemSearch21() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Polynemus sextarius Bloch & Schneider, 1801";
        template.kingdom = "Animalia";
        template.family = "Polynemidae";
        template.genus = "Polynemus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/b19c506f-17a4-4e50-8d9f-b76cf225c08a", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/b19c506f-17a4-4e50-8d9f-b76cf225c08a", result.getAccepted().taxonId);
        assertEquals("POLYNEMIDAE", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.256, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Strange name change
    @Test
    public void testProblemSearch22() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Laccaria sp. A";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60093449", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60093449", result.getAccepted().taxonId);
        assertEquals("Laccaria", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.279, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    // Prefixed rank marker
    @Test
    public void testProblemSearch23() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "fam. Apocynaceae gen. Alyxia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51430965", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51430965", result.getAccepted().taxonId);
        assertEquals("Alyxia", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Wrong order and candidate with missing family leading to inaccurate match
    @Test
    public void testProblemSearch24() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Strombus maculatus Sowerby, 1842";
        template.scientificNameAuthorship = "Sowerby, 1842";
        template.class_ = "Gastropoda";
        template.order = "Sorbeoconcha"; // informal in index
        template.family = "Strombidae";
        template.genus = "Strombus";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/fb36a7b1-415b-45d4-a49c-1735f2fb1cf8", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/fb36a7b1-415b-45d4-a49c-1735f2fb1cf8", result.getAccepted().taxonId);
        assertEquals("STROMBIDAE", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.220, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.REMOVED_FAMILY, AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Class in right position
    @Test
    public void testProblemSearch25() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eulalia sp1";
        template.class_ = "Polychaeta";
        template.taxonRank = Rank.GENUS;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/853559b7-9aa8-422a-a1d2-e71fffc35bad", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/853559b7-9aa8-422a-a1d2-e71fffc35bad", result.getAccepted().taxonId);
        assertEquals("Eulalia", result.getAccepted().scientificName);
        assertEquals(0.908, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.626, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    // Subgenus name
    @Test
    public void testProblemSearch26() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Aedes (Finlaya) fuscitarsis";
        template.scientificNameAuthorship = "Belkin, 1962";
        template.genus = "Aedes (Finlaya)";
        template.family = "Culicidae";
        template.order = "Diptera";
        template.class_ = "Insecta";
        template.kingdom = "Animalia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/7d85e130-26ab-4ba6-a2da-63db97698f57", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/7d85e130-26ab-4ba6-a2da-63db97698f57", result.getAccepted().taxonId);
        assertEquals("Aedes", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.493, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_AUTHORSHIP, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // CF at end of name
    @Test
    public void testProblemSearch27() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Callogobius clitellus cf";
        template.kingdom = "Animalia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/d019cab5-8ace-4040-9220-cc090f5e9885", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/d019cab5-8ace-4040-9220-cc090f5e9885", result.getAccepted().taxonId);
        assertEquals("Callogobius", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.321, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CONFER_SPECIES_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Should match genus
    @Test
    public void testProblemSearch28() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Mynes geoffroyi guerini";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/583400fe-96b6-4f5a-a696-f49c8ef3e4ff", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/cdeb6478-0066-4df2-9ea5-3be44a0b6036", result.getAccepted().taxonId);
        assertEquals("Mynes", result.getMatch().scientificName);
        assertEquals("Symbrenthia", result.getAccepted().scientificName);
        assertEquals(0.941, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.163, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    // Should match Acacia paraneura
    @Test
    public void testProblemSearch29() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia ? paraneura hybrid";
        template.genus = "Acacia";
        template.family = "Fabaceae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2892942", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2892942", result.getAccepted().taxonId);
        assertEquals("Acacia paraneura", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(BayesianTerm.illformedData, AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Should match Amylocystis lapponicus
    @Test
    public void testProblemSearch30() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Amylocystis lapponica (Romell) Bondartsev & Singer";
        template.genus = "Amylocystis";
        template.family = "Dacryobolaceae";
        template.order = "Polyporales";
        template.class_ = "Agaricomycetes";
        template.kingdom = "Fungi";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-45059", result.getMatch().taxonId);
        assertEquals("NZOR-6-45059", result.getAccepted().taxonId);
        assertEquals("Amylocystis lapponicus", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.842, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    // Should match Genus
    @Test
    public void testProblemSearch31() throws Exception {
         AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Hydnoplicata whitei";
        template.kingdom = "Fungi";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-73333", result.getMatch().taxonId);
        assertEquals("NZOR-6-107291", result.getAccepted().taxonId);
        assertEquals("Hydnoplicata whitei", result.getMatch().scientificName);
        assertEquals("Hydnoplicata convoluta", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Should match Genus
    @Test
    public void testProblemSearch32() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "*";
        template.kingdom = "Animalia";
        template.class_ = "Malacostraca";
        template.order = "Decapoda";
        template.family = "Parthenopidae";
        template.genus = "Parthenope";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/34bb22f3-e96e-47ff-b7b1-8bfe73a7a150", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/34bb22f3-e96e-47ff-b7b1-8bfe73a7a150", result.getAccepted().taxonId);
        assertEquals("Parthenope", result.getMatch().scientificName);
        assertEquals("Parthenope", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(BayesianTerm.illformedData, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Wierd name next door
    @Test
    public void testProblemSearch33() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "'wombat'";
        template.class_ = "Diplopoda";
        template.order = "Polydesmida";
        template.family = "Paradoxosomatidae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/7d013b86-3811-4df2-a8e3-cb7ec9722f66", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/7d013b86-3811-4df2-a8e3-cb7ec9722f66", result.getAccepted().taxonId);
        assertEquals("PARADOXOSOMATIDAE", result.getMatch().scientificName);
        assertEquals("PARADOXOSOMATIDAE", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.308, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(BayesianTerm.illformedData, AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testProblemSearch34() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eucalyptus aff. baueriana (Werribee)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732879", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732879", result.getAccepted().taxonId);
        assertEquals("Eucalyptus", result.getMatch().scientificName);
        assertEquals("Eucalyptus", result.getAccepted().scientificName);
        assertEquals(0.998, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.087, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.AFFINITY_SPECIES_NAME), result.getIssues());
    }

    @Test
    public void testProblemSearch35() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eucalyptus sp. Killarney  ";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2901570", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2901570", result.getAccepted().taxonId);
        assertEquals("Eucalyptus sp. Killarney (C.R.Michell 2403)", result.getMatch().scientificName);
        assertEquals("Eucalyptus sp. Killarney (C.R.Michell 2403)", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.BARE_PHRASE_NAME), result.getIssues());
    }

    @Ignore("Pocaeae is a homonym")
    public void testProblemSearch36() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Hordelymus europaeus (L.) Harz";
        template.scientificNameAuthorship = "(L.) Harz";
        template.family = "Poaceae";
        template.genus = "Hordelymus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/7d013b86-3811-4df2-a8e3-cb7ec9722f66", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/7d013b86-3811-4df2-a8e3-cb7ec9722f66", result.getAccepted().taxonId);
        assertEquals("PARADOXOSOMATIDAE", result.getMatch().scientificName);
        assertEquals("PARADOXOSOMATIDAE", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.462, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(BayesianTerm.illformedData, AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

     @Ignore("Pocaeae is a homonym")
    public void testProblemSearch37() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Luziola divergens Swallen";
        template.scientificNameAuthorship = "Swallen";
        template.family = "Poaceae";
        template.genus = "Luziola";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/7d013b86-3811-4df2-a8e3-cb7ec9722f66", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/7d013b86-3811-4df2-a8e3-cb7ec9722f66", result.getAccepted().taxonId);
        assertEquals("PARADOXOSOMATIDAE", result.getMatch().scientificName);
        assertEquals("PARADOXOSOMATIDAE", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.462, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(BayesianTerm.illformedData, AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Combined misspelling and invalid family
    @Test
    public void testProblemSearch38() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Tulostoma macowani Bres.";
        template.family = "Tulostomataceae";
        template.genus = "Tulostoma";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60092111", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60092111", result.getAccepted().taxonId);
        assertEquals("Tulostoma macowanii", result.getMatch().scientificName);
        assertEquals("Tulostoma macowanii", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.769, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME, AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME), result.getIssues());
    }

    // Incorrect rank with a strange synonym gumming up the works
    @Test
    public void testProblemSearch39() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eucalyptus";
        template.family = "Myrtaceae";
        template.genus = "Eucalyptus";
        template.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732879", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51732879", result.getAccepted().taxonId);
        assertEquals("Eucalyptus", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(0.99850, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.444, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    // Embedded vernacular name
    @Test
    public void testProblemSearch40() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Phascolarctos cinereus (Koala)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e9d6fbbd-1505-4073-990a-dc66c930dad6", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e9d6fbbd-1505-4073-990a-dc66c930dad6", result.getAccepted().taxonId);
        assertEquals("Phascolarctos cinereus", result.getAccepted().scientificName);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.818, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_AUTHORSHIP, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    // Embedded vernacular name
    @Test
    public void testProblemSearch41() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Phascolarctos cinereus ( Koala )";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e9d6fbbd-1505-4073-990a-dc66c930dad6", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e9d6fbbd-1505-4073-990a-dc66c930dad6", result.getAccepted().taxonId);
        assertEquals("Phascolarctos cinereus", result.getAccepted().scientificName);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.818, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_AUTHORSHIP, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // Embedded distribution
    @Test
    public void testProblemSearch42() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Phascolarctos cinereus (combined populations of Qld, NSW and the ACT)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("ALA_DR22912_357", result.getMatch().taxonId);
        assertEquals("ALA_DR22912_357", result.getAccepted().taxonId);
        assertEquals("Phascolarctos cinereus (combined populations of Qld, NSW and the ACT)", result.getAccepted().scientificName);
        assertEquals(Rank.SUBSPECIES, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Grasses are a problem because of incorrect species lists
    @Test
    public void testProblemSearch43() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Poaceae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51644646", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51644646", result.getAccepted().taxonId);
        assertEquals("Poaceae", result.getAccepted().scientificName);
        assertEquals(Rank.FAMILY, result.getAccepted().taxonRank);
        assertEquals(0.922, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    // Family/genus ignored. Family not present. Genus is subgenus that is synonymised to another genus
    // Matched order is a synonym
    @Test
    public void testProblemSearch44() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificNameAuthorship = "Froggatt,";
        template.phylum = "Arthropoda";
        template.order = "Isoptera";
        template.family = "Calotermitidae";
        template.genus = "Calotermes (Glyptotermes)";
        template.locationId = new HashSet<>(Arrays.asList("http://vocab.getty.edu/tgn/7006238", "http://vocab.getty.edu/tgn/7000490")); // Vic, Australia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/db72349c-e9ce-4287-9f21-4332774ca33e", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/bf0614d9-26df-43f0-bfe8-b6d16d5d640a", result.getAccepted().taxonId);
        assertEquals("Termitoidae", result.getAccepted().scientificName);
        assertNull(result.getAccepted().taxonRank); // Odd
        assertEquals(0.979, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.2, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_LOCATION), result.getIssues());
    }

    // FMatch from Acacia to Acakia on badly formatted phrase name
    @Test
    public void testProblemSearch45() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia sp. blue mulga (C.R.Dunlop 1951)";
        template.scientificNameAuthorship = "(C.R.Dunlop 1951)";
        template.family = "FABACEAE";
        template.genus = "Acacia";
        template.locationId = new HashSet<>(Arrays.asList("http://vocab.getty.edu/tgn/7001829", "http://vocab.getty.edu/tgn/7000490")); // NT, Australia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51471290", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51471290", result.getAccepted().taxonId);
        assertEquals("Acacia", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.335, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    // Outside scope of locality
    @Test
    public void testProblemSearch46() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Adiantum";
        template.scientificNameAuthorship = "L.";
        template.vernacularName = "Huruhuru tapairu";
        template.taxonRank = Rank.GENUS;
        template.kingdom = "Plantae";
        template.phylum = "Charophyta";
        template.class_ = "Equisetopsida";
        template.family = "Pteridaceae";
        template.genus = "Adiantum";
        template.locationId = new HashSet<>(Arrays.asList("http://vocab.getty.edu/tgn/1000225", "http://vocab.getty.edu/tgn/1000006")); // Vanuatu, Oceania
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51266356", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51266356", result.getAccepted().taxonId);
        assertEquals("Adiantum", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.LOCATION_OUT_OF_SCOPE), result.getIssues());
    }

    // Ensure correct preferred vernacular name
    @Test
    public void testProblemSearch47() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Falco (Hierofalco) peregrinus ";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/083b413f-8746-4788-8dc1-3da495d78a79", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/083b413f-8746-4788-8dc1-3da495d78a79", result.getAccepted().taxonId);
        assertEquals("Peregrine Falcon", result.getAccepted().vernacularName);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // In CoL but an insect so not included
    @Test
    public void testProblemSearch48() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Spilosoma canescens";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
    }


    // Gonyaulacales no longer in index
    @Test
    public void testProblemSearch49() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Tripos limulus";
        template.kingdom = "Chromista";
        template.class_ = "Peridinea";
        template.order = "Gonyaulacales";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-70818", result.getMatch().taxonId);
        assertEquals("NZOR-6-70818", result.getAccepted().taxonId);
        assertEquals("Peridinea", result.getAccepted().scientificName);
        assertEquals(Rank.CLASS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.200, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_LOCATION), result.getIssues());
    }

    //
    // 20230725-3 Tubificidae is a synonym of Tubificinae
    // Tubificinae is subfamily with family Naididae, order Oligochaeta
    // Naididae only has Tubificinae as a synonym
    @Test
    public void testProblemSearch50() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.class_ = "Clitellata";
        template.order = "Haplotaxida";
        template.family = "Tubificidae";
        template.genus = "Phallodrilus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/475a2fd0-99b5-43de-93b4-eb38b62db7ef", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/222713ad-13c9-47b4-8d71-52317f7f12f0", result.getAccepted().taxonId);
        assertEquals("Tubificinae", result.getAccepted().scientificName);
        assertEquals(Rank.SUBFAMILY, result.getAccepted().taxonRank);
        assertEquals(0.971, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.077, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }


    // 20230725-3 Acmaea is a synonym of Maratus (Arachnid, not Gastropod)
    @Test
    public void testProblemSearch51() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acmaea testudinalis Müller, 1776";
        template.scientificNameAuthorship = "Müller, 1776";
        template.class_ = "Gastropoda";
        template.order = "Patellogastropoda";
        template.family = "Acmaeidae";
        template.genus = "Acmaea";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/c454ea8b-a735-4ff0-8c66-81bac372282b", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/c454ea8b-a735-4ff0-8c66-81bac372282b", result.getAccepted().taxonId);
        assertEquals("PATELLOGASTROPODA", result.getAccepted().scientificName);
        assertEquals(Rank.SUBCLASS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.176, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_LOCATION, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // 20230725-3 Anseres (Swans) not in index, previously in NZOR
    @Test
    public void testProblemSearch52() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Anseres (Swans)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
    }

    // 20230725-3 Callionymus limiceps present as miscellaneous literature and as a synonym
    @Test
    public void testProblemSearch53() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Callionymus limiceps";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/8637e3f0-c4dd-4e31-9cae-f23d5c97f511", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/2807f3b7-bd1a-47d0-a440-bbfa73a467ee", result.getAccepted().taxonId);
        assertEquals("Repomucenus limiceps", result.getAccepted().scientificName);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
        assertEquals(0.800, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());
    }

    // 20230725-3 Ferrissia (Pettancylus) tasmanicus is in index
    // Combination of epithet change and supplied family not present match Planorbidae
    @Test
    public void testProblemSearch54() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Ferrissia (Pettancylus) tasmanica (Tenison-Woods, 1876)";
        template.class_ = "Gastropoda";
        template.family = "Ancylidae";
        template.genus = "Ferrissia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3ffd6043-30d4-4c33-9e84-84c0373a948f", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/3ffd6043-30d4-4c33-9e84-84c0373a948f", result.getAccepted().taxonId);
        assertEquals("Ferrissia", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.257, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_FAMILY, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    // 20230725-3 "Genus" is in index
    @Test
    public void testProblemSearch55() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Genus sp. brown cup fungi";
        template.family = "Family";
        template.genus = "Genus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
    }

    // 20230725-3 Gibbomodiola albicostus is in index
    // Order is MYTILIDA, Mytiloida is a synonym
    @Test
    public void testProblemSearch56() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Gibbomodiola albicosta";
        template.scientificNameAuthorship = "(Lamarck, 1819)";
        template.kingdom = "Animalia";
        template.class_ = "Bivalvia";
        template.order = "Mytiloida";
        template.family = "Mytilidae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/76b37192-3c44-4c84-b7ca-bd7cc44a82db", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/76b37192-3c44-4c84-b7ca-bd7cc44a82db", result.getAccepted().taxonId);
        assertEquals("Gibbomodiola albicostus", result.getAccepted().scientificName);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.842, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME), result.getIssues());
    }

    // 20230725-3 Not in index but potential homonym issue
    @Test
    public void testProblemSearch57() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acherontia styx Westwood, 1844";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(BayesianTerm.invalidMatch), result.getIssues());
        template = new AlaLinnaeanClassification();
        template.scientificName = "Acherontia styx";
        result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(BayesianTerm.invalidMatch), result.getIssues());
     }

    // 20230725-3 Acanthocanthopsis quadrata is in index
    // Order is now Cyclopoida
    @Test
    public void testProblemSearch58() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acanthocanthopsis quadratus Heegaard, 1945";
        template.kingdom = "Animalia";
        template.class_ = "Maxillopoda";
        template.order = "Poecilostomatoida";
        template.family = "Chondracanthidae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/a02f625d-9894-449c-83b8-57b2b3e9a16f", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/a02f625d-9894-449c-83b8-57b2b3e9a16f", result.getAccepted().taxonId);
        assertEquals("Acanthocanthopsis", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.331, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // 20230725-3 Baseodiscus quinquelineatus (Quoy & Gaimard, 1833) is in index
    // Family is VALENCINIIDAE class is NEMERTINEA differnece is too much (one or the other, please)
    @Test
    public void testProblemSearch59() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Baseodiscus quinquelineatus (Quoy & Gaimard, 1833)";
        template.class_ = "Anopla";
        template.family = "Baseodiscidae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
    }

    // Campanella is homonym, Fungi should disambiguate
    @Test
    public void testProblemSearch60() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Campanella sp.";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60094123", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60094123", result.getAccepted().taxonId);
        assertEquals("Campanella", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(0.505, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    // 20230725-3 Lophozia bicrenata is present as synonym however family and class do not match, so section is found instead
    @Test
    public void testProblemSearch61() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Lophozia bicrenata";
        template.scientificNameAuthorship = "0";
        template.genus = "Lophozia";
        template.family = "Jungermanniaceae";
        template.order = "Jungermanniales";
        template.class_ = "Hepatopsida";
        template.kingdom = "Plantae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-45069", result.getMatch().taxonId);
        assertEquals("NZOR-6-45069", result.getAccepted().taxonId);
        assertEquals("Jungermannia sect. Lophozia", result.getAccepted().scientificName);
        assertEquals(Rank.SECTION_BOTANY, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.342, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_ORDER, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    // 20230725-3 Halymeniaceae is present with a distribution of NZ
    @Test
    public void testProblemSearch62() throws Exception {
         AlaLocationClassification loc = new AlaLocationClassification();
        loc.country = "Australia";
        loc.countryCode = "AU";
        loc.stateProvince = "Western Australia";
        Match<AlaLocationClassification, MatchMeasurement> location = this.searcher.search(loc);
        assertTrue((location.isValid()));
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "? Corynomorpha sp.";
        template.genus = "Corynomorpha";
        template.family = "Halymeniaceae";
        template.locationId = location.getAllIdentifiers();
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-42069", result.getMatch().taxonId);
        assertEquals("NZOR-6-42069", result.getAccepted().taxonId);
        assertEquals("Halymeniaceae", result.getAccepted().scientificName);
        assertEquals(Rank.FAMILY, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.143, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_LOCATION, AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    // 20230725-3 Anguilla location is Australia only by AFD, available in NZOR in the past but not in currrent taxon variants
    @Test
    public void testProblemSearch63() throws Exception {
        AlaLocationClassification loc = new AlaLocationClassification();
        loc.country = "New Zealand";
        loc.waterBody = "Cannington C";
        Match<AlaLocationClassification, MatchMeasurement> location = this.searcher.search(loc);
        assertTrue((location.isValid()));
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Anguilla dieffenbachii";
        template.vernacularName = "Longfin eel";
        template.locationId = location.getAllIdentifiers();
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/93462e74-3469-470c-8720-6634e3ddd99e", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/93462e74-3469-470c-8720-6634e3ddd99e", result.getAccepted().taxonId);
        assertEquals("Anguilla", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.274, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_LOCATION), result.getIssues());
    }

    // 20230725-3 Litoria watjulumensis present with NT distribution (note a not o) and family of Pelodryadidae
    // Hylidae does not appear to be present or a synonym of Pelodryadidae
    @Test
    public void testProblemSearch64() throws Exception {
        AlaLocationClassification loc = new AlaLocationClassification();
        loc.country = "Australia";
        loc.stateProvince = "Northern Territory";
        Match<AlaLocationClassification, MatchMeasurement> location = this.searcher.search(loc);
        assertTrue((location.isValid()));
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Litoria wotjulumensis";
        template.genus = "Litoria";
        template.family = "Hylidae";
        template.order = "Anura";
        template.phylum = "Chordata";
        template.kingdom = "Animalia";
        template.locationId = location.getAllIdentifiers();
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/8caedef5-8554-41c9-b26d-e66b8010367c", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/8caedef5-8554-41c9-b26d-e66b8010367c", result.getAccepted().taxonId);
        assertEquals("Litoria", result.getAccepted().scientificName);
        assertEquals(Rank.GENUS, result.getAccepted().taxonRank);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.369, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.REMOVED_FAMILY, AlaLinnaeanFactory.REMOVED_ORDER), result.getIssues());
    }

    @Test
    public void testMultipleMatches1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Cygnus atratus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e848916c-5f2d-43ef-8cba-8e32a9034377", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/e848916c-5f2d-43ef-8cba-8e32a9034377", result.getAccepted().taxonId);
        assertEquals("Cygnus atratus", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());
    }

    @Test
    public void testRemoveSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Echinochaete brachypora";
        template.order = "Somethingales";
        template.kingdom = "Fungi";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60098663", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.846, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_ORDER), result.getIssues());
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
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60096937", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.684, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_PHYLUM, AlaLinnaeanFactory.REMOVED_CLASS, AlaLinnaeanFactory.REMOVED_ORDER), result.getIssues());
    }

    @Test
    public void testRemoveSearch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Canarium acutifolium var. acutifolium";
        template.genus = "Canarim"; // Canarium
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/apni/2904909", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.667, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME), result.getIssues());
    }

    @Test
    public void testSoundexSearch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Poodytes gramina"; // Poodytes gramineus
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://biodiversity.org.au/afd/taxa/b4c2ec8d-918d-4fb4-8ef9-f6d5d3bbceda", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.667, result.getFidelity().getFidelity(), 0.001);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME));
    }

    @Test
    public void testMisappliedName1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        // Other possible names: Callisia elegans, Caladium macrorrhizon, Buchanania florida, Boussingaultia baselloides
        template.scientificName = "Abroma augustum";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        // The actusl value may change bewteen instances of the index here, since there are multiple possibilities
        assertEquals("https://id.biodiversity.org.au/instance/apni/3463078", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2904866", result.getAccepted().taxonId);
        assertEquals(0.994, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(TaxonomicStatus.misapplied, result.getMatch().taxonomicStatus);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISAPPLIED_NAME), result.getIssues());
    }

    @Test
    public void testMisappliedName2() throws Exception {
        //test to ensure that the accepted name is returned when it also exists as a misapplied name.
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bertya rosmarinifolia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/apni/2893214", result.getAccepted().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testMisappliedName3() throws Exception {
        //test to ensure that the accepted name is returned when it also exists as a misapplied name.
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia acuminata Benth.";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51285812", result.getAccepted().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testMisappliedName4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia bivenosa DC.";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertNotNull(result.getAccepted());
        assertEquals("https://id.biodiversity.org.au/node/apni/2912987", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testSynonymAsHomonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Abelia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51432946", result.getAccepted().taxonId);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    // This one is a pain, since there's a series and a subsection. Good luck.
    // Unable to distinguish between series and subsection given accuracy limits of RankID
    @Ignore("20230725-3 Goodenia subsect. Bracteolatae no longer in index")
    public void testSynonymAsHomonym2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bracteolatae";
        template.taxonRank = Rank.SERIES;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/89092", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME, AlaLinnaeanFactory.MULTIPLE_MATCHES), result.getIssues());
    }

    @Test
    public void indeterminateNameTest1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acacia sp";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51471290", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void indeterminateNameTest2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Insecta fam.";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/17c9fd64-3c07-4df5-a33d-eda1e065e99f", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
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
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/f0b9ceab-3466-4e65-a6ec-eb27810e9394", result.getAccepted().taxonId);
        assertEquals(Rank.FAMILY, result.getAccepted().taxonRank);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.HIGHER_ORDER_MATCH));
    }

    @Test
    public void testSpeciesSplitSynonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corvus orru";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/2c5fd509-d4d6-4adb-9566-96280ff9e6af", result.getAccepted().taxonId);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
        assertTrue(result.getIssues().contains(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM));
    }

    @Test
    public void testEmbeddedRankMarker1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Flueggea virosa subsp. melanthesoides";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2893899", result.getAccepted().taxonId);
        assertEquals(Rank.SUBSPECIES, result.getAccepted().taxonRank);
    }

    @Test
    public void testEmbeddedRankMarker2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thelymitra sp. adorata";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51414212", result.getAccepted().taxonId);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
    }

    @Test
    public void testEmbeddedRankMarker3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Grevillea brachystylis subsp. Busselton (G.J.Keighery s.n. 28/8/1985)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/897499", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2918130", result.getAccepted().taxonId);
        assertEquals(Rank.SUBSPECIES, result.getAccepted().taxonRank);
    }

    @Test
    public void testEmbeddedRankMarker4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Lindernia sp. Pilbara (M.N.Lyons & L.Lewis FV 1069)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/51306553", result.getAccepted().taxonId);
        assertEquals(Rank.SPECIES, result.getAccepted().taxonRank);
    }

    @Test
    public void testExcludedNames1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Cyrtodactylus louisiadensis";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/75091b4a-30df-4a12-a9b2-5b282a9785a4", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/a8399b0f-1dc7-450a-bc76-54664c092080", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.EXCLUDED_NAME), result.getIssues());
    }

    @Ignore("20230725-3 Zygophyllum sessilifolium no longer in index (although visible in APC?)")
    public void testExcludedNames2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Zygophyllum sessilifolium";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51448994", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.EXCLUDED_NAME), result.getIssues());
    }

    @Ignore("Now a heterotypic synonym in 20230725-2")
    public void testExcludedNames3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Callistemon pungens";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2909631", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.PARTIALLY_EXCLUDED_NAME), result.getIssues());
    }


    // Unable to resolve the two versions
    // One is Aves (it's a bird, numbnuts) and one is the subgenus Phalacrocorax (Phalacrocorax) - Cormorants
    @Test
    public void testExcludedNames4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Carbo ater";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/416d9a5b-b43a-4ed3-9431-b0e6e7a693d4", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/9e27e64f-407b-4050-96ef-4ea4381b1554", result.getAccepted().taxonId);
        assertEquals(0.941, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.222, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    @Test
    public void testHomonymsWithResolution1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
    }

    @Test
    public void testHomonymsWithResolution2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.kingdom = "Animalia";
        template.phylum = "Chordata";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
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
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2908051", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testHomonymsWithResolution4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.scientificNameAuthorship = "Blumenbach, 1798";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/52c68649-47d5-4f2e-9730-417fc54fb080", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testHomonymsWithResolution5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.scientificNameAuthorship = "Blumenbach";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/52c68649-47d5-4f2e-9730-417fc54fb080", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testHomonymsWithResolution6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thalia";
        template.family = "Marantaceae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2908051", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testParentChildSynonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Geopelia placida";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3d5c4e0d-5138-46e0-8e14-5acd8fd2c523", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testParentChildSynonym2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Corvus orru";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/2c5fd509-d4d6-4adb-9566-96280ff9e6af", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testParentChildSynonym3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Phoma lobeliae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60108344", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testParentChildSynonym4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Geopelia placida";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/3d5c4e0d-5138-46e0-8e14-5acd8fd2c523", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.PARENT_CHILD_SYNONYM, AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testUncertainName1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Darwinia acerosa?";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2919768", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testUncertainName2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Swainsona cf. luteola";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/51316648", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.CONFER_SPECIES_NAME), result.getIssues());
    }

    @Test
    public void testUncertainName3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Paraminabea aff. aldersladei";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/ae347200-36e4-4bef-baef-02bd97f962ba", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }



    @Test
    public void testUncertainName4() throws Exception  {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Carex aff. tereticaulis (Lake Omeo)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51460185", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51460185", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.044, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());

        // Match onto higher order without further info
        template = new AlaLinnaeanClassification();
        template.scientificName = "Carex aff. tereticaulis";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51460185", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51460185", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.067, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());

        // Actual taxon
        template = new AlaLinnaeanClassification();
        template.scientificName = "Carex tereticaulis";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2919780", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2919780", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }


    // Caused by a homonym in Buchnera (plant, bacteria)
    @Test
    public void testUncertainName5() throws Exception  {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Buchnera aff. asperata";
        template.genus = "Buchnera";
        template.family = "Orobanchaceae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51442060", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51442060", result.getAccepted().taxonId);
        assertEquals("Buchnera", result.getAccepted().scientificName);
        assertEquals(0.937, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.304, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH, AlaLinnaeanFactory.AFFINITY_SPECIES_NAME), result.getIssues());
    }

    @Test
    public void testSensuStrictoMarker1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Oenochrominae s. str.";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/d0105fd4-2e0a-4fcd-8dea-37828853f535", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSensuStrictoMarker2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Pterodroma arminjoniana s. str.";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("ALA_DR22912_1271", result.getMatch().taxonId);
        assertEquals("ALA_DR22912_1271", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.667, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME), result.getIssues());
    }


    // Multiple vouchers for Elaeocarpus sp. Rocky Creek
    // (Hunter s.n. 16 Sep 1993) and (G.Read AQ 562114)
    @Test
    public void testPhraseMatch1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Elaeocarpus sp. Rocky Creek";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/871103", result.getMatch().taxonId); // Variable
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51435153", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.SYNTHETIC_MATCH, AlaLinnaeanFactory.BARE_PHRASE_NAME), result.getIssues());
    }

    @Test
    public void testPhraseMatch2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Elaeocarpus sp. Rocky Creek (Hunter s.n. 16 Sep 1993)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/871103", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2916168", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testPhraseMatch3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Pultenaea sp. Olinda (R.Coveny 6616)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2886985", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testPhraseMatch4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Goodenia sp. Bachsten Creek (M.D.Barrett 685) WA Herbarium";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2890349", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testPhraseMatch5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Baeckea sp. Bungalbin Hill (B.J.Lepschi & L.A.Craven 4586)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/51440049", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51440054", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testPhraseMatch6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Astroloma sp. Cataby (E.A.Griffin 1022)";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/7178434", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testAcceptedSynonym1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Thelymitra sp. adorata";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        // Has a nom inval version as well
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51414212", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }


    @Test
    public void testAcceptedSynonym2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.genus = "Acanthus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2892448", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2892448", result.getAccepted().taxonId);
        assertEquals("Acanthus", result.getAccepted().scientificName);
        assertEquals(0.998, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    @Test
    public void testKingdom1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Animalia";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/4647863b-760d-4b59-aaa1-502c8cdf8d3c", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testKingdom2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bacteria";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://www.catalogueoflife.org/data/taxon/B", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testHomonyms1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Agathis";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
    }


    @Test
    public void testHomonyms2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Agathis";
        template.nomenclaturalCode = NomenclaturalCode.BOTANICAL;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51299766", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Merged classifications for Protozoa and Chromista
    @Test
    public void testHomonyms3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Pseudoholophrya";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/4142a5ce-1e40-4e8f-8f94-5683f3816b23", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Merged classifications for Protozoa and Chromista
    @Test
    public void testHomonyms4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Pseudoholophrya";
        template.kingdom = "Chromista";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/4142a5ce-1e40-4e8f-8f94-5683f3816b23", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.EXPANDED_KINGDOM), result.getIssues());
        template = new AlaLinnaeanClassification();
        template.scientificName = "Pseudoholophrya";
        template.kingdom = "Protozoa";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/4142a5ce-1e40-4e8f-8f94-5683f3816b23", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testHomonyms5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Serpula";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertFalse(result.isValid());
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM, BayesianTerm.invalidMatch), result.getIssues());
    }

    @Test
    public void testHomonyms6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Serpula";
        template.phylum = "Annelida";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/e15c4f10-c856-42a2-bda1-0419a5dcb641", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    // No longer homonym because of ambiguous nom codes
    @Test
    public void testHomonyms7() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Patellina";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/8d6a5e0e-03c4-4333-ae22-42daea4d01c6", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Potential problem with multiple taxa with similar names
    @Test
    public void testHomonyms8() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Rubiaceae";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getAccepted().taxonId);
        assertEquals("Rubiaceae", result.getAccepted().scientificName);
        assertEquals(0.999, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }


    // Make a clear rank determination
    @Test
    public void testHomonyms9() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Rubiaceae";
        template.taxonRank = Rank.FAMILY;
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446612", result.getAccepted().taxonId);
        assertEquals("Rubiaceae", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Cross-rank homonym
    @Test
    public void testHomonyms10() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Bdelloidea";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/6e95cd75-80b9-41ce-8dd2-eae29a8f7e1b", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/6e95cd75-80b9-41ce-8dd2-eae29a8f7e1b", result.getAccepted().taxonId);
        assertEquals("BDELLOIDEA", result.getAccepted().scientificName);
        assertEquals(0.667, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.UNRESOLVED_HOMONYM), result.getIssues());
    }



    // Class in right position
    @Test
    public void testHomonyms11() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.order = "Eugregarinorida";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/065763e7-e00d-4e5d-a9d0-99b290c4b2c8", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/065763e7-e00d-4e5d-a9d0-99b290c4b2c8", result.getAccepted().taxonId);
        assertEquals("EUGREGARINORIDA", result.getAccepted().scientificName);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testSpeciesPlural1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Opuntia spp.";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51269889", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51269889", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testCultivar1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Hypoestes phyllostachya 'Splash'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51731549", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51731549", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.HIGHER_ORDER_MATCH), result.getIssues());
    }

    @Ignore("20230725-3 Anigozanthos 'Bush Rebel' from APNI no longer in index")
    public void testCultivar2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Anigozanthos 'Bush Rebel'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/4946384", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testCultivar3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Acianthus sp. 'Gibraltar Range'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/50738493", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testCultivar4() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Conospermum taxifolium 'Tasmanian form'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/name/apni/229673", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testCultivar5() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Grevillea sp aff patulifolia 'Kanangra'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/837807", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2916815", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME, AlaLinnaeanFactory.CANONICAL_NAME), result.getIssues());
    }

    @Test
    public void testCultivar6() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Grevillea sp. nov. 'Belowra'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/837821", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2898070", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testCultivar7() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Oligochaetochilus aff. boormanii 'Coastal'";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/51411288", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51412124", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME), result.getIssues());
    }

    @Test
    public void testVariant1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Eolophus roseicapilla";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
        template.scientificName = "Eolophus roseicapillus";
        result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/9b4ad548-8bb3-486a-ab0a-905506c463ea", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(0.667, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISSPELLED_SCIENTIFIC_NAME), result.getIssues());

    }


    @Test
    public void testSynthetic1() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Amphipogon brownii";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/51442466", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51442480", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.SYNTHETIC_MATCH), result.getIssues());
    }


    @Test
    public void testSynthetic2() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Astrotricha sp. 1";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/3672536", result.getMatch().taxonId); // Variable
        assertEquals("https://id.biodiversity.org.au/node/apni/6923469", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.SYNTHETIC_MATCH), result.getIssues());
    }

    @Test
    public void testSynthetic3() throws Exception {
        AlaLinnaeanClassification template = new AlaLinnaeanClassification();
        template.scientificName = "Leptospermum stellatum var. stellatum";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/906899", result.getMatch().taxonId); // Variable
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51440260", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaLinnaeanFactory.SYNTHETIC_MATCH), result.getIssues());
    }

    @Test
    public void testVernacular1() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Red Kangaroo";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/7e6e134b-2bc7-43c4-b23a-6e3f420f57ad", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/7e6e134b-2bc7-43c4-b23a-6e3f420f57ad", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Multiple possibilities
    @Test
    public void testVernacular2() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Blue Gum"; // Multiple possibilities
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2912252", result.getMatch().taxonId); // Variable
        assertEquals("https://id.biodiversity.org.au/node/apni/2912252", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }


    @Test
    public void testVernacular3() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Aka-kōpū-kererū";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446140", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446140", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacular4() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Aka-kopu-kereru";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446140", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446140", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacular5() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Yellow-tailed Black-Cockatoo";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/145b081d-eca7-4d9b-9171-b97e2d061536", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/145b081d-eca7-4d9b-9171-b97e2d061536", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacular6() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Scarlet Robin";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/a3e5376b-f9e6-4bdf-adae-1e7add9f5c29", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/a3e5376b-f9e6-4bdf-adae-1e7add9f5c29", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Ignore("Not in 20230725-2 vernacular names")
    public void testVernacularSameTaxon1() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Wedge-leaved Rattlepod";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2895442", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/node/apni/2895442", result.getAccepted().taxonId);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testVernacularSameTaxon2() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Waratah";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
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
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/7e6e134b-2bc7-43c4-b23a-6e3f420f57ad", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/7e6e134b-2bc7-43c4-b23a-6e3f420f57ad", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaVernacularFactory.REMOVED_LANGUAGE), result.getIssues());
    }


    @Test
    public void testVernacularMisspelled1() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Red Kangaru";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://biodiversity.org.au/afd/taxa/7e6e134b-2bc7-43c4-b23a-6e3f420f57ad", result.getMatch().taxonId);
        assertEquals("https://biodiversity.org.au/afd/taxa/7e6e134b-2bc7-43c4-b23a-6e3f420f57ad", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaVernacularFactory.MISSPELLED_VERNACULAR_NAME), result.getIssues());
    }


    @Test
    public void testVernacularMisspelled2() throws Exception {
        AlaVernacularClassification template = new AlaVernacularClassification();
        template.vernacularName = "Aka-kopu-kereroo";
        Match<AlaVernacularClassification, MatchMeasurement> result = this.searcher.search(template);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446140", result.getMatch().taxonId);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51446140", result.getAccepted().taxonId);
        assertEquals(Issues.of(AlaVernacularFactory.MISSPELLED_VERNACULAR_NAME), result.getIssues());
    }


    @Test
    public void testIdentifierSearch1() throws Exception {
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search("https://id.biodiversity.org.au/node/fungi/60096937");
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/fungi/60096937", result.getMatch().taxonId);
        assertEquals("Scleroderma aurantium", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertEquals("https://id.biodiversity.org.au/node/fungi/60096937", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testIdentifierSearch2() throws Exception {
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search("https://id.biodiversity.org.au/instance/apni/884491");
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/884491", result.getMatch().taxonId);
        assertEquals("Brunoniaceae", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.heterotypicSynonym, result.getMatch().taxonomicStatus);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51462931", result.getAccepted().taxonId);
        assertEquals("Goodeniaceae", result.getAccepted().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getAccepted().taxonomicStatus);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    @Test
    public void testIdentifierSearch3() throws Exception {
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search("NothingToSeeHere");
        assertFalse(result.isValid());
    }

    @Test
    public void testGetVernacluarNames1() throws Exception {
        List<String> result = this.searcher.getVernacularNames("NothingToSeeHere");
        assertNotNull(result);
        assertEquals(Arrays.asList(), result);
    }

    @Test
    public void testGetVernacluarNames2() throws Exception {
        List<String> result = this.searcher.getVernacularNames("https://biodiversity.org.au/afd/taxa/cd651aef-f210-45b1-8652-3a33581d1beb");
        assertNotNull(result);
        assertEquals(new HashSet<String>(Arrays.asList("Bell's Turtle", "Western Sawshelled Turtle")), new HashSet<String>(result));
    }

    @Test
    public void testGetVernacluarNames3() throws Exception {
        List<String> result = this.searcher.getVernacularNames("https://biodiversity.org.au/afd/taxa/d692b693-42bb-40b7-81db-f7af5d7958a5");
        assertNotNull(result);
        assertEquals(Arrays.asList(), result);
    }

    @Test
    public void testGetVernacluarNames4() throws Exception {
        List<String> result = this.searcher.getVernacularNames("https://id.biodiversity.org.au/taxon/apni/51458346");
        assertNotNull(result);
        assertEquals(Arrays.asList("Mayfly Orchids", "Mosquito Orchids"), result);
    }

    @Test // Orange Roughy should be first as standard name
    public void testGetVernacluarNames5() throws Exception {
        List<String> result = this.searcher.getVernacularNames("https://biodiversity.org.au/afd/taxa/340484bd-33f6-4b46-a63c-751f0b159ed1");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Orange Roughy", result.get(0));
        assertEquals(new HashSet<String>(Arrays.asList("Orange Roughy", "Red Roughy", "Sea Perch", "Orange Ruff", "Deepsea Perch", "Deep-sea Perch")), new HashSet<String>(result));
    }

    @Test
    public void testAutocomplete1() throws Exception {
        List<Autocomplete> result = this.searcher.autocomplete("Elusor", 10, false);
        assertNotNull(result);
        assertEquals(2, result.size());
        Autocomplete autocomplete = result.get(0);
        assertEquals("Elusor", autocomplete.getName());
        assertEquals("https://biodiversity.org.au/afd/taxa/2591a002-27b9-412d-a243-0b8c7cdc66a5", autocomplete.getTaxonId());
        autocomplete = result.get(1);
        assertEquals("Elusor macrurus", autocomplete.getName());
        assertEquals("https://biodiversity.org.au/afd/taxa/d315deea-822c-4f2c-b439-da33d6af5fd6", autocomplete.getTaxonId());
    }

    @Test
    public void testAutocomplete2() throws Exception {
        List<Autocomplete> result = this.searcher.autocomplete("Mary riv", 10, false);
        assertNotNull(result);
        assertTrue(result.size() > 1);
        assertTrue(result.stream().anyMatch(a -> "Mary River Turtle".equals(a.getName())));
        assertTrue(result.stream().anyMatch(a -> "Elusor macrurus".equals(a.getClassification().scientificName)));
        assertTrue(result.stream().anyMatch(a -> "Maccullochella mariensis".equals(a.getClassification().scientificName)));
        assertTrue(result.stream().anyMatch(a -> "Samadera sp. Mary River (I.D.Cowie 1454)".equals(a.getClassification().scientificName)));
    }

    @Test
    public void testAutocomplete3() throws Exception {
        List<Autocomplete> result = this.searcher.autocomplete("Mary river t", 10, false);
        assertNotNull(result);
        assertTrue(result.size() == 1);
        assertTrue(result.stream().anyMatch(a -> "Mary River Turtle".equals(a.getName())));
        assertTrue(result.stream().anyMatch(a -> "Elusor macrurus".equals(a.getClassification().scientificName)));
        assertFalse(result.stream().anyMatch(a -> "Maccullochella mariensis".equals(a.getClassification().scientificName)));
        assertFalse(result.stream().anyMatch(a -> "Samadera sp. Mary River (I.D.Cowie 1454)".equals(a.getClassification().scientificName)));
    }

    @Test
    public void testAutocomplete4() throws Exception {
        List<Autocomplete> result = this.searcher.autocomplete("Acacia d", 50, true);
        assertNotNull(result);
        assertTrue(result.size() > 1);
        assertTrue(result.stream().anyMatch(a -> "Acacia dura".equals(a.getName())));
        assertTrue(result.stream().anyMatch(a -> "Acacia decora".equals(a.getName())));
    }

    @Test
    public void testAutocomplete5() throws Exception {
        List<Autocomplete> result = this.searcher.autocomplete("Acacia", 1, false);
        assertNotNull(result);
        assertEquals(1, result.size());
        Autocomplete autocomplete = result.get(0);
        assertEquals("Acacia", autocomplete.getName());
    }

    @Test
    public void testAutocomplete6() throws Exception {
        List<Autocomplete> result = this.searcher.autocomplete("Mylitta pse", 10, true);
        assertNotNull(result);
        assertEquals(1, result.size());
        Autocomplete autocomplete = result.get(0);
        assertEquals("Mylitta pseudacaciae", autocomplete.getName());
        assertEquals("Hysterangium pseudacaciae", autocomplete.getClassification().scientificName);
        assertNotNull(autocomplete.getSynonyms());
        assertEquals(1, autocomplete.getSynonyms().size());
        assertEquals("Mylitta pseudacaciae", autocomplete.getSynonyms().get(0).getClassification().scientificName);
    }

    @Test
    public void testAutocomplete7() throws Exception {
        List<Autocomplete> result = this.searcher.autocomplete("Mylitta pse", 10, false);
        assertNotNull(result);
        assertEquals(0 ,result.size());
    }

    // Misapplication only applies in Australia
    @Test
    public void testLocalitySearch1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Acaena anserinifolia";
        classification.locationId = Collections.singleton("http://vocab.getty.edu/tgn/7000490"); // Australia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/879572", result.getMatch().taxonId);
        assertEquals("Acaena anserinifolia", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.misapplied, result.getMatch().taxonomicStatus);
        assertEquals("https://id.biodiversity.org.au/node/apni/2906921", result.getAccepted().taxonId);
        assertEquals("Acaena novae-zelandiae", result.getAccepted().scientificName);
        assertEquals(0.935, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.MISAPPLIED_NAME), result.getIssues());
    }

    // Misapplication doesn't apply in New Zealand
    @Test
    public void testLocalitySearch2() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Acaena anserinifolia";
        classification.locationId = Collections.singleton("http://vocab.getty.edu/tgn/1000226"); // New Zealand
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-99180", result.getMatch().taxonId);
        assertEquals("Acaena anserinifolia", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertEquals("NZOR-6-99180", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM), result.getIssues());
    }

    // Somewhere else (outside scope of distribution information)
    @Test
    public void testLocalitySearch3() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Acaena anserinifolia";
        classification.locationId = Collections.singleton("http://vocab.getty.edu/tgn/1000223"); // New Calendonia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("NZOR-6-99180", result.getMatch().taxonId);
        assertEquals("Acaena anserinifolia", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertEquals("NZOR-6-99180", result.getAccepted().taxonId);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.ACCEPTED_AND_SYNONYM, AlaLinnaeanFactory.PARTIALLY_MISAPPLIED_NAME, AlaLinnaeanFactory.LOCATION_OUT_OF_SCOPE), result.getIssues());
    }


    // Sub-national distribution
    @Test
    public void testLocalitySearch4() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Neptunia gracilis gracilis";
        classification.locationId = new HashSet<>(Arrays.asList("http://vocab.getty.edu/tgn/7001828", "http://vocab.getty.edu/tgn/7000490")); // NSW, Australia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/apni/2912936", result.getMatch().taxonId);
        assertEquals("Neptunia gracilis f. gracilis", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }


    // Sub-national distribution via synonym
    // TBD synonym inherits accepted distribution if not present
    @Test
    public void testLocalitySearch5() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Neptunia gracilis gracilis";
        classification.locationId = new HashSet<>(Arrays.asList("http://vocab.getty.edu/tgn/7001992", "http://vocab.getty.edu/tgn/7000490")); // Tas, Australia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/instance/apni/839379", result.getMatch().taxonId);
        assertEquals("Neptunia gracilis var. gracilis", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.homotypicSynonym, result.getMatch().taxonomicStatus);
        assertEquals("https://id.biodiversity.org.au/node/apni/2912936", result.getAccepted().taxonId);
        assertEquals("Neptunia gracilis f. gracilis", result.getAccepted().scientificName);
        assertEquals(0.142, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }


    // Sub-national distribution
    @Test
    public void testLocalitySearch6() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Riccia multifida var. filiformis";
        classification.locationId = new HashSet<>(Arrays.asList("http://vocab.getty.edu/tgn/7001828", "http://vocab.getty.edu/tgn/7000490")); // NSW, Australia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/ausmoss/2888647", result.getMatch().taxonId);
        assertEquals("Riccia multifida var. filiformis", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(), result.getIssues());
    }

    // Sub-national distribution in wrong place
    @Test
    public void testLocalitySearch7() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Riccia multifida var. filiformis";
        classification.locationId = new HashSet<>(Arrays.asList("http://vocab.getty.edu/tgn/7001992", "http://vocab.getty.edu/tgn/7000490")); // Tas, Australia
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("https://id.biodiversity.org.au/node/ausmoss/2888647", result.getMatch().taxonId);
        assertEquals("Riccia multifida var. filiformis", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.REMOVED_LOCATION), result.getIssues());
    }


    // Ensure virus names can be found
    @Test
    public void testVirusSearch1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Arbovirus: Exotic West Nile virus";
        Match<AlaLinnaeanClassification, MatchMeasurement> result = this.searcher.search(classification);
        assertTrue(result.isValid());
        assertEquals("ALA_DR22913_619", result.getMatch().taxonId);
        assertEquals("Arbovirus: Exotic West Nile virus", result.getMatch().scientificName);
        assertEquals(TaxonomicStatus.accepted, result.getMatch().taxonomicStatus);
        assertEquals(1.0, result.getProbability().getPosterior(), 0.001);
        assertEquals(1.0, result.getFidelity().getFidelity(), 0.001);
        assertEquals(Issues.of(AlaLinnaeanFactory.UNPARSABLE_NAME), result.getIssues());
    }

    // Pterostylis longifolia and friends
    public void testProblemX() {
        fail("Fix this");
    }

}
