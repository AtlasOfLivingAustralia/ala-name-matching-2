package au.org.ala.names.builder;

import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.Observable;
import au.org.ala.util.TestUtils;
import org.apache.lucene.document.Document;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.Reader;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Test cases for {@link DwCASource}
 */
public class DwCASourceTest {
    private static final Observable TAXON_ID_OBS = new Observable(DwcTerm.taxonID);
    private static final Observable TAXON_RANK_OBS = new Observable(DwcTerm.taxonRank);
    private static final Observable SCIENTIFIC_NAME_OBS = new Observable(DwcTerm.scientificName);
    private static final Observable VERNACULAR_NAME_OBS = new Observable(DwcTerm.vernacularName);

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
        URL sample = this.getClass().getResource("/sample-1");
        DwCASource source = new DwCASource(sample);
        source.load(this.store);

        assertEquals(710, this.store.getStore().size());
        Document value = this.store.get(DwcTerm.Taxon, TAXON_ID_OBS, "https://id.biodiversity.org.au/node/apni/2901022");
        assertNotNull(value);
        assertEquals("Canarium acutifolium", value.get(SCIENTIFIC_NAME_OBS.getField()));
        assertEquals("species", value.get(TAXON_RANK_OBS.getField()));

        value = this.store.get(GbifTerm.VernacularName, TAXON_ID_OBS, "https://id.biodiversity.org.au/node/apni/2913682");
        assertNotNull(value);
        assertEquals("Mallow", value.get(VERNACULAR_NAME_OBS.getField()));
        source.close();
    }
}
