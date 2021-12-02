package au.org.ala.names;

import au.org.ala.names.builder.DwCASource;
import au.org.ala.vocab.ALATerm;
import org.gbif.dwc.Archive;
import org.gbif.dwc.ArchiveFile;
import org.gbif.dwc.DwcFiles;
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

public class VernacularNameFilterTest {
    private static final Term CONCEPT = GbifTerm.VernacularName;
    private static final Term NAME_ID = ALATerm.nameID;
    private VernacularNameFilter filter;
    private DwCASource source;
    private ArchiveFile archiveFile;

    @Before
    public void setup() throws Exception {
        this.filter = new VernacularNameFilter();
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
    public void testTest1() {
        Record record = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/434071");
        assertTrue(filter.test(record));
    }

    @Test
    public void testTest2() {
        Record record = this.find(NAME_ID, "urn:lsid:biodiversity.org.au:afd.name:246216");
        assertTrue(filter.test(record));
    }

    @Test
    public void testTest3() {
        Record record = this.find(NAME_ID, "urn:lsid:biodiversity.org.au:afd.name:242991");
        assertTrue(filter.test(record));
    }

    @Test
    public void testTest4() {
        Record record = this.find(NAME_ID, "https://id.biodiversity.org.au/name/apni/51313199");
        assertFalse(filter.test(record));
    }
}