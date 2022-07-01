package au.org.ala.names.lucene;

import au.org.ala.bayesian.TestClassification;
import au.org.ala.bayesian.TestFactory;
import org.junit.After;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LuceneClassifierSearcherTest {
    private LuceneUtils lucene;
    private LuceneClassifierSearcher searcher;

    @After
    public void cleanUp() throws Exception {
        if (this.searcher != null) {
            this.searcher.close();
            this.searcher = null;
        }
        if (this.lucene != null) {
            this.lucene.close();
            this.lucene = null;
        }
    }

    @Test
    public void testSearch1() throws Exception {
        this.lucene = new LuceneUtils(LuceneClassifierSearcherTest.class, "lucene-searcher-1.csv", TestFactory.OBSERVABLES, TestFactory.SCIENTIFIC_NAME);
        LuceneClassifierSearcherConfiguration config = LuceneClassifierSearcherConfiguration.builder().scoreCutoff(0.1f).build();
        this.searcher = new LuceneClassifierSearcher(this.lucene.getIndexDir(), config, TestFactory.TAXON_ID);
        TestClassification classification = new TestClassification();
        classification.scientificName = "Lates calcarifer";
        List<LuceneClassifier> classifiers = this.searcher.search(classification);
        assertNotNull(classifiers);
        assertEquals(1, classifiers.size());
        LuceneClassifier c1 = classifiers.get(0);
        assertEquals("Lates calcarifer", c1.get(TestFactory.SCIENTIFIC_NAME));
        assertEquals("ACTINOPTERYGII", c1.get(TestFactory.CLASS_));
        assertEquals("Barramundi", c1.get(TestFactory.VERNACULAR_NAME));
        assertEquals(Integer.valueOf(7000), c1.get(TestFactory.RANK_ID));
    }

    @Test
    public void testSearch2() throws Exception {
        this.lucene = new LuceneUtils(LuceneClassifierSearcherTest.class, "lucene-searcher-1.csv", TestFactory.OBSERVABLES, TestFactory.SCIENTIFIC_NAME);
        LuceneClassifierSearcherConfiguration config = LuceneClassifierSearcherConfiguration.builder().scoreCutoff(0.1f).build();
        this.searcher = new LuceneClassifierSearcher(this.lucene.getIndexDir(), config, TestFactory.TAXON_ID);
        TestClassification classification = new TestClassification();
        classification.class_ = "Reptilia";
        List<LuceneClassifier> classifiers = this.searcher.search(classification);
        assertNotNull(classifiers);
        assertEquals(2, classifiers.size());
        LuceneClassifier c1 = classifiers.get(0);
        assertEquals("reptilia", ((String) c1.get(TestFactory.CLASS_)).toLowerCase());
        LuceneClassifier c2 = classifiers.get(1);
        assertEquals("reptilia", ((String) c2.get(TestFactory.CLASS_)).toLowerCase());
    }


    @Test
    public void testSearch3() throws Exception {
        this.lucene = new LuceneUtils(LuceneClassifierSearcherTest.class, "lucene-searcher-1.csv", TestFactory.OBSERVABLES, TestFactory.SCIENTIFIC_NAME);
        LuceneClassifierSearcherConfiguration config = LuceneClassifierSearcherConfiguration.builder().scoreCutoff(0.1f).build();
        this.searcher = new LuceneClassifierSearcher(this.lucene.getIndexDir(), config, TestFactory.TAXON_ID);
        TestClassification classification = new TestClassification();
        classification.class_ = "Reptilia";
        classification.rankID = 7000;
        List<LuceneClassifier> classifiers = this.searcher.search(classification);
        assertNotNull(classifiers);
        assertEquals(3, classifiers.size());
        LuceneClassifier c1 = classifiers.get(0);
        assertEquals(true, c1.match("Reptilia", TestFactory.CLASS_));
        assertEquals(true, c1.match(7000, TestFactory.RANK_ID));
        LuceneClassifier c2 = classifiers.get(1);
        assertEquals(true, c2.match("reptilia", TestFactory.CLASS_));
        assertEquals(true, c2.match(7000, TestFactory.RANK_ID));
    }
}
