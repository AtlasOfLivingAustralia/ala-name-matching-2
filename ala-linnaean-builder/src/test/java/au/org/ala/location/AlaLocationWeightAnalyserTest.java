package au.org.ala.location;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.vocab.GeographyType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AlaLocationWeightAnalyserTest {
    private AlaLocationWeightAnalyser analyser;

    @Before
    public void setUp() throws Exception {
        IndexBuilderConfiguration config = new IndexBuilderConfiguration();
        config.setWeightAnalyserClass(AlaLocationWeightAnalyser.class);
        Network network = new Network("test");
        this.analyser = (AlaLocationWeightAnalyser) config.createWeightAnalyser(network);
    }

    private Classifier makeClassifier(String locationId, GeographyType geographyType, Double area) throws StoreException {
        LuceneClassifier classifier = new LuceneClassifier();
        if (locationId != null)
            classifier.add(AlaLocationFactory.locationId, locationId, false, false);
        if (geographyType != null)
            classifier.add(AlaLocationFactory.geographyType, geographyType, false, false);
        if (area != null)
            classifier.add(AlaLocationFactory.area, area, false, false);
        return classifier;
    }

    @Test
    public void testWeight1() throws Exception {
        Classifier classifier = this.makeClassifier("http://vocab.getty.edu/tgn/7029392", GeographyType.continent, 31033131.0);
        assertEquals(11.34, this.analyser.weight(classifier), 0.01);
    }

    @Test
    public void testWeight2() throws Exception {
        Classifier classifier = this.makeClassifier("http://vocab.getty.edu/tgn/7029392", GeographyType.continent, null);
        assertEquals(10.90, this.analyser.weight(classifier), 0.1);
    }

    @Test
    public void testWeight3() throws Exception {
        Classifier classifier = this.makeClassifier("http://vocab.getty.edu/tgn/7024196", GeographyType.stateProvince, null);
        assertEquals(4.91, this.analyser.weight(classifier), 0.01);
    }


    @Test
    public void testWeight4() throws Exception {
        Classifier classifier = this.makeClassifier("http://vocab.getty.edu/tgn/1007365", GeographyType.island, null);
        assertEquals(1.00, this.analyser.weight(classifier), 0.01);
    }

    @Test
    public void testWeight5() throws Exception {
        Classifier classifier = this.makeClassifier("http://vocab.getty.edu/tgn/1007365", GeographyType.municipality, null);
        assertEquals(1.00, this.analyser.weight(classifier), 0.01);
    }

    @Test
    public void testModify1() throws Exception {
        Classifier classifier = this.makeClassifier("http://vocab.getty.edu/tgn/7029392", GeographyType.continent, null);
         assertEquals(1.0, this.analyser.modify(classifier, 1.0), 0.00001);
    }

}