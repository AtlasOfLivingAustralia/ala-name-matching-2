package au.org.ala.names;

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.util.SimpleClassifier;
import au.org.ala.vocab.ALATerm;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.api.Rank;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class AlaNameAnalyserTest {
    private AlaNameAnalyser analyser;

    @Before
    public void setUp() throws Exception {
        this.analyser = new AlaNameAnalyser();
    }

    @Test
    public void testAnalyseForIndex1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata";
        this.analyser.analyseForIndex(classification);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForIndex2() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata dealbata";
        this.analyser.analyseForIndex(classification);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.INFRASPECIFIC_NAME, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseFoIndex3() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata dealbata";
        classification.taxonRank = Rank.VARIETY;
        this.analyser.analyseForIndex(classification);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.INFRASPECIFIC_NAME, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForIndex4() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Plantae";
        this.analyser.analyseForIndex(classification);
        assertNull(classification.genus);
        assertNull(classification.specificEpithet);
        assertNull(classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForIndex5() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia sp.";
        this.analyser.analyseForIndex(classification);
        assertEquals("Acacia sp.", classification.scientificName);
        assertEquals("Acacia", classification.genus);
        assertNull(classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForIndex6() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata?";
        this.analyser.analyseForIndex(classification);
        assertEquals("Acacia dealbata?", classification.scientificName);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForSearch1() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata";
        this.analyser.analyseForSearch(classification);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForSearch2() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata dealbata";
        this.analyser.analyseForSearch(classification);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.INFRASPECIFIC_NAME, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseFoSearch3() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata dealbata";
        classification.taxonRank = Rank.VARIETY;
        this.analyser.analyseForSearch(classification);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.INFRASPECIFIC_NAME, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForSearch4() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Plantae";
        this.analyser.analyseForSearch(classification);
        assertNull(classification.genus);
        assertNull(classification.specificEpithet);
        assertNull(classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }


    @Test
    public void testAnalyseForSearch5() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "sp.";
        try {
            this.analyser.analyseForSearch(classification);
            fail("Expecting inference exception");
        } catch (InferenceException e) {
        }
    }

    @Test
    public void testAnalyseForSearch6() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia dealbata?";
        this.analyser.analyseForSearch(classification);
        assertEquals("Acacia dealbata", classification.scientificName);
        assertEquals("Acacia", classification.genus);
        assertNull(classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().contains(AlaLinnaeanFactory.INDETERMINATE_NAME));
    }

    @Test
    public void testAnalyseForSearch7() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia aff. dealbata";
        this.analyser.analyseForSearch(classification);
        assertEquals("Acacia dealbata", classification.scientificName);
        assertEquals("Acacia", classification.genus);
        assertNull(classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().contains(AlaLinnaeanFactory.AFFINITY_SPECIES_NAME));
    }

    @Test
    public void testAnalyseForSearch8() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia cf. dealbata";
        this.analyser.analyseForSearch(classification);
        assertEquals("Acacia dealbata", classification.scientificName);
        assertEquals("Acacia", classification.genus);
        assertNull(classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().contains(AlaLinnaeanFactory.CONFER_SPECIES_NAME));
    }


    @Test
    public void testAnalyseForSearch9() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Acacia sp. dealbata";
        this.analyser.analyseForSearch(classification);
        assertEquals("Acacia dealbata", classification.scientificName);
        assertEquals("Acacia", classification.genus);
        assertEquals("dealbata", classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().contains(ALATerm.canonicalMatch));
    }

    @Test
    public void testAnalyseForSearch10() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Sida sp. Walhallow Station (C.Edgood 28/Oct/94)";
        this.analyser.analyseForSearch(classification);
        assertEquals("Sida sp. Walhallow Station (C.Edgood 28/Oct/94)", classification.scientificName);
        assertEquals("Sida", classification.genus);
        assertNull(classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }


    @Test
    public void testAnalyseForSearch11() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Goodenia sp. Bachsten Creek (M.D.Barrett 685) WA Herbarium";
        this.analyser.analyseForSearch(classification);
        assertEquals("Goodenia sp. Bachsten Creek (M.D.Barrett 685)", classification.scientificName);
        assertEquals("WA Herbarium", classification.nominatingParty);
        assertEquals("Goodenia", classification.genus);
        assertEquals("Bachsten Creek", classification.phraseName);
        assertNull(classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseNames1() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata");
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, "Link");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.SPECIES);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), false);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia dealbata"));
        assertTrue(names.contains("Acacia dealbata Link"));
    }

    @Test
    public void testAnalyseNames2() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata");
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, "Link");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), false);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia dealbata"));
        assertTrue(names.contains("Acacia dealbata Link"));
    }


    // Normaliser gets rid of accents.
    @Test
    public void testAnalyseNames3() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acäcia dealbata");
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, "Link");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.SPECIES);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), false);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia dealbata"));
        assertTrue(names.contains("Acacia dealbata Link"));
        assertEquals("Acacia dealbata", classifier.get(AlaLinnaeanFactory.scientificName));
    }

    @Test
    public void testAnalyseNames4() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Goodenia ser. Bracteolatae");
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, "Benth.");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.SERIES);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), false);
        assertNotNull(names);
        assertEquals(4, names.size());
        assertTrue(names.contains("Goodenia ser. Bracteolatae"));
        assertTrue(names.contains("Goodenia ser. Bracteolatae Benth."));
        assertTrue(names.contains("Bracteolatae"));
        assertTrue(names.contains("Goodenia Bracteolatae"));
    }

    @Test
    public void testAnalyseNames5() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata Link dealbata");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.SERIES);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), false);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia dealbata Link dealbata"));
        assertTrue(names.contains("Acacia dealbata dealbata"));
    }

    @Test
    public void testAnalyseNames6() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia sp. Bigge Island (A.A. Mitchell 3436)");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia sp. Bigge Island (A.A. Mitchell 3436)"));
        assertTrue(names.contains("Acacia sp. Bigge Island"));
    }


    @Test
    public void testAnalyseNames7() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia 'Morning Glory'");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), false);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia 'Morning Glory'"));
    }

    @Test
    public void testAnalyseNames8() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata");
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, "Link");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.SPECIES);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia dealbata"));
    }


    @Test
    public void testAnalyseNames9() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia sect. Acacia");
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, "Mill.");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia sect. Acacia"));
        assertTrue(names.contains("Acacia Acacia"));
    }


    @Test
    public void testAnalyseNames10() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Goodenia subsect. Bracteolatae");
        classifier.add(AlaLinnaeanFactory.scientificNameAuthorship, "(Benth.) K.Krause");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Goodenia subsect. Bracteolatae"));
        assertTrue(names.contains("Bracteolatae"));
        assertTrue(names.contains("Goodenia Bracteolatae"));
    }

    @Test
    public void testAnalyseNames11() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia 'H.L.White'");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.UNRANKED);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia 'H.L.White'"));
    }

    @Test
    public void testAnalyseNames12() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia $ Brunioideae");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.INFRAGENUS);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia $ Brunioideae"));
    }

    @Test
    public void testAnalyseNames13() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia Div. II Bipinnatae");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.INFRAGENUS);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia Div. II Bipinnatae"));
        assertTrue(names.contains("infrag. Acacia Div.II Bipinnatae"));
    }

    @Test
    public void testAnalyseNames14() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia sp. laterite");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.UNRANKED);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia sp. laterite"));
    }

    @Test
    public void testAnalyseNames15() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia sp. holey trunk");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.UNRANKED);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia sp. holey trunk"));
    }


    @Test
    public void testAnalyseNames16() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Grevillea brachystylis subsp. Busselton (G.J.Keighery s.n. 28/8/1985)");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Grevillea brachystylis subsp. Busselton (G.J.Keighery s.n. 28/8/1985)"));
        assertTrue(names.contains("Grevillea brachystylis ssp. Busselton (G.J.Keighery s.n. 28/8/1985)"));
        assertTrue(names.contains("Grevillea brachystylis subsp. Busselton"));
    }


}
