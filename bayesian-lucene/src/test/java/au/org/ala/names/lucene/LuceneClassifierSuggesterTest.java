package au.org.ala.names.lucene;

import au.org.ala.bayesian.ClassifierSuggester;
import au.org.ala.bayesian.TestClassification;
import au.org.ala.bayesian.TestFactory;
import au.org.ala.util.FileUtils;
import org.apache.lucene.store.FSDirectory;
import org.gbif.dwc.terms.DwcTerm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class LuceneClassifierSuggesterTest {
    private File dir;
    private LuceneUtils lucene;
    private LuceneClassifierSearcher searcher;
    private LuceneClassifierSuggester suggester;

    @Before
    public void setUp() throws Exception {
        this.dir = FileUtils.makeTmpDir("test");
        this.lucene = new LuceneUtils(LuceneClassifierSuggesterTest.class, "/sample-1/taxon.txt", TestFactory.OBSERVABLES, TestFactory.SCIENTIFIC_NAME);
        LuceneClassifierSearcherConfiguration config = LuceneClassifierSearcherConfiguration.builder().scoreCutoff(0.1f).build();
        this.searcher = new LuceneClassifierSearcher(this.lucene.getIndexDir(), config, TestFactory.TAXON_ID);
    }

    @After
    public void cleanUp() throws Exception {
        if (this.suggester != null) {
            this.suggester.close();
            this.suggester = null;
        }
        if (this.searcher != null) {
            this.searcher.close();
            this.searcher = null;
        }
        if (this.lucene != null) {
            this.lucene.close();
            this.lucene = null;
        }
        if (this.dir != null) {
            FileUtils.deleteAll(this.dir);
        }
    }

    @Test
    public void testSuggest1() throws Exception {
        this.suggester = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory());
        this.suggester.load();
        List<ClassifierSuggester.Suggestion<LuceneClassifier>> suggestions = this.suggester.suggest("fungi", 10, true);
        assertNotNull(suggestions);
        assertEquals(1, suggestions.size());
        ClassifierSuggester.Suggestion<LuceneClassifier> suggestion = suggestions.get(0);
        assertEquals("Fungi", suggestion.getName());
        assertEquals("urn:lsid:indexfungorum.org:names:90156", suggestion.getMatch().get(TestFactory.TAXON_ID));
        assertEquals(1.0, suggestion.getScore(), 0.00001);
    }

    @Test
    public void testSuggest2() throws Exception {
        this.suggester = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory());
        this.suggester.load();
        List<ClassifierSuggester.Suggestion<LuceneClassifier>> suggestions = this.suggester.suggest("preiss", 10, true);
        assertNotNull(suggestions);
        assertEquals(1, suggestions.size());
        ClassifierSuggester.Suggestion<LuceneClassifier> suggestion = suggestions.get(0);
        assertEquals("Malva preissiana", suggestion.getName());
        assertEquals("https://id.biodiversity.org.au/node/apni/2888637", suggestion.getMatch().get(TestFactory.TAXON_ID));
        assertEquals(0.375, suggestion.getScore(), 0.00001);
    }

    @Test
    public void testSuggest3() throws Exception {
        this.suggester = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory());
        this.suggester.load();
        List<ClassifierSuggester.Suggestion<LuceneClassifier>> suggestions = this.suggester.suggest("Sphe", 10, true);
        assertNotNull(suggestions);
        assertEquals(4, suggestions.size());
        assertEquals("Sphenoderia", suggestions.get(0).getName());
        assertEquals("Sphenoderia", suggestions.get(0).getMatch().get(TestFactory.SCIENTIFIC_NAME));
        assertEquals("Sphenoderia fissirostris", suggestions.get(1).getName());
        assertEquals("Sphenoderia fissirostris", suggestions.get(2).getName()); // Synonym
        assertEquals("Sphenoderia fissirostris fissirostris", suggestions.get(3).getName());
    }

    @Test
    public void testSuggest4() throws Exception {
        this.suggester = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory());
        this.suggester.load();
        List<ClassifierSuggester.Suggestion<LuceneClassifier>> suggestions = this.suggester.suggest("xxx", 4, true);
        assertNotNull(suggestions);
        assertEquals(0, suggestions.size());
    }

    @Test
    public void testMetadata1() throws Exception {
        this.suggester = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory());
        this.suggester.load();
        File store = new File(this.dir, LuceneClassifierSuggester.SUGGESTER_METADATA);
        assertTrue(store.exists());
    }

    @Test
    public void testLoad1() throws Exception {
        this.suggester = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory());
        assertFalse(this.suggester.load());
        LuceneClassifierSuggester loaded = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory());
        assertTrue(loaded.load());
        List<ClassifierSuggester.Suggestion<LuceneClassifier>> suggestions = loaded.suggest("sphe", 10, true);
        assertNotNull(suggestions);
        assertEquals(4, suggestions.size());
    }


    @Test
    public void testLoad2() throws Exception {
        LuceneUtils vernacular = new LuceneUtils(LuceneClassifierSuggesterTest.class, "/sample-1/vernacularname.txt", TestFactory.OBSERVABLES, TestFactory.VERNACULAR_NAME, true);
        LuceneClassifierSearcherConfiguration config = LuceneClassifierSearcherConfiguration.builder().scoreCutoff(0.1f).build();
        LuceneClassifierSearcher vernacularSearcher = new LuceneClassifierSearcher(vernacular.getIndexDir(), config, TestFactory.TAXON_ID);
        this.suggester = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory(), vernacularSearcher);
        assertFalse(this.suggester.load());
        LuceneClassifierSuggester loaded = new LuceneClassifierSuggester(FSDirectory.open(this.dir.toPath()), null, this.searcher, new TestFactory(), vernacularSearcher);
        assertTrue(loaded.load());
        List<ClassifierSuggester.Suggestion<LuceneClassifier>> suggestions = loaded.suggest("Mallow", 10, true);
        assertNotNull(suggestions);
        assertEquals(5, suggestions.size());
        suggestions = loaded.suggest("Oxym", 10, true);
        assertNotNull(suggestions);
        assertEquals(2, suggestions.size());
    }

}
