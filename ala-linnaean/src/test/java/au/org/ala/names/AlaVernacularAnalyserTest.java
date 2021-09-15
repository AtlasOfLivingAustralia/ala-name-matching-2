package au.org.ala.names;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Issues;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.vocab.ALATerm;
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
}
