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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class AlaNameAnalyserTest {
    private static final Logger logger = LoggerFactory.getLogger(AlaNameAnalyserTest.class);

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
        assertEquals(Rank.VARIETY, classification.taxonRank);
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
        assertNull(classification.genus);
        assertNull(classification.specificEpithet);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertEquals(Issues.of(AlaLinnaeanFactory.INDETERMINATE_NAME), classification.getIssues());
    }

    // Separate specific and cultivar epithet
    @Test
    public void testAnalyseForIndex7() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Conospermum taxifolium 'Tasmanian form'";
        this.analyser.analyseForIndex(classification);
        assertEquals("Conospermum taxifolium 'Tasmanian form'", classification.scientificName);
        assertEquals("Conospermum", classification.genus);
        assertEquals("taxifolium", classification.specificEpithet);
        assertEquals("Tasmanian form", classification.cultivarEpithet);
        assertEquals(Rank.CULTIVAR, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    // Separate specific and cultivar epithet
    @Test
    public void testAnalyseForIndex8() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Conospermum taxifolium 'Tasmanian form'";
        classification.genus = "Conospermum";
        classification.specificEpithet = "taxifolium 'Tasmanian form'";
        this.analyser.analyseForIndex(classification);
        assertEquals("Conospermum taxifolium 'Tasmanian form'", classification.scientificName);
        assertEquals("Conospermum", classification.genus);
        assertEquals("taxifolium", classification.specificEpithet);
        assertEquals("Tasmanian form", classification.cultivarEpithet);
        assertEquals(Rank.CULTIVAR, classification.taxonRank);
        assertEquals(Issues.of(AlaLinnaeanFactory.CANONICAL_NAME), classification.getIssues());
    }

    // Ranks
    @Test
    public void testAnalyseForIndex9() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Melanogaster";
        classification.taxonRank = Rank.SERIES;
        this.analyser.analyseForIndex(classification);
        assertEquals("Melanogaster", classification.scientificName);
        assertEquals(Rank.SERIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForIndex10() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Goodenia sp. Bachsten Creek (M.D.Barrett 685) WA Herbarium";
        this.analyser.analyseForIndex(classification);
        assertEquals("Goodenia sp. Bachsten Creek (M.D.Barrett 685)", classification.scientificName);
        assertEquals("WA Herbarium", classification.nominatingParty);
        assertEquals("Goodenia", classification.genus);
        assertEquals("BACHSTENCREEK", classification.phraseName);
        assertEquals("MDBARRETT685", classification.voucher);
        assertNull(classification.specificEpithet);
        assertNull(classification.scientificNameAuthorship);
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
        assertEquals(Rank.VARIETY, classification.taxonRank);
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
        assertEquals("Acacia aff. dealbata", classification.scientificName);
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
        assertEquals("Acacia cf. dealbata", classification.scientificName);
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
        assertTrue(classification.getIssues().contains(AlaLinnaeanFactory.CANONICAL_NAME));
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
        assertEquals("BACHSTENCREEK", classification.phraseName);
        assertEquals("MDBARRETT685", classification.voucher);
        assertNull(classification.specificEpithet);
        assertNull(classification.scientificNameAuthorship);
        assertEquals(Rank.SPECIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForSearch12() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Canarium acutifolium var. acutifolium";
        this.analyser.analyseForSearch(classification);
        assertEquals("Canarium acutifolium var. acutifolium", classification.scientificName);
        assertEquals("Canarium", classification.genus);
        assertEquals("acutifolium", classification.specificEpithet);
        assertEquals(Rank.VARIETY, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    @Test
    public void testAnalyseForSearch13() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Canarium acutifolium acutifolium";
        this.analyser.analyseForSearch(classification);
        assertEquals("Canarium acutifolium acutifolium", classification.scientificName);
        assertEquals("Canarium", classification.genus);
        assertEquals("acutifolium", classification.specificEpithet);
        assertEquals(Rank.INFRASPECIFIC_NAME, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    // Ranks
    @Test
    public void testAnalyseForSearch14() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Melanogaster";
        classification.taxonRank = Rank.SERIES;
        this.analyser.analyseForSearch(classification);
        assertEquals("Melanogaster", classification.scientificName);
        assertEquals(Rank.SERIES, classification.taxonRank);
        assertTrue(classification.getIssues().isEmpty());
    }

    // Author
    @Test
    public void testAnalyseForSearch15() throws Exception {
        AlaLinnaeanClassification classification = new AlaLinnaeanClassification(this.analyser);
        classification.scientificName = "Canarium longiflorum";
        classification.scientificNameAuthorship = "Zipp.";
        this.analyser.analyseForSearch(classification);
        assertEquals("Canarium longiflorum", classification.scientificName);
        assertEquals("Zipp.", classification.scientificNameAuthorship);
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
        logger.info("Names " + names);
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
        logger.info("Names " + names);
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
        logger.info("Names " + names);
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
        logger.info("Names " + names);
        assertEquals(4, names.size());
        assertTrue(names.contains("Goodenia ser. Bracteolatae"));
        assertTrue(names.contains("Goodenia Bracteolatae"));
        assertTrue(names.contains("Goodenia ser. Bracteolatae Benth."));
        assertTrue(names.contains("Bracteolatae"));
    }

    @Test
    public void testAnalyseNames5() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia dealbata Link dealbata");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.SUBSPECIES);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), false);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Acacia dealbata Link dealbata"));
        assertTrue(names.contains("Acacia dealbata subsp. dealbata"));
        assertTrue(names.contains("Acacia dealbata dealbata"));
    }

    @Test
    public void testAnalyseNames6() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia sp. Bigge Island (A.A. Mitchell 3436)");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
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
        logger.info("Names " + names);
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
        logger.info("Names " + names);
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
        logger.info("Names " + names);
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
        logger.info("Names " + names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Goodenia subsect. Bracteolatae"));
        assertTrue(names.contains("Goodenia Bracteolatae"));
        assertTrue(names.contains("Bracteolatae"));
    }

    @Test
    public void testAnalyseNames11() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia 'H.L.White'");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.UNRANKED);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
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
        logger.info("Names " + names);
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
        logger.info("Names " + names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia Div. II Bipinnatae"));
    }

    @Test
    public void testAnalyseNames14() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia sp. laterite");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.UNRANKED);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acacia sp. laterite"));
        assertTrue(names.contains("Acacia laterite"));
    }

    @Test
    public void testAnalyseNames15() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia sp. holey trunk");
        classifier.add(AlaLinnaeanFactory.nomenclaturalCode, NomenclaturalCode.BOTANICAL);
        classifier.add(AlaLinnaeanFactory.taxonRank, Rank.UNRANKED);
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Acacia sp. holey trunk"));
        assertTrue(names.contains("Acacia holey trunk"));
        assertTrue(names.contains("Acacia holey subsp. trunk"));
    }

    @Test
    public void testAnalyseNames16() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Grevillea brachystylis subsp. Busselton (G.J.Keighery s.n. 28/8/1985)");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(3, names.size());
        assertTrue(names.contains("Grevillea brachystylis subsp. Busselton (G.J.Keighery s.n. 28/8/1985)"));
        assertTrue(names.contains("Grevillea brachystylis ssp. Busselton (G.J.Keighery s.n. 28/8/1985)"));
        assertTrue(names.contains("Grevillea brachystylis subsp. Busselton"));
    }

    @Test
    public void testAnalyseNames17() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acianthus sp. 'Gibraltar Range'");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Acianthus sp. 'Gibraltar Range'"));
        assertTrue(names.contains("Acianthus 'Gibraltar Range'"));
    }

    @Test
    public void testAnalyseNames18() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Grevillea sp. aff. patulifolia 'Kanangra'");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Grevillea sp. aff. patulifolia 'Kanangra'"));
        assertTrue(names.contains("Grevillea aff. patulifolia 'Kanangra'"));
    }

    @Test
    public void testAnalyseNames19() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Grevillea sp. nov. 'Belowra'");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Grevillea sp. nov. 'Belowra'"));
    }

    @Test
    public void testAnalyseNames20() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Arthrinium aff. amnium 'Coastal'");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Arthrinium aff. amnium 'Coastal'"));
    }


    @Test
    public void testAnalyseNames21() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Oenochrominae s. str.");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Oenochrominae s. str."));
        assertTrue(names.contains("Oenochrominae"));
    }


    @Test
    public void testAnalyseNames22() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Canarium acutifolium var. acutifolium");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Canarium acutifolium var. acutifolium"));
        assertTrue(names.contains("Canarium acutifolium acutifolium"));
    }

    @Test
    public void testAnalyseNames23() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Phasma (Bacteria) spinosum");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Phasma (Bacteria) spinosum"));
        assertTrue(names.contains("Phasma spinosum"));
    }

    @Test
    public void testAnalyseNames24() throws Exception {
        Classifier classifier = new LuceneClassifier();
        classifier.add(AlaLinnaeanFactory.scientificName, "Acacia subsect. Capitatae-Racemosae");
        Set<String> names = this.analyser.analyseNames(classifier, AlaLinnaeanFactory.scientificName, Optional.empty(), Optional.of(AlaLinnaeanFactory.scientificNameAuthorship), true);
        assertNotNull(names);
        logger.info("Names " + names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia subsect. Capitatae-Racemosae"));
    }

}
