package au.org.ala.names;

import au.org.ala.names.builder.DwCASource;
import au.org.ala.vocab.ALATerm;
import org.gbif.dwc.ArchiveFile;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.Term;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.Assert.*;

public class VernacularNameOrderTest {
    private static final Term CONCEPT = GbifTerm.VernacularName;
    private static final Term NAME_ID = ALATerm.nameID;
    private VernacularNameOrder order;
    private DwCASource source;
    private ArchiveFile archiveFile;

    @Before
    public void setup() throws Exception {
        this.order = new VernacularNameOrder();
        URL location = this.getClass().getResource("/sample-1.zip");
        this.source = new DwCASource(location, new AlaVernacularFactory(), AlaVernacularFactory.OBSERVABLES, Arrays.asList(CONCEPT));
        this.archiveFile = this.source.getArchive().getExtension(CONCEPT);
        assertNotNull(this.archiveFile);
    }

    @After
    public void tearDown() throws Exception {
        if (this.source != null)
            this.source.close();
    }

    private Record find(Term term, String value) {
        for (Record record: this.archiveFile) {
            String rv = record.value(term);
            if (Objects.equals(rv, value))
                return record;
        }
        fail("Can't find " + value + " in " + term);
        return null;
    }

    @Test
    public void testCompare1() {
        Record record1 = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/434071");
        Record record2 = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/434071");
        assertEquals(0, this.order.compare(null, null));
        assertEquals(0, this.order.compare(record1, record2));
        assertEquals(Integer.MIN_VALUE, this.order.compare(null, record2));
        assertEquals(Integer.MAX_VALUE, this.order.compare(record1, null));
    }

    @Test
    public void testTest2() {
        Record record1 = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/443207");
        Record record2 = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/444663");
        assertEquals(-100, this.order.compare(record1, record2));
        assertEquals(100, this.order.compare(record2, record1));
    }

    @Test
    public void testTest3() {
        Record record1 = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/443207");
        Record record2 = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/51313199");
        assertEquals(201, this.order.compare(record1, record2));
        assertEquals(-201, this.order.compare(record2, record1));
    }

    @Test
    public void testTest4() {
        Record record1 = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/443207");
        Record record2 = this.find(NAME_ID, "urn:lsid:biodiversity.org.au:afd.name:242991");
        assertEquals(-50, this.order.compare(record1, record2));
        assertEquals(50, this.order.compare(record2, record1));
    }
}