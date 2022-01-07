package au.org.ala.names;

import au.org.ala.bayesian.Hints;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import org.gbif.nameparser.api.Rank;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import static org.junit.Assert.*;

public class ALAClassificationMatcherTest {
    public static final String INDEX = "/data/lucene/index-20210811-2";

    private LuceneClassifierSearcher searcher;
    private ALAClassificationMatcher matcher;

    @Before
    public void setUp() throws Exception {
        File index = new File(INDEX);
        if (!index.exists())
            throw new IllegalStateException("Index " + index + " not present");
        this.searcher = new LuceneClassifierSearcher(index);
        this.matcher = new ALAClassificationMatcher(AlaLinnaeanFactory.instance(), this.searcher);
    }

    @After
    public void tearDown() throws Exception {
        if (this.searcher != null)
            this.searcher.close();
    }


    @Test
    public void testFindKingdom1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        boolean match = this.matcher.findKingdom("Acanthamoebidae", Rank.FAMILY, classification);
        assertTrue(match);
        Hints<AlaLinnaeanClassification> hints = classification.getHints();
        assertNotNull(hints);
        Set<String> values = hints.getHints(AlaLinnaeanFactory.kingdom);
        assertEquals(Collections.singleton("PROTISTA"), values);
    }

    @Test
    public void testFindKingdom2() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        boolean match = this.matcher.findKingdom("Flammulina", Rank.GENUS, classification);
        assertFalse(match); // Bare homonym
    }


    @Test
    public void testFindKingdom3() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        boolean match = this.matcher.findKingdom("Harpadontidae", Rank.FAMILY, classification);
        assertTrue(match); // Synonym
        Hints<AlaLinnaeanClassification> hints = classification.getHints();
        assertNotNull(hints);
        Set<String> values = hints.getHints(AlaLinnaeanFactory.kingdom);
        assertEquals(Collections.singleton("ANIMALIA"), values);
    }

}