package au.org.ala.names.builder;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.Observable;
import au.org.ala.names.generated.SimpleLinnaeanFactory;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Test cases for {@link DwCASource}
 */
public class DwCASourceTest {
    private static final Observable VERNACULAR_NAME_OBS = new Observable(DwcTerm.vernacularName);

    private Network network;
    private SimpleLinnaeanFactory factory;
    private Annotator annotator;
    private TestLoadStore store;

    @Before
    public void setUp() throws Exception {
        this.network = new Network();
        this.factory = SimpleLinnaeanFactory.instance();
        this.annotator = new TestAnnotator();
        this.store = new TestLoadStore(this.annotator);
    }

    @After
    public void tearDown() throws Exception {
        this.store.close();
    }

    @Test
    public void testLoad1() throws Exception {
        URL sample = this.getClass().getResource("/sample-1.zip");
        DwCASource source = new DwCASource(sample, this.factory, this.network.getObservables(), Arrays.asList(DwcTerm.Taxon, GbifTerm.VernacularName));
        source.load(this.store, null);

        assertEquals(154, this.store.getStore().size());
        Classifier value = this.store.get(DwcTerm.Taxon, SimpleLinnaeanFactory.taxonId, "https://id.biodiversity.org.au/node/apni/2901022");
        assertNotNull(value);
        assertEquals("Canarium acutifolium", value.get(SimpleLinnaeanFactory.scientificName));
        assertEquals("species", value.get(SimpleLinnaeanFactory.taxonRank));
        assertEquals("accepted", value.get(SimpleLinnaeanFactory.taxonomicStatus));

        value = this.store.get(GbifTerm.VernacularName, SimpleLinnaeanFactory.taxonId, "https://id.biodiversity.org.au/node/apni/2913682");
        assertNotNull(value);
        assertEquals("Mallow", value.get(VERNACULAR_NAME_OBS));
        source.close();
    }

    @Test
    public void testLoad2() throws Exception {
        URL sample = this.getClass().getResource("/sample-1.zip");
        DwCASource source = new DwCASource(sample, this.factory, this.network.getObservables(), Arrays.asList(DwcTerm.Taxon, GbifTerm.VernacularName));
        source.load(this.store, Arrays.asList(SimpleLinnaeanFactory.taxonId, SimpleLinnaeanFactory.scientificName));

        assertEquals(154, this.store.getStore().size());
        Classifier value = this.store.get(DwcTerm.Taxon, SimpleLinnaeanFactory.taxonId, "https://id.biodiversity.org.au/node/apni/2901022");
        assertNotNull(value);
        assertEquals("Canarium acutifolium", value.get(SimpleLinnaeanFactory.scientificName));
        assertNull(value.get(SimpleLinnaeanFactory.taxonRank));
        assertNull(value.get(SimpleLinnaeanFactory.taxonomicStatus));

        value = this.store.get(GbifTerm.VernacularName, SimpleLinnaeanFactory.taxonId, "https://id.biodiversity.org.au/node/apni/2913682");
        assertNotNull(value);
        assertNull(value.get(VERNACULAR_NAME_OBS));
        source.close();
    }
}
