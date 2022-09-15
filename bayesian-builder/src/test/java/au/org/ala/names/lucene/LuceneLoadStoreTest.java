package au.org.ala.names.lucene;

import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Annotator;
import au.org.ala.names.builder.TestAnnotator;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class LuceneLoadStoreTest {
    private static final Observable<String> TAXON_ID_OBS = Observable.string(DwcTerm.taxonID);
    private static final Observable<String> SCIENTIFIC_NAME_OBS = Observable.string(DwcTerm.scientificName);
    private static final Observable<String> GENUS_OBS = Observable.string(DwcTerm.genus);
    private static final Observable<String> FAMILY_OBS = Observable.string(DwcTerm.family);

    private Network network;
    private Annotator annotator;
    private LuceneLoadStore store;

    @Before
    public void setUp() throws Exception {
        this.network = new Network();
        this.annotator = new TestAnnotator();
        this.store = new LuceneLoadStore("test", null, true, true, 0);
    }

    @After
    public void cleanUp() throws Exception {
        if (this.store != null) {
            this.store.close();
            this.store = null;
        }
    }

    private LuceneClassifier makeClassifier(Term type, String... vals) throws StoreException {
        if (vals.length % 2 != 0)
            throw new IllegalArgumentException("Need term-value pair");
        int i = 0;
        LuceneClassifier classifier = new LuceneClassifier();
        while (i < vals.length) {
            Term term = TermFactory.instance().findTerm(vals[i++]);
            String value = vals[i++];
            classifier.add(Observable.string(term), value, false, false);
        }
        return classifier;
    }


    @Test
    public void testStoreGet1() throws Exception {
        LuceneClassifier v1 = this.makeClassifier(DwcTerm.Taxon, "taxonID", "ID-1", "scientificName", "Acacia dealbata", "genus", "Acacia", "family", "Fabaceae");
        this.store.store(v1, DwcTerm.Taxon);
        LuceneClassifier v1r = this.store.get(DwcTerm.Taxon, TAXON_ID_OBS, "ID-1");
        assertNotNull(v1r);
        assertEquals("ID-1", v1r.get(TAXON_ID_OBS).toString());
        assertEquals("Acacia dealbata", v1r.get(SCIENTIFIC_NAME_OBS).toString());
        assertEquals("Acacia", v1r.get(GENUS_OBS).toString());
        assertEquals("Fabaceae", v1r.get(FAMILY_OBS).toString());
        assertEquals(DwcTerm.Taxon, v1r.getType());
    }

    @Test
    public void testStoreGet2() throws Exception {
        LuceneClassifier v1 = this.makeClassifier(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia dealbata", "genus", "Acacia", "family", "Fabaceae");
        LuceneClassifier v2 = this.makeClassifier(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia abbreviata", "genus", "Acacia", "family", "Fabaceae");
        this.store.store(v1, DwcTerm.Taxon);
        this.store.store(v2, DwcTerm.Taxon);
        try {
            LuceneClassifier v1r = this.store.get(DwcTerm.Taxon, TAXON_ID_OBS, "ID-1");
            fail("Expecting store exception");
        } catch (StoreException ex) {
            assertEquals("More than one answer for taxonID = ID-1", ex.getMessage());
        }
    }

    @Test
    public void testStoreGetAll() throws Exception {
        LuceneClassifier v1 = this.makeClassifier(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia dealbata", "genus", "Acacia", "family", "Fabaceae");
        LuceneClassifier v2 = this.makeClassifier(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia abbreviata", "genus", "Acacia", "family", "Fabaceae");
        this.store.store(v1, DwcTerm.Taxon);
        this.store.store(v2, DwcTerm.Taxon);
        this.store.commit();
        Iterator<LuceneClassifier> di = this.store.getAll(DwcTerm.Taxon, new Observation(true, TAXON_ID_OBS, "ID-1")).iterator();
        assertTrue(di.hasNext());
        LuceneClassifier v1r = di.next();
        assertNotNull(v1r);
        assertEquals("ID-1", v1r.get(TAXON_ID_OBS).toString());
        assertEquals("Acacia", v1r.get(GENUS_OBS).toString());
        assertTrue(di.hasNext());
        LuceneClassifier v2r = di.next();
        assertNotNull(v2r);
        assertEquals("ID-1", v2r.get(TAXON_ID_OBS).toString());
        assertEquals("Acacia", v2r.get(GENUS_OBS).toString());
        assertFalse(di.hasNext());
    }

}
