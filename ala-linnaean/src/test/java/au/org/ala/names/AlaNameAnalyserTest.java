package au.org.ala.names;

import au.org.ala.bayesian.Issues;
import au.org.ala.util.SimpleClassifier;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AlaNameAnalyserTest {
    private AlaNameAnalyser analyser;

    @Before
    public void setUp() throws Exception {
        this.analyser = new AlaNameAnalyser();
    }

    @Test
    public void testClassificationNamePopulation1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata";
        classification.infer();
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals("species", classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testClassificationNamePopulation2() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata dealbata";
        classification.infer();
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals("infraspecific_name", classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testClassificationNamePopulation3() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata dealbata";
        classification.taxonRank = "variety";
        classification.infer();
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals("infraspecific_name", classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testClassificationNamePopulation4() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Plantae";
        classification.infer();
        assertNull(classification.genus);
        assertNull(classification.specificEpithet);
        assertNull(classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

}
