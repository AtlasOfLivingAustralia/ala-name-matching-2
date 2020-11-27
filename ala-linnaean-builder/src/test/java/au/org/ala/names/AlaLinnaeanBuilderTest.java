package au.org.ala.names;

import au.org.ala.bayesian.ClassificationMatcher;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Match;
import au.org.ala.names.builder.IndexBuilder;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.builder.LoadStore;
import au.org.ala.names.builder.Source;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import au.org.ala.util.TestUtils;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.nameparser.api.Rank;
import org.junit.*;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AlaLinnaeanBuilderTest extends TestUtils {
    private static File work;
    private static File output;
    private static IndexBuilder builder;

    private AlaLinnaeanFactory factory;
    private LuceneClassifierSearcher searcher;
    private ClassificationMatcher<AlaLinnaeanClassification, AlaLinnaeanParameters, AlaLinnaeanInferencer, AlaLinnaeanFactory> matcher;

    @BeforeClass
    public static void setUpClass() throws Exception {
        work = makeTmpDir("work");
        output = makeTmpDir("output");
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setBuilderClass(AlaLinnaeanBuilder.class);
        config.setNetwork(AlaLinnaeanBuilder.class.getResource("ala-linnaean.json"));
        config.setWork(work);
        config.setFactoryClass(AlaLinnaeanFactory.class);
        builder = new IndexBuilder(config);
        Source source = Source.create(AlaLinnaeanBuilderTest.class.getResource("/sample-1.zip"), AlaLinnaeanFactory.instance(), AlaLinnaeanFactory.instance().getObservables(), config.getTypes());
        builder.load(source);
        builder.build();
        builder.buildIndex(output);
   }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (builder != null)
            builder.close();
        if (output != null)
            deleteAll(output);
        if (work != null)
            deleteAll(work);
    }

    @Before
    public void setUp() throws Exception {
        this.factory = AlaLinnaeanFactory.instance();
        this.searcher = new LuceneClassifierSearcher(this.output);
        this.matcher = this.factory.createMatcher(this.searcher);
    }

    @After
    public void tearDown() throws Exception {
        if (this.searcher != null)
            this.searcher.close();
    }

    @Test
    public void testLoadBuild1() throws Exception {
        LoadStore store = this.builder.getParameterisedStore();
        Classifier doc = store.get(DwcTerm.Taxon, AlaLinnaeanFactory.taxonId, "urn:lsid:indexfungorum.org:names:90156");
        assertNotNull(doc);
        assertEquals("Fungi", doc.get(AlaLinnaeanFactory.scientificName));
        doc = store.get(DwcTerm.Taxon, AlaLinnaeanFactory.taxonId, "https://id.biodiversity.org.au/node/apni/2904909");
        assertEquals("Canarium acutifolium var. acutifolium", doc.get(AlaLinnaeanFactory.scientificName));
        assertEquals("CANARIM ACITIFALIM VAR. ACITIFALIM", doc.get(AlaLinnaeanFactory.soundexScientificName));
        assertEquals("Canarium", doc.get(AlaLinnaeanFactory.genus));
        assertEquals("https://id.biodiversity.org.au/node/apni/2918714", doc.get(AlaLinnaeanFactory.genusId));
        AlaLinnaeanParameters params = new AlaLinnaeanParameters();
        doc.loadParameters(params);
        AlaLinnaeanInferencer inference = new AlaLinnaeanInferencer();
        AlaLinnaeanInferencer.Evidence evidence = new AlaLinnaeanInferencer.Evidence();
        evidence.e$scientificName = true;
        Inference prob = inference.probability(evidence, params);
        assertEquals(1.0, prob.getPosterior(), 0.00001);
        evidence.e$genus = false;
        prob = inference.probability(evidence, params);
        assertEquals(1.0, prob.getPosterior(), 0.00001);
        evidence.e$soundexGenus = true;
        prob = inference.probability(evidence, params);
        assertEquals(1.0, prob.getPosterior(), 0.00001);
        evidence.e$soundexGenus = false;
        prob = inference.probability(evidence, params);
        assertEquals(1.0, prob.getPosterior(), 0.00001);
        evidence.e$scientificName = false;
        prob = inference.probability(evidence, params);
        assertEquals(0.0, prob.getPosterior(), 0.00001);
        evidence.e$scientificName = null;
        evidence.e$genus = true;
        evidence.e$soundexGenus = true;
        prob = inference.probability(evidence, params);
        assertEquals(0.00409, prob.getEvidence(), 0.00001);
        assertEquals(0.33425, prob.getPosterior(), 0.00001);
    }


    @Test
    public void testSearch1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Canarium acutifolium var. acutifolium";
        List<LuceneClassifier> classifiers = this.searcher.search(classification);
        assertNotNull(classifiers);
        assertEquals(20, classifiers.size());
        assertEquals("https://id.biodiversity.org.au/node/apni/2904909", classifiers.get(0).get(AlaLinnaeanFactory.taxonId));
    }

    @Test
    public void testMatch1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Canarium acutifolium var. acutifolium";
        Match<AlaLinnaeanClassification> match = matcher.findMatch(classification);
        assertNotNull(match);
        assertEquals("https://id.biodiversity.org.au/node/apni/2904909", match.getMatch().taxonId);
        assertEquals(0.001366, match.getProbability().getEvidence(), 0.00001);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testMatch2() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.genus = "Canarium";
        classification.specificEpithet = "acutifolium";
        Match<AlaLinnaeanClassification> match = matcher.findMatch(classification);
        assertNotNull(match);
        assertEquals("https://id.biodiversity.org.au/node/apni/2901022", match.getMatch().taxonId);
        assertEquals(0.0027322, match.getProbability().getEvidence(), 0.00001);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testMatch3() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.genus = "Canarium";
        classification.specificEpithet = "acutifolium";
        classification.taxonRank = Rank.SPECIES;
        Match<AlaLinnaeanClassification> match = matcher.findMatch(classification);
        assertNotNull(match);
        assertEquals("https://id.biodiversity.org.au/node/apni/2901022", match.getMatch().taxonId);
        assertEquals(0.002732, match.getProbability().getEvidence(), 0.00001);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }


    @Test
    public void testMatch4() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Plantae";
        Match<AlaLinnaeanClassification> match = matcher.findMatch(classification);
        assertNotNull(match);
        assertEquals("https://id.biodiversity.org.au/taxon/apni/51337710", match.getMatch().taxonId);
        assertEquals(0.0082, match.getProbability().getEvidence(), 0.00001);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }


    @Test
    public void testMatch5() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Canarium acutifolium";
        Match<AlaLinnaeanClassification> match = matcher.findMatch(classification);
        assertNotNull(match);
        assertEquals("https://id.biodiversity.org.au/node/apni/2901022", match.getMatch().taxonId);
        assertEquals(0.002732, match.getProbability().getEvidence(), 0.00001);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

}
