package au.org.ala.names;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Observable;
import au.org.ala.names.builder.IndexBuilder;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.builder.LoadStore;
import au.org.ala.names.builder.Source;
import au.org.ala.util.TestUtils;
import au.org.ala.vocab.ALATerm;
import org.gbif.dwc.terms.DwcTerm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AlaLinnaeanBuilderTest extends TestUtils {
    private File work;
    private File output;
    private IndexBuilder builder;

    @Before
    public void setUp() throws Exception {
        this.work = this.makeTmpDir("work");
        this.output = this.makeTmpDir("output");
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setBuilderClass(AlaLinnaeanBuilder.class);
        config.setNetwork(AlaLinnaeanBuilder.class.getResource("AlaLinnaean.json"));
        config.setWork(this.work);
        this.builder = new IndexBuilder(config);
    }

    @After
    public void tearDown() throws Exception {
        if (this.builder != null)
            this.builder.close();
        if (this.output != null)
            this.deleteAll(this.output);
        if (this.work != null)
            this.deleteAll(this.work);
    }

    @Test
    public void testLoadBuild1() throws Exception {
        Observable taxonID = this.builder.getNetwork().getObservable(DwcTerm.taxonID);
        Observable scientificName = this.builder.getNetwork().getObservable(DwcTerm.scientificName);
        Observable genus = this.builder.getNetwork().getObservable(DwcTerm.genus);
        Observable genusID = this.builder.getNetwork().getObservable(ALATerm.genusID);
        Observable soundex = this.builder.getNetwork().getObservable(ALATerm.soundexScientificName);
        Source source = Source.create(this.getClass().getResource("/sample-1.zip"));
        this.builder.load(source);
        LoadStore store = this.builder.getLoadStore();
        Classifier doc = store.get(DwcTerm.Taxon, taxonID, "urn:lsid:indexfungorum.org:names:90156");
        assertNotNull(doc);
        assertEquals("Fungi", doc.get(scientificName));
        this.builder.build();
        doc = store.get(DwcTerm.Taxon, taxonID, "https://id.biodiversity.org.au/node/apni/2904909");
        assertEquals("Canarium acutifolium var. acutifolium", doc.get(scientificName));
        assertEquals("CANARIM ACITIFALIM VAR. ACITIFALIM", doc.get(soundex));
        assertEquals("Canarium", doc.get(genus));
        assertEquals("https://id.biodiversity.org.au/node/apni/2918714", doc.get(genusID));
        AlaLinnaeanParameters params = new AlaLinnaeanParameters();
        doc.loadParameters(params);
        AlaLinnaeanInference inference = new AlaLinnaeanInference();
        inference.parameters = params;
        AlaLinnaeanInference.Evidence evidence = new AlaLinnaeanInference.Evidence();
        evidence.e$scientificName = true;
        double prob = inference.probability(evidence);
        assertEquals(1.0, prob, 0.00001);
        evidence.e$genus = false;
        prob = inference.probability(evidence);
        assertEquals(1.0, prob, 0.00001);
        evidence.e$soundexGenus = true;
        prob = inference.probability(evidence);
        assertEquals(1.0, prob, 0.00001);
        evidence.e$soundexGenus = false;
        prob = inference.probability(evidence);
        assertEquals(1.0, prob, 0.00001);
        evidence.e$scientificName = false;
        prob = inference.probability(evidence);
        assertEquals(0.0, prob, 0.00001);
        evidence.e$scientificName = null;
        evidence.e$genus = true;
        evidence.e$soundexGenus = true;
        prob = inference.probability(evidence);
        assertEquals(0.33333, prob, 0.00001);
    }
}
