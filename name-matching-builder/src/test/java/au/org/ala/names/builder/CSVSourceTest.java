package au.org.ala.names.builder;

import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.Observable;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.util.TestUtils;
import org.gbif.dwc.terms.DwcTerm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test cases for {@link CSVSource}
 */
public class CSVSourceTest {
    private static final Observable TAXON_ID_OBS = new Observable(DwcTerm.taxonID);
    private static final Observable TAXON_RANK_OBS = new Observable(DwcTerm.taxonRank);
    private static final Observable SCIENTIFIC_NAME_OBS = new Observable(DwcTerm.scientificName);

    private Network network;
    private Annotator annotator;
    private TestLoadStore store;

    @Before
    public void setUp() throws Exception {
        this.network = new Network();
        this.annotator = new TestAnnotator();
        this.store = new TestLoadStore(this.annotator);
    }

    @After
    public void tearDown() throws Exception {
        this.store.close();
    }

    @Test
    public void testLoad1() throws Exception {
        Reader reader = TestUtils.getResourceReader(this.getClass(), "source-1.csv");
        CSVSource source = new CSVSource(DwcTerm.Taxon, reader, this.network.getObservables());
        source.load(this.store);

        assertEquals(11, this.store.getStore().size());
        LuceneClassifier value = this.store.get(DwcTerm.Taxon, TAXON_ID_OBS, "S-1");
        assertNotNull(value);
        assertEquals("Artemia franciscana", value.get(SCIENTIFIC_NAME_OBS));
        assertEquals("species", value.get(TAXON_RANK_OBS));
        source.close();
    }

    @Test
    public void testLoad2() throws Exception {
        URL url = this.getClass().getResource("source-1.csv");
        CSVSource source = new CSVSource(DwcTerm.Taxon, url, this.network.getObservables());
        source.load(this.store);

        assertEquals(11, this.store.getStore().size());
        LuceneClassifier value = this.store.get(DwcTerm.Taxon, TAXON_ID_OBS, "S-1");
        assertNotNull(value);
        assertEquals("Artemia franciscana", value.get(SCIENTIFIC_NAME_OBS));
        assertEquals("species", value.get(TAXON_RANK_OBS));
        source.close();
    }
}
