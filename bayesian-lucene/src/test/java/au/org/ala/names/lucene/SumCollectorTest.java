package au.org.ala.names.lucene;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class SumCollectorTest {
    private LuceneUtils lucene;

    @After
    public void cleanUp() throws Exception {
        if (this.lucene != null) {
            this.lucene.close();
            this.lucene = null;
        }
    }

    @Test
    public void testSumCollector1() throws Exception {
        this.lucene = new LuceneUtils(SumCollector.class, "sum-collector-1.csv", Collections.EMPTY_LIST);
        SumCollector collector = new SumCollector(this.lucene.getSearcher(), "weight", 1.0);
        Query query = new TermQuery(new Term("type", "insect"));
        this.lucene.getSearcher().search(query, collector);
        Assert.assertEquals(110.0, collector.getSum(), 0.0001);
    }

    @Test
    public void testSumCollector2() throws Exception {
        this.lucene = new LuceneUtils(SumCollector.class, "sum-collector-1.csv", Collections.EMPTY_LIST);
        SumCollector collector = new SumCollector(this.lucene.getSearcher(), "weight", 1.0);
        Query query = new TermQuery(new Term("type", "invalid"));
        this.lucene.getSearcher().search(query, collector);
        Assert.assertEquals(0.0, collector.getSum(), 0.0001);
    }

    @Test
    public void testSumCollector3() throws Exception {
        this.lucene = new LuceneUtils(SumCollector.class, "sum-collector-1.csv", Collections.EMPTY_LIST);
        SumCollector collector = new SumCollector(this.lucene.getSearcher(), "weight", 1.0);
        Query query = new TermQuery(new Term("name", "Ant"));
        this.lucene.getSearcher().search(query, collector);
        Assert.assertEquals(100.0, collector.getSum(), 0.0001);
    }

}
