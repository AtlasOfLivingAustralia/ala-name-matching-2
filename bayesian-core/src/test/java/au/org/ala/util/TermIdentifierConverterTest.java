package au.org.ala.util;

import au.org.ala.bayesian.Observable;
import org.gbif.dwc.terms.DcTerm;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class TermIdentifierConverterTest {
    @Test
    public void testStyle1() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true);
        assertEquals("dwc_scientificName", converter.convert(new Observable("scientificName")));
        assertEquals("variety", converter.convert(new Observable("variety")));
        assertEquals("Alpha", converter.convert(new Observable("category:Alpha")));
        assertEquals("__Alpha", converter.convert(new Observable("_.Alpha")));
        assertEquals("alpha", converter.convert(new Observable("ãlpha")));
    }

    @Test
    public void testStyle2() {
        IdentifierConverter converter = new TermIdentifierConverter("[a-z0-9_]", ".", true);
        assertEquals("dwc.scientific.ame", converter.convert(new Observable("scientificName")));
        assertEquals("variety", converter.convert(new Observable("variety")));
        assertEquals(".lpha", converter.convert(new Observable("category:Alpha")));
        assertEquals("_.lpha", converter.convert(new Observable("_:Alpha")));
        assertEquals("alpha", converter.convert(new Observable("ãlpha")));
    }

    @Test
    public void testUriConversion1() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true);
        Observable o = new Observable("fred");
        o.setUri(URI.create(DcTerm.audience.qualifiedName()));
        assertEquals("dcterms_audience", converter.convert(o));
    }

    @Test
    public void testMultiTerm1() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true);
        assertEquals("variety", converter.convert(new Observable(URI.create("http://nowhere.com/variety"))));
        assertEquals("variety_", converter.convert(new Observable(URI.create("https://id.ala.org.au/terms/variety"))));
        assertEquals("variety_1", converter.convert(new Observable(URI.create("https://blah.org.au/terms/variety"))));
        assertEquals("variety_2", converter.convert(new Observable("variety")));
    }

    @Test
    public void testMultiTerm2() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", false);
        assertEquals("variety", converter.convert(new Observable(URI.create("http://nowhere.com/variety"))));
        assertEquals("variety", converter.convert(new Observable(URI.create("https://id.ala.org.au/terms/variety"))));
        assertEquals("variety", converter.convert(new Observable(URI.create("https://blah.org.au/terms/variety"))));
        assertEquals("variety", converter.convert(new Observable("variety")));
    }

}
