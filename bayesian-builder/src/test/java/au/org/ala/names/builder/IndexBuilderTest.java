package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Observable;
import au.org.ala.names.generated.SimpleLinnaeanBuilder;
import au.org.ala.names.generated.SimpleLinnaeanFactory;
import au.org.ala.names.generated.SimpleLinnaeanParameters_FT;
import au.org.ala.names.generated.SimpleLinnaeanParameters_TT;
import au.org.ala.names.lucene.LuceneLoadStore;
import org.gbif.dwc.terms.DwcTerm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IndexBuilderTest {
    private IndexBuilderConfiguration config;
    private IndexBuilder builder;

    @Before
    public void setUp() throws Exception {
        this.config = new IndexBuilderConfiguration();
        this.config.setWork(new File("target/test-work"));
        this.config.setBuilderClass(SimpleLinnaeanBuilder.class);
        this.config.setLoadStoreClass(LuceneLoadStore.class);
        this.config.setNetwork(this.getClass().getResource("../lucene/simple-network.json"));
        this.config.setFactoryClass(SimpleLinnaeanFactory.class);
        this.builder = new IndexBuilder(this.config);
    }

    @After
    public void tearDown() throws Exception {
        if (this.builder != null)
            this.builder.close();
    }

    @Test
    public void testLoad1() throws Exception {
        Observable taxonID = this.builder.network.getObservable(DwcTerm.taxonID);
        Observable taxonomicStatus = this.builder.network.getObservable(DwcTerm.taxonomicStatus);
        URL surl = this.getClass().getResource("source-1.csv");
        CSVSource source = new CSVSource(surl, this.builder.getFactory(), this.builder.getNetwork().getObservables());
        this.builder.load(source);
        Classifier classifier = this.builder.loadStore.get(DwcTerm.Taxon, taxonID, "S-1");
        assertEquals("accepted", classifier.get(taxonomicStatus));
    }

    @Test
    public void testExpandTree1() throws Exception {
        Observable taxonID = this.builder.network.getObservable(DwcTerm.taxonID);
        Observable family = this.builder.network.getObservable(DwcTerm.family);
        Observable kingdom = this.builder.network.getObservable(DwcTerm.kingdom);
        URL surl = this.getClass().getResource("source-1.csv");
        CSVSource source = new CSVSource(surl, this.builder.getFactory(), this.builder.getNetwork().getObservables());
        this.builder.load(source);
        this.builder.expandTree();
        Classifier classifier = this.builder.expandedStore.get(DwcTerm.Taxon, taxonID, "S-1");
        assertNotNull(classifier);
        assertEquals("ARTEMIIDAE", classifier.get(family));
        assertEquals("Animalia", classifier.get(kingdom));
        int[] indexes = classifier.getIndex();
        assertEquals(2, indexes.length);
    }

    @Test
    public void testExpandSynonyms1() throws Exception {
        Observable taxonID = this.builder.network.getObservable(DwcTerm.taxonID);
        Observable family = this.builder.network.getObservable(DwcTerm.family);
        Observable kingdom = this.builder.network.getObservable(DwcTerm.kingdom);
        URL surl = this.getClass().getResource("source-1.csv");
        CSVSource source = new CSVSource(surl, this.builder.getFactory(), this.builder.getNetwork().getObservables());
        this.builder.load(source);
        this.builder.expandTree();
        this.builder.expandSynonyms();
        Classifier classifier = this.builder.expandedStore.get(DwcTerm.Taxon, taxonID, "S-S-1");
        assertNotNull(classifier);
        assertEquals("ARTEMIIDAE", classifier.get(family));
        assertEquals("Animalia", classifier.get(kingdom));
    }

    @Test
    public void testBuildParameters1() throws Exception {
        Observable taxonID = this.builder.network.getObservable(DwcTerm.taxonID);
        URL surl = this.getClass().getResource("source-1.csv");
        CSVSource source = new CSVSource(surl, this.builder.getFactory(), this.builder.getNetwork().getObservables());
        this.builder.load(source);
        this.builder.expandTree();
        this.builder.expandSynonyms();
        this.builder.buildParameters();
        Classifier classifier = this.builder.parameterisedStore.get(DwcTerm.Taxon, taxonID, "S-S-1");
        assertEquals("FT", classifier.getSignature());
        SimpleLinnaeanParameters_FT parameters = new SimpleLinnaeanParameters_FT();
        classifier.loadParameters(parameters);
        assertEquals(0.0909, parameters.prior_taxonId_t, 0.0001);
        assertEquals(0.0, parameters.inf_kingdom_f$t_t, 0.0001);
        assertEquals(0.6667, parameters.inf_phylum_t$f_f, 0.0001);
        assertEquals(0.0, parameters.inf_phylum_t$t_f, 0.0001);
    }


    @Test
    public void testBuildParameters2() throws Exception {
        Observable taxonID = this.builder.network.getObservable(DwcTerm.taxonID);
        URL surl = this.getClass().getResource("source-1.csv");
        CSVSource source = new CSVSource(surl, this.builder.getFactory(), this.builder.getNetwork().getObservables());
        this.builder.load(source);
        this.builder.expandTree();
        this.builder.expandSynonyms();
        this.builder.buildParameters();
        Classifier classifier = this.builder.parameterisedStore.get(DwcTerm.Taxon, taxonID, "S-1");
        assertEquals("FT", classifier.getSignature());
        SimpleLinnaeanParameters_FT parameters = new SimpleLinnaeanParameters_FT();
        classifier.loadParameters(parameters);
        assertEquals(0.0909, parameters.prior_taxonId_t, 0.0001);
        assertEquals(0.0, parameters.inf_kingdom_f$t_t, 0.0001);
        assertEquals(0.0, parameters.inf_genus_t$t_ff, 0.0001);
        assertEquals(0.0, parameters.inf_genus_t$f_ft, 0.0001);
    }

}
