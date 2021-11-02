package au.org.ala.names;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.vocab.TaxonomicStatus;
import org.gbif.nameparser.api.Rank;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class AlaWeightAnalyserTest {
    private AlaWeightAnalyser analyser;

    @Before
    public void setUp() throws Exception {
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setWeightAnalyserClass(AlaWeightAnalyser.class);
        URL wf = this.getClass().getResource(AlaWeightAnalyser.TAXON_WEIGHTS_FILE);
        if (wf.getProtocol().equals("file")) {
            File dataDir = new File(wf.getPath());
            dataDir = dataDir.getParentFile();
            config.setData(dataDir);
        }
        Network network = new Network("test");
        this.analyser = (AlaWeightAnalyser) config.createWeightAnalyser(network);
    }

    private Classifier makeClassifier(String taxonId, String scientificName, String scientificNameAuthorship, Rank rank, TaxonomicStatus status, Integer priority) throws StoreException {
        LuceneClassifier classifier = new LuceneClassifier();
        if (taxonId != null)
            classifier.add(AlaLinnaeanFactory.taxonId, taxonId);
        if (scientificName != null)
            classifier.add(AlaLinnaeanFactory.scientificName, scientificName);
        if (scientificNameAuthorship != null)
            classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, scientificNameAuthorship);
        if (rank != null)
            classifier.add(AlaLinnaeanFactory.taxonRank, rank);
        if (status != null)
            classifier.add(AlaLinnaeanFactory.taxonomicStatus, status);
        if (priority != null)
            classifier.add(AlaLinnaeanFactory.priority, priority);
        return classifier;
    }

    @Test
    public void testWeight1() throws Exception {
        Classifier classifier = this.makeClassifier("23205008", "Neilo delli", "Marshall, 1978", Rank.SPECIES, TaxonomicStatus.accepted, null);
        assertEquals(1.0, this.analyser.weight(classifier), 0.00001);
    }

    @Test
    public void testWeight2() throws Exception {
        Classifier classifier = this.makeClassifier("23205008", "Neilo delli", "Marshall, 1978", Rank.SPECIES, TaxonomicStatus.accepted, 5000);
        assertEquals(9.51719, this.analyser.weight(classifier), 0.00001);
    }

    @Test
    public void testWeight3() throws Exception {
        Classifier classifier = this.makeClassifier("https://id.biodiversity.org.au/node/apni/2901022", "Canarium acutifolium", "(DC.) Merr.", Rank.SPECIES, TaxonomicStatus.accepted, 6000);
        assertEquals(100.0, this.analyser.weight(classifier), 0.00001);
    }

    @Test
    public void testModify1() throws Exception {
        Classifier classifier = this.makeClassifier("23205008", "Neilo delli", "Marshall, 1978", Rank.SPECIES, TaxonomicStatus.accepted, null);
        assertEquals(20.0, this.analyser.modify(classifier, 1.0), 0.00001);
    }

    @Test
    public void testModify2() throws Exception {
        Classifier classifier = this.makeClassifier("urn:lsid:indexfungorum.org:names:90156", "Fungi", null, Rank.KINGDOM, TaxonomicStatus.accepted, null);
        assertEquals(10.0, this.analyser.modify(classifier, 1.0), 0.00001);
    }
}