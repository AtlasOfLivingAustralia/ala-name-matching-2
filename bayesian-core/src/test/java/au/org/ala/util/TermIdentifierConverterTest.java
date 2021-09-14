package au.org.ala.util;

import au.org.ala.bayesian.Observable;
import org.gbif.dwc.terms.DcTerm;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class TermIdentifierConverterTest {
    @Test
    public void testStyle1() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true, null, null);
        assertEquals("dwc_scientificName", converter.convert(new Observable("scientificName")));
        assertEquals("variety", converter.convert(new Observable("variety")));
        assertEquals("Alpha", converter.convert(new Observable("category:Alpha")));
        assertEquals("__Alpha", converter.convert(new Observable("_.Alpha")));
        assertEquals("alpha", converter.convert(new Observable("ãlpha")));
    }

    @Test
    public void testStyle2() {
        IdentifierConverter converter = new TermIdentifierConverter("[a-z0-9_]", ".", true, null, null);
        assertEquals("dwc.scientific.ame", converter.convert(new Observable("scientificName")));
        assertEquals("variety", converter.convert(new Observable("variety")));
        assertEquals(".lpha", converter.convert(new Observable("category:Alpha")));
        assertEquals("_.lpha", converter.convert(new Observable("_:Alpha")));
        assertEquals("alpha", converter.convert(new Observable("ãlpha")));
    }

    public void testStyle3() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true, "pre_", null);
        assertEquals("pre_dwc_scientificName", converter.convert(new Observable("scientificName")));
        assertEquals("pre_variety", converter.convert(new Observable("variety")));
        assertEquals("pre_Alpha", converter.convert(new Observable("category:Alpha")));
        assertEquals("pre___Alpha", converter.convert(new Observable("_.Alpha")));
        assertEquals("pre_alpha", converter.convert(new Observable("ãlpha")));
    }

    @Test
    public void testStyle4() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true, null, "_suf");
        assertEquals("dwc_scientificName_suf", converter.convert(new Observable("scientificName")));
        assertEquals("variety_suf", converter.convert(new Observable("variety")));
        assertEquals("Alpha_suf", converter.convert(new Observable("category:Alpha")));
        assertEquals("__Alpha_suf", converter.convert(new Observable("_.Alpha")));
        assertEquals("alpha_suf", converter.convert(new Observable("ãlpha")));
    }

    @Test
    public void testUriConversion1() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true, null, null);
        Observable o = new Observable("fred");
        o.setUri(URI.create(DcTerm.audience.qualifiedName()));
        assertEquals("dcterms_audience", converter.convert(o));
    }


    @Test
    public void testUriConversion2() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true, "p_", "_s");
        Observable o = new Observable("fred");
        o.setUri(URI.create(DcTerm.audience.qualifiedName()));
        assertEquals("p_dcterms_audience_s", converter.convert(o));
    }

    @Test
    public void testMultiTerm1() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", true, null, null);
        assertEquals("variety", converter.convert(new Observable(URI.create("http://nowhere.com/variety"))));
        assertEquals("variety_", converter.convert(new Observable(URI.create("https://id.ala.org.au/terms/variety"))));
        assertEquals("variety_1", converter.convert(new Observable(URI.create("https://blah.org.au/terms/variety"))));
        assertEquals("variety_2", converter.convert(new Observable("variety")));
    }

    @Test
    public void testMultiTerm2() {
        IdentifierConverter converter = new TermIdentifierConverter("[A-Za-z0-9_]", "_", false, null, null);
        assertEquals("variety", converter.convert(new Observable(URI.create("http://nowhere.com/variety"))));
        assertEquals("variety", converter.convert(new Observable(URI.create("https://id.ala.org.au/terms/variety"))));
        assertEquals("variety", converter.convert(new Observable(URI.create("https://blah.org.au/terms/variety"))));
        assertEquals("variety", converter.convert(new Observable("variety")));
    }

}
