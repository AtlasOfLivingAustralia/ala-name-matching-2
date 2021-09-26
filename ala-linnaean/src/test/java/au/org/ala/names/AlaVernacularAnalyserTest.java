package au.org.ala.names;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Issues;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.vocab.ALATerm;
import au.org.ala.vocab.TaxonomicStatus;
import au.org.ala.vocab.VernacularStatus;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.Rank;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class AlaVernacularAnalyserTest {
    private AlaVernacularAnalyser analyser;

    @Before
    public void setUp() throws Exception {
        this.analyser = new AlaVernacularAnalyser();
    }

    @Test
    public void testAnalyseForIndex1() throws Exception {
        AlaVernacularClassification classification = new AlaVernacularClassification(this.analyser);
        classification.vernacularName = "Common Wombat";
        this.analyser.analyseForIndex(classification);
        assertEquals("Common Wombat", classification.vernacularName);
        assertEquals(1.0, classification.weight, 0.00001);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForIndex2() throws Exception {
        AlaVernacularClassification classification = new AlaVernacularClassification(this.analyser);
        classification.vernacularName = "Common Wombat";
        classification.vernacularStatus = VernacularStatus.preferred;
        this.analyser.analyseForIndex(classification);
        assertEquals("Common Wombat", classification.vernacularName);
        assertEquals(20.0, classification.weight, 0.00001);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForIndex3() throws Exception {
        AlaVernacularClassification classification = new AlaVernacularClassification(this.analyser);
        classification.vernacularName = "Common Wombat";
        classification.taxonRank = Rank.SPECIES;
        this.analyser.analyseForIndex(classification);
        assertEquals("Common Wombat", classification.vernacularName);
        assertEquals(10.0, classification.weight, 0.00001);
        assertTrue(classification.getIssues().isEmpty());
    }


    @Test
    public void testAnalyseForIndex4() throws Exception {
        AlaVernacularClassification classification = new AlaVernacularClassification(this.analyser);
        classification.vernacularName = "Common Wombat";
        classification.taxonomicStatus= TaxonomicStatus.homotypicSynonym;
        this.analyser.analyseForIndex(classification);
        assertEquals("Common Wombat", classification.vernacularName);
        assertEquals(0.1, classification.weight, 0.00001);
        assertTrue(classification.getIssues().isEmpty());
    }


    @Test
    public void testAnalyseForSearch1() throws Exception {
        AlaVernacularClassification classification = new AlaVernacularClassification(this.analyser);
        classification.vernacularName = "Dingo";
        this.analyser.analyseForSearch(classification);
        assertEquals("Dingo", classification.vernacularName);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseNames1() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaVernacularFactory.vernacularName, "King Island Dunnart");
        Set<String> names = this.analyser.analyseNames(classifier, AlaVernacularFactory.vernacularName, Optional.empty(), Optional.empty(), false);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("King Island Dunnart"));
    }

    @Test
    public void testAnalyseNames2() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaVernacularFactory.vernacularName, "Mārū");
        Set<String> names = this.analyser.analyseNames(classifier, AlaVernacularFactory.vernacularName, Optional.empty(), Optional.empty(), false);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Mārū"));
        assertTrue(names.contains("Maru"));
    }

    @Test
    public void testAnalyseNames3() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaVernacularFactory.vernacularName, "Yellow-fin Tuna");
        Set<String> names = this.analyser.analyseNames(classifier, AlaVernacularFactory.vernacularName, Optional.empty(), Optional.empty(), false);
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Yellow-fin Tuna"));
        assertTrue(names.contains("Yellow fin Tuna"));
        assertTrue(names.contains("Yellowfin Tuna"));
    }

    @Test
    public void testAnalyseNames4() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaVernacularFactory.vernacularName, "Yellow - footed Hopping-mouse");
        Set<String> names = this.analyser.analyseNames(classifier, AlaVernacularFactory.vernacularName, Optional.empty(), Optional.empty(), false);
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Yellow - footed Hopping-mouse"));
        assertTrue(names.contains("Yellow footed Hopping mouse"));
        assertTrue(names.contains("Yellowfooted Hoppingmouse"));
    }


    @Test
    public void testAnalyseNames5() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaVernacularFactory.vernacularName, "Sturt's Desert-Pea");
        Set<String> names = this.analyser.analyseNames(classifier, AlaVernacularFactory.vernacularName, Optional.empty(), Optional.empty(), false);
        assertNotNull(names);
        assertEquals(6, names.size());
        assertTrue(names.contains("Sturt's Desert-Pea"));
        assertTrue(names.contains("Sturts Desert-Pea"));
        assertTrue(names.contains("Sturt's Desert Pea"));
        assertTrue(names.contains("Sturts Desert Pea"));
        assertTrue(names.contains("Sturt's DesertPea"));
        assertTrue(names.contains("Sturts DesertPea"));
    }

}
