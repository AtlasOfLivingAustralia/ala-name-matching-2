package au.org.ala.names.lucene;

import au.org.ala.bayesian.TestClassification;
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
        this.lucene = new LuceneUtils(LuceneClassifierSearcherTest.class, "lucene-searcher-1.csv", TestClassification.OBSERVABLES);
        this.searcher = new LuceneClassifierSearcher(this.lucene.getIndexDir(), null);
        TestClassification classification = new TestClassification();
        classification.scientificName = "Lates calcarifer";
        List<LuceneClassifier> classifiers = this.searcher.search(classification);
        assertNotNull(classifiers);
        assertEquals(4, classifiers.size());
        LuceneClassifier c1 = classifiers.get(0);
        assertEquals("Lates calcarifer", c1.get(TestClassification.SCIENTIFIC_NAME));
        assertEquals("ACTINOPTERYGII", c1.get(TestClassification.CLASS_));
        assertEquals("Barramundi", c1.get(TestClassification.VERNACULAR_NAME));
        assertEquals(Integer.valueOf(7000), c1.get(TestClassification.RANK_ID));
    }

    @Test
    public void testSearch2() throws Exception {
        this.lucene = new LuceneUtils(LuceneClassifierSearcherTest.class, "lucene-searcher-1.csv", TestClassification.OBSERVABLES);
        this.searcher = new LuceneClassifierSearcher(this.lucene.getIndexDir(), null);
        TestClassification classification = new TestClassification();
        classification.class_ = "Reptilia";
        List<LuceneClassifier> classifiers = this.searcher.search(classification);
        assertNotNull(classifiers);
        assertEquals(4, classifiers.size());
        LuceneClassifier c1 = classifiers.get(0);
        assertEquals("reptilia", ((String) c1.get(TestClassification.CLASS_)).toLowerCase());
        LuceneClassifier c2 = classifiers.get(1);
        assertEquals("reptilia", ((String) c2.get(TestClassification.CLASS_)).toLowerCase());
    }


    @Test
    public void testSearch3() throws Exception {
        this.lucene = new LuceneUtils(LuceneClassifierSearcherTest.class, "lucene-searcher-1.csv", TestClassification.OBSERVABLES);
        this.searcher = new LuceneClassifierSearcher(this.lucene.getIndexDir(), null);
        TestClassification classification = new TestClassification();
        classification.class_ = "Reptilia";
        classification.rankID = 7000;
        List<LuceneClassifier> classifiers = this.searcher.search(classification);
        assertNotNull(classifiers);
        assertEquals(4, classifiers.size());
        LuceneClassifier c1 = classifiers.get(0);
        assertEquals(true, c1.match("Reptilia", TestClassification.CLASS_));
        assertEquals(true, c1.match(7000, TestClassification.RANK_ID));
        LuceneClassifier c2 = classifiers.get(1);
        assertEquals(true, c2.match("reptilia", TestClassification.CLASS_));
        assertEquals(true, c2.match(7000, TestClassification.RANK_ID));
    }
}
