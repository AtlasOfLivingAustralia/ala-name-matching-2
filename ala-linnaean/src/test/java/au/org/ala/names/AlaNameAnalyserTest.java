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
    public void testClassifierNamePopulation1() throws Exception {
        Issues issues = new Issues();
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata");
        this.analyser.analyse(classifier, issues);
        assertEquals("Acacia", classifier.get(AlaLinnaeanFactory.genus));
        assertEquals("dealbata", classifier.get(AlaLinnaeanFactory.specificEpithet));
        assertEquals("species", classifier.get(AlaLinnaeanFactory.taxonRank));
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testClassifierNamePopulation2() throws Exception {
        Issues issues = new Issues();
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata dealbata");
        this.analyser.analyse(classifier, issues);
        assertEquals("Acacia", classifier.get(AlaLinnaeanFactory.genus));
        assertEquals("dealbata", classifier.get(AlaLinnaeanFactory.specificEpithet));
        assertEquals("infraspecific_name", classifier.get(AlaLinnaeanFactory.taxonRank));
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testClassifierNamePopulation3() throws Exception {
        Issues issues = new Issues();
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata dealbata");
        classifier.add(AlaLinnaeanFactory.taxonRank, "variety");
        this.analyser.analyse(classifier, issues);
        assertEquals("Acacia", classifier.get(AlaLinnaeanFactory.genus));
        assertEquals("dealbata", classifier.get(AlaLinnaeanFactory.specificEpithet));
        assertEquals("infraspecific_name", classifier.get(AlaLinnaeanFactory.taxonRank));
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testClassifierNamePopulation4() throws Exception {
        Issues issues = new Issues();
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Plantae");
        this.analyser.analyse(classifier, issues);
        assertNull(classifier.get(AlaLinnaeanFactory.genus));
        assertNull(classifier.get(AlaLinnaeanFactory.specificEpithet));
        assertNull(classifier.get(AlaLinnaeanFactory.taxonRank));
        assertTrue(issues.isEmpty());
    }
    
    @Test
    public void testClassificationNamePopulation1() throws Exception {
        Issues issues = new Issues();
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Acacia dealbata";
        this.analyser.analyse(classification, issues);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals("species", classification.taxonRank);
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testClassificationNamePopulation2() throws Exception {
        Issues issues = new Issues();
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Acacia dealbata dealbata";
        this.analyser.analyse(classification, issues);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals("infraspecific_name", classification.taxonRank);
    }

    @Test
    public void testClassificationNamePopulation3() throws Exception {
        Issues issues = new Issues();
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Acacia dealbata dealbata";
        classification.taxonRank = "variety";
        this.analyser.analyse(classification, issues);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals("infraspecific_name", classification.taxonRank);
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testClassificationNamePopulation4() throws Exception {
        Issues issues = new Issues();
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
        classification.scientificName = "Plantae";
        this.analyser.analyse(classification, issues);
        assertNull(classification.genus);
        assertNull(classification.specificEpithet);
        assertNull(classification.taxonRank);
        assertTrue(issues.isEmpty());
    }

}
