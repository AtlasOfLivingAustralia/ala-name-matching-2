package au.org.ala.names.lucene;

import au.org.ala.bayesian.Network;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.names.builder.Annotator;
import au.org.ala.names.builder.LoadStore;
import au.org.ala.names.builder.StoreException;
import au.org.ala.names.builder.TestAnnotator;
import au.org.ala.names.model.ExternalContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class LuceneLoadStoreTest {
    private static final Observable TAXON_ID_OBS = new Observable(DwcTerm.taxonID);
    private static final Observable SCIENTIFIC_NAME_OBS = new Observable(DwcTerm.scientificName);
    private static final Observable GENUS_OBS = new Observable(DwcTerm.genus);
    private static final Observable FAMILY_OBS = new Observable(DwcTerm.family);

    private Network network;
    private Annotator annotator;
    private LuceneLoadStore store;

    @Before
    public void setUp() throws Exception {
        this.network = new Network();
        this.annotator = new TestAnnotator();
        this.store = new LuceneLoadStore(this.annotator, null, true);
    }

    @After
    public void cleanUp() throws Exception {
        if (this.store != null) {
            this.store.close();
            this.store = null;
        }
    }

    private Document makeDocument(Term type, String... vals) throws StoreException {
        if (vals.length % 2 != 0)
            throw new IllegalArgumentException("Need term-value pair");
        int i = 0;
        Document document = new Document();
        while (i < vals.length) {
            Term term = TermFactory.instance().findTerm(vals[i++]);
            String value = vals[i++];
            document.add(this.store.convert(new Observable(term), value));
        }
        return document;
    }


    @Test
    public void testStoreGet1() throws Exception {
        Document v1 = this.makeDocument(DwcTerm.Taxon, "taxonID", "ID-1", "scientificName", "Acacia dealbata", "genus", "Acacia", "family", "Fabaceae");
        this.store.store(v1, DwcTerm.Taxon);
        Document v1r = this.store.get(DwcTerm.Taxon, TAXON_ID_OBS, "ID-1");
        assertNotNull(v1r);
        assertEquals("ID-1", v1r.get(TAXON_ID_OBS.getField()));
        assertEquals("Acacia dealbata", v1r.get(SCIENTIFIC_NAME_OBS.getField()));
        assertEquals("Acacia", v1r.get(GENUS_OBS.getField()));
        assertEquals("Fabaceae", v1r.get(FAMILY_OBS.getField()));
        assertEquals(DwcTerm.Taxon.qualifiedName(), v1r.get(this.annotator.getTypeField()));
    }

    @Test
    public void testStoreGet2() throws Exception {
        Document v1 = this.makeDocument(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia dealbata", "genus", "Acacia", "family", "Fabaceae");
        Document v2 = this.makeDocument(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia abbreviata", "genus", "Acacia", "family", "Fabaceae");
        this.store.store(v1, DwcTerm.Taxon);
        this.store.store(v2, DwcTerm.Taxon);
        try {
            Document v1r = this.store.get(DwcTerm.Taxon, TAXON_ID_OBS, "ID-1");
            fail("Expecting store exception");
        } catch (StoreException ex) {
            assertEquals("More than one answer for taxonID = ID-1", ex.getMessage());
        }
    }

    @Test
    public void testStoreGetAll() throws Exception {
        Document v1 = this.makeDocument(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia dealbata", "genus", "Acacia", "family", "Fabaceae");
        Document v2 = this.makeDocument(DwcTerm.Taxon,"taxonID", "ID-1", "scientificName", "Acacia abbreviata", "genus", "Acacia", "family", "Fabaceae");
        this.store.store(v1, DwcTerm.Taxon);
        this.store.store(v2, DwcTerm.Taxon);
        this.store.commit();
        Iterator<Document> di = this.store.getAll(DwcTerm.Taxon, new Observation(true, TAXON_ID_OBS, "ID-1")).iterator();
        assertTrue(di.hasNext());
        Document v1r = di.next();
        assertNotNull(v1r);
        assertEquals("ID-1", v1r.get(TAXON_ID_OBS.getField()));
        assertEquals("Acacia", v1r.get(GENUS_OBS.getField()));
        assertTrue(di.hasNext());
        Document v2r = di.next();
        assertNotNull(v2r);
        assertEquals("ID-1", v2r.get(TAXON_ID_OBS.getField()));
        assertEquals("Acacia", v2r.get(GENUS_OBS.getField()));
        assertFalse(di.hasNext());
    }

}
