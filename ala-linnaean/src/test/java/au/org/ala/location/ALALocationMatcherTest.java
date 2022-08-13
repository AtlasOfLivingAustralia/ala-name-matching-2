package au.org.ala.location;

import au.org.ala.bayesian.Match;
import au.org.ala.bayesian.MatchMeasurement;
import au.org.ala.bayesian.MatchOptions;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import au.org.ala.vocab.GeographyType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ALALocationMatcherTest {
    public static final String INDEX = "/data/lucene/location-2022";

    private LuceneClassifierSearcher searcher;
    private ALALocationClassificationMatcher matcher;

    @Before
    public void setUp() throws Exception {
        File index = new File(INDEX);
        if (!index.exists())
            throw new IllegalStateException("Index " + index + " not present");
        this.searcher = new LuceneClassifierSearcher(index, null, AlaLocationFactory.locationId);
        this.matcher = new ALALocationClassificationMatcher(AlaLocationFactory.instance(), this.searcher, null);
    }

    @After
    public void tearDown() throws Exception {
        if (this.searcher != null)
            this.searcher.close();
    }


    @Test
    public void testSimpleMatch1() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "Europe";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/1000003", match.getAccepted().locationId);
        assertEquals(GeographyType.continent, match.getAccepted().geographyType);
        assertEquals("Europe", match.getAccepted().locality);
        assertEquals("Europe", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertNull(match.getAccepted().country);
        assertNull(match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleMatch2() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "France";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/1000070", match.getAccepted().locationId);
        assertEquals(GeographyType.country, match.getAccepted().geographyType);
        assertEquals("France", match.getAccepted().locality);
        assertEquals("Europe", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("France", match.getAccepted().country);
        assertEquals("FR", match.getAccepted().countryCode);
        assertNull(match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleMatch3() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "New South Wales";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/7001828", match.getAccepted().locationId);
        assertEquals(GeographyType.stateProvince, match.getAccepted().geographyType);
        assertEquals("New South Wales", match.getAccepted().locality);
        assertEquals("Oceania", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("Australia", match.getAccepted().country);
        assertEquals("AU", match.getAccepted().countryCode);
        assertEquals("New South Wales", match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleMatch4() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "Austral Islands";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/1009851", match.getAccepted().locationId);
        assertEquals(GeographyType.islandGroup, match.getAccepted().geographyType);
        assertEquals("Austral Islands", match.getAccepted().locality);
        assertEquals("Oceania", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertEquals("Austral Islands", match.getAccepted().islandGroup);
        assertEquals("French Polynesia", match.getAccepted().country);
        assertEquals("PF", match.getAccepted().countryCode);
        assertNull(match.getAccepted().stateProvince);
        assertEquals(0.99999, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleMatch5() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "Heard Island";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/1007365", match.getAccepted().locationId);
        assertEquals(GeographyType.island, match.getAccepted().geographyType);
        assertEquals("Heard Island", match.getAccepted().locality);
        assertEquals("Oceania", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("Australia", match.getAccepted().country);
        assertEquals("AU", match.getAccepted().countryCode);
        assertEquals("Heard Island and McDonald Islands", match.getAccepted().stateProvince);
        assertEquals(0.16915, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleMatch6() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "Atlantic Ocean";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/7014206", match.getAccepted().locationId);
        assertEquals(GeographyType.waterBody, match.getAccepted().geographyType);
        assertEquals("Atlantic Ocean", match.getAccepted().locality);
        assertNull(match.getAccepted().continent);
        assertEquals("Atlantic Ocean", match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertNull(match.getAccepted().country);
        assertNull(match.getAccepted().countryCode);
        assertNull(match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testSimpleMatch7() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "Tasman Sea";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/1112316", match.getAccepted().locationId);
        assertEquals(GeographyType.waterBody, match.getAccepted().geographyType);
        assertEquals("Tasman Sea", match.getAccepted().locality);
        assertNull(match.getAccepted().continent);
        assertEquals("Tasman Sea", match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertNull(match.getAccepted().country);
        assertNull(match.getAccepted().countryCode);
        assertNull(match.getAccepted().stateProvince);
        assertEquals(0.99999, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testConfirmedMatch1() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "New South Wales";
        classification.country = "Australia";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/7001828", match.getAccepted().locationId);
        assertEquals(GeographyType.stateProvince, match.getAccepted().geographyType);
        assertEquals("New South Wales", match.getAccepted().locality);
        assertEquals("Oceania", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("Australia", match.getAccepted().country);
        assertEquals("AU", match.getAccepted().countryCode);
        assertEquals("New South Wales", match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }


    @Test
    public void testConfirmedMatch2() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "Twin Islands";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertEquals(0.05263, match.getProbability().getPosterior(), 0.00001);
        classification.continent = "Oceania";
        match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertEquals(0.34101, match.getProbability().getPosterior(), 0.00001);
        classification.stateProvince = "Western Australia";
        match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertEquals(0.75828, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testConfirmedMatch3() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "North Province";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertEquals(0.33333, match.getProbability().getPosterior(), 0.00001);
        classification.continent = "Oceania";
        match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertEquals(0.82375, match.getProbability().getPosterior(), 0.00001);
        classification.continent = "Asia";
        classification.soundexContinent = null;
        match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertEquals(0.65268, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testCapitalisation1() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "new south wAles";
        classification.country = "AUSTRALIA";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/7001828", match.getAccepted().locationId);
        assertEquals(GeographyType.stateProvince, match.getAccepted().geographyType);
        assertEquals("New South Wales", match.getAccepted().locality);
        assertEquals("Oceania", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("Australia", match.getAccepted().country);
        assertEquals("AU", match.getAccepted().countryCode);
        assertEquals("New South Wales", match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }


    @Test
    public void testAlternateName1() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "NSW";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/7001828", match.getAccepted().locationId);
        assertEquals(GeographyType.stateProvince, match.getAccepted().geographyType);
        assertEquals("New South Wales", match.getAccepted().locality);
        assertEquals("Oceania", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("Australia", match.getAccepted().country);
        assertEquals("AU", match.getAccepted().countryCode);
        assertEquals("New South Wales", match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }

    @Test
    public void testAlternateName2() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "NSW";
        classification.countryCode = "AU";
        Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/7001828", match.getAccepted().locationId);
        assertEquals(GeographyType.stateProvince, match.getAccepted().geographyType);
        assertEquals("New South Wales", match.getAccepted().locality);
        assertEquals("Oceania", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("Australia", match.getAccepted().country);
        assertEquals("AU", match.getAccepted().countryCode);
        assertEquals("New South Wales", match.getAccepted().stateProvince);
        assertEquals(1.0, match.getProbability().getPosterior(), 0.00001);
    }


    @Test
    public void testAlternateName3() throws Exception {
        AlaLocationClassification classification = new AlaLocationClassification();
        classification.locality = "Territorial Collectivity of Mayotte";
         Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification, MatchOptions.ALL);
        assertTrue(match.isValid());
        assertSame(match.getMatch(), match.getAccepted());
        assertEquals("http://vocab.getty.edu/tgn/7001669", match.getAccepted().locationId);
        assertEquals(GeographyType.country, match.getAccepted().geographyType);
        assertEquals("Mayotte", match.getAccepted().locality);
        assertEquals("Africa", match.getAccepted().continent);
        assertNull(match.getAccepted().waterBody);
        assertNull(match.getAccepted().islandGroup);
        assertEquals("Mayotte", match.getAccepted().country);
        assertEquals("YT", match.getAccepted().countryCode);
        assertNull(match.getAccepted().stateProvince);
        assertEquals(0.99998, match.getProbability().getPosterior(), 0.00001);
    }

}