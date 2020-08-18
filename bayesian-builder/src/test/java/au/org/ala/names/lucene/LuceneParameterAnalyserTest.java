package au.org.ala.names.lucene;

import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.names.builder.Annotator;
import au.org.ala.names.builder.IndexBuilder;
import au.org.ala.names.builder.TestAnnotator;
import au.org.ala.vocab.BayesianTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LuceneParameterAnalyserTest {
    private static final double DEFAULT_WIEGHT = 1.0;
    private Network network;
    private Annotator annotator;
    private LuceneUtils lucene;
    private Observable weight;

    @Before
    public void setUp() throws Exception {
        BayesianTerm.values(); // Ensure loaded
        this.network = Network.read(this.getClass().getResource("simple-network.json"));
        this.weight = this.network.findObservable(BayesianTerm.weight, true).get();
        this.annotator = new TestAnnotator();
        this.lucene = new LuceneUtils(LuceneParameterAnalyser.class, "parameter-analyser-1.csv", this.network.getObservables());
    }

    @After
    public void cleanUp() throws Exception {
        if (this.lucene != null) {
            this.lucene.close();
            this.lucene = null;
        }
    }

    private Observation makeFact(Term field, String... values) {
        return new Observation(true, new Observable(field), values);
    }

    private Observation makeNotFact(Term field, String... values) {
        return new Observation(false, new Observable(field), values);
    }

    @Test
    public void testTotalWeight1() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        assertEquals(250.0, analyser.getTotalWeight(), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputePrior1() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        assertEquals(0.28, analyser.computePrior(this.makeFact(DwcTerm.genus, "Osphranter")), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputePrior2() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        assertEquals(0.48, analyser.computePrior(this.makeFact(DwcTerm.genus, "Acacia")), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputePrior3() throws Exception {
         LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);

        assertEquals(LuceneParameterAnalyser.MAXIMUM_PROBABILITY, analyser.computePrior(this.makeFact(DwcTerm.genus, "Montitega", "Acacia", "Osphranter", "Agathis")), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputePrior4() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);

        assertEquals(LuceneParameterAnalyser.MINIMUM_PROBABILITY, analyser.computePrior(this.makeFact(DwcTerm.genus, "Invalid")), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputeConditional1() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.genus, "Acacia");
        Observation input1 = this.makeFact(DwcTerm.specificEpithet, "abbreviata");
        assertEquals(LuceneParameterAnalyser.MAXIMUM_PROBABILITY, analyser.computeConditional(node, input1), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputeConditional2() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.genus, "Acacia");
        Observation input1 = this.makeNotFact(DwcTerm.specificEpithet, "abbreviata");
        assertEquals(0.43478260869565216, analyser.computeConditional(node, input1), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputeConditional3() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.genus, "Acacia");
        Observation input1 = this.makeFact(DwcTerm.specificEpithet, "dealbata");
        assertEquals(0.7692307692307693, analyser.computeConditional(node, input1), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputeConditional4() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.genus, "Acacia");
        Observation input1 = this.makeFact(DwcTerm.specificEpithet, "dealbata");
        Observation input2 = this.makeFact(DwcTerm.scientificName, "Acacia dealbata");
        assertEquals(LuceneParameterAnalyser.MAXIMUM_PROBABILITY, analyser.computeConditional(node, input1, input2), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputeConditional5() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.genus, "Acacia");
        Observation input1 = this.makeFact(DwcTerm.specificEpithet, "dealbata");
        Observation input2 = this.makeNotFact(DwcTerm.scientificName, "Acacia dealbata");
        assertEquals(LuceneParameterAnalyser.MINIMUM_PROBABILITY, analyser.computeConditional(node, input1, input2), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }


    @Test
    public void testComputeConditional6() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.family, "Fabaceae");
        Observation input1 = this.makeFact(DwcTerm.genus, "Acacia");
        assertEquals(LuceneParameterAnalyser.MAXIMUM_PROBABILITY, analyser.computeConditional(node, input1), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    @Test
    public void testComputeConditional7() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.order, "Fabales");
        Observation input1 = this.makeFact(DwcTerm.family, "Fabaceae");
        assertEquals(LuceneParameterAnalyser.MAXIMUM_PROBABILITY, analyser.computeConditional(node, input1), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }

    // Homonym
    @Test
    public void testComputeConditional8() throws Exception {
        LuceneParameterAnalyser analyser = new LuceneParameterAnalyser(this.network, this.annotator, this.lucene.getSearcher(), this.weight, DEFAULT_WIEGHT);
        Observation node = this.makeFact(DwcTerm.family, "Braconidae");
        Observation input1 = this.makeFact(DwcTerm.genus, "Agathis");
        assertEquals(0.6666666666666666, analyser.computeConditional(node, input1), LuceneParameterAnalyser.MINIMUM_PROBABILITY);
    }
}
