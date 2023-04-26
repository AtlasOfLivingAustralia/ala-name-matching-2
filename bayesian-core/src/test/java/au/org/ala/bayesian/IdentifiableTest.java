package au.org.ala.bayesian;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class IdentifiableTest {
    @Test
    public void makeIdFromURI1() throws Exception {
        assertEquals("subgenus", Identifiable.makeIdFromURI(URI.create("http://ala.org.au/terms/subgenus")));
    }

    @Test
    public void makeIdFromURI2() throws Exception {
        assertEquals("subgenus", Identifiable.makeIdFromURI(URI.create("http://ala.org.au/terms/subgenus#")));
    }

    @Test
    public void makeIdFromURI3() throws Exception {
        assertEquals("type", Identifiable.makeIdFromURI(URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")));
    }

    @Test
    public void makeIdFromURI4() throws Exception {
        assertEquals("_978-0143035008", Identifiable.makeIdFromURI(URI.create("urn:isbn:978-0143035008")));
    }

    @Test
    public void makeIdFromURI5() throws Exception {
        assertEquals("nothing", Identifiable.makeIdFromURI(URI.create("nothing")));
    }

    @Test
    public void makeIdFromURI6() throws Exception {
        assertEquals("unknown", Identifiable.makeIdFromURI(URI.create("")));
    }
}
