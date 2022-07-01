package au.org.ala.names.lucene;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.BayesianTerm;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.gbif.dwc.terms.DwcTerm;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class LuceneClassifierTest {
    private LuceneUtils utils;

    @After
    public void tearDown() throws Exception {
        if (this.utils != null)
            this.utils.close();
    }

    @Test
    public void testMakeDocumentCopy1() throws Exception {
        String name = "Acacia dealbata";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        Document copy = classifier.makeDocumentCopy();
        assertEquals(2, copy.getFields().size());
        assertEquals(name, copy.get(TestFactory.SCIENTIFIC_NAME.getExternal(ExternalContext.LUCENE)));
        assertEquals(name, copy.get(TestFactory.SCIENTIFIC_NAME.getExternal(ExternalContext.LUCENE_VARIANT)));
    }

    @Test
    public void testMakeDocumentCopy2() throws Exception {
        String name = "Insecta";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.CLASS_, name, false, false);
        Document copy = classifier.makeDocumentCopy();
        assertEquals(1, copy.getFields().size());
        assertEquals(name, copy.get(TestFactory.CLASS_.getExternal(ExternalContext.LUCENE)));
    }

    @Test
    public void testMakeDocumentCopy3() throws Exception {
        Integer id = 2000;
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.RANK_ID, id, false, false);
        Document copy = classifier.makeDocumentCopy();
        assertEquals(2, copy.getFields().size());
        IndexableField[] fields = copy.getFields(TestFactory.RANK_ID.getExternal(ExternalContext.LUCENE));
        assertEquals(2, fields.length);
        assertEquals(id, fields[0].numericValue());
        assertEquals(id, fields[1].numericValue());
    }

    @Test
    public void testMakeDocumentCopy4() throws Exception {
        Integer id = 100;
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.RANK_RANGE, id, false, false);
        Document copy = classifier.makeDocumentCopy();
        assertEquals(2, copy.getFields().size());
        IndexableField[] fields = copy.getFields(TestFactory.RANK_RANGE.getExternal(ExternalContext.LUCENE));
        assertEquals(2, fields.length);
        assertEquals(StoredField.class, fields[0].getClass());
        assertEquals(id, fields[0].numericValue());
        assertEquals(IntPoint.class, fields[1].getClass());
        assertEquals(id, fields[1].numericValue());
    }

    @Test
    public void testMakeDocumentCopy5() throws Exception {
        TestEnum id = TestEnum.BAR;
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.TEST_ENUM, id, false, false);
        Document copy = classifier.makeDocumentCopy();
        assertEquals(1, copy.getFields().size());
        assertEquals(id.name().toLowerCase(), copy.get(TestFactory.TEST_ENUM.getExternal(ExternalContext.LUCENE)));
    }


    @Test
    public void testMakeDocumentCopy6() throws Exception {
        this.utils = new LuceneUtils(TestFactory.OBSERVABLES);
        Integer id = 2000;
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.RANK_ID, id, false, false);
        String docId = this.utils.add(classifier.getDocument());
        Document stored = this.utils.get(docId);
        LuceneClassifier classifier1 = new LuceneClassifier(stored);
        Document copy = classifier1.makeDocumentCopy();
        assertEquals(3, copy.getFields().size());
        IndexableField[] fields = copy.getFields(TestFactory.RANK_ID.getExternal(ExternalContext.LUCENE));
        assertEquals(2, fields.length);
        assertEquals(StoredField.class, fields[0].getClass());
        assertEquals(id, fields[0].numericValue());
        assertEquals(IntPoint.class, fields[1].getClass());
        assertEquals(id, fields[1].numericValue());
    }


    @Test
    public void testMakeDocumentCopy7() throws Exception {
        this.utils = new LuceneUtils(TestFactory.OBSERVABLES);
        String name = "Eucaltypus regnans";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        String docId = this.utils.add(classifier.getDocument());
        Document stored = this.utils.get(docId);
        LuceneClassifier classifier1 = new LuceneClassifier(stored);
        Document copy = classifier1.makeDocumentCopy();
        assertEquals(3, copy.getFields().size());
        IndexableField[] fields = copy.getFields(TestFactory.SCIENTIFIC_NAME.getExternal(ExternalContext.LUCENE));
        assertEquals(1, fields.length);
        assertEquals(StoredField.class, fields[0].getClass());
        assertEquals(name, fields[0].stringValue());
        fields = copy.getFields(TestFactory.SCIENTIFIC_NAME.getExternal(ExternalContext.LUCENE_VARIANT));
        assertEquals(1, fields.length);
        assertEquals(TextField.class, fields[0].getClass());
        assertEquals(name, fields[0].stringValue());
    }

    @Test
    public void testHas1() throws Exception {
        String name = "Eucaltypus regnans";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        assertTrue(classifier.has(TestFactory.SCIENTIFIC_NAME));
    }

    @Test
    public void testMatch1() throws Exception {
        String name = "Eucaltypus regnans";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        assertEquals(true, classifier.match(name, TestFactory.SCIENTIFIC_NAME));
    }

    @Test
    public void testMatch2() throws Exception {
        String name = "Eucaltypus regnans";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        assertEquals(false, classifier.match(name + "x", TestFactory.SCIENTIFIC_NAME));
    }

    @Test
    public void testMatch3() throws Exception {
        String name = "Eucaltypus regnans";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        assertEquals(null, classifier.match(name, TestFactory.CLASS_));
    }

    @Test
    public void add1() throws Exception {
        String name = "Insecta";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.CLASS_, name, false, false);
        try {
            classifier.add(TestFactory.CLASS_, name, false, false);
            fail("Can't add value twice");
        } catch (StoreException ex) {
        }
    }

    @Test
    public void add2() throws Exception {
        String name = "Insecta";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        classifier.add(TestFactory.SCIENTIFIC_NAME, name, false, false);
        assertEquals(true, classifier.match(name, TestFactory.SCIENTIFIC_NAME));
    }

    @Test
    public void add3() throws Exception {
        String name1 = "Insecta";
        String name2 = "Insects";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name1, false, false);
        classifier.add(TestFactory.SCIENTIFIC_NAME, name2, false, false);
        assertEquals(true, classifier.match(name1, TestFactory.SCIENTIFIC_NAME));
        assertEquals(true, classifier.match(name2, TestFactory.SCIENTIFIC_NAME));
        TestClassification classification = new TestClassification();
        classification.read(classifier, false);
        assertEquals(name1, classification.scientificName);
    }

    @Test
    public void addAll() throws Exception {
    }

    @Test
    public void testReplace1() throws Exception {
        String name1 = "Insecta";
        String name2 = "Insects";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.CLASS_, name1, false, false);
        classifier.clear(TestFactory.CLASS_);
        classifier.add(TestFactory.CLASS_, name2, false, false);
        TestClassification classification = new TestClassification();
        classification.read(classifier, false);
        assertEquals(name2, classification.class_);
    }

    @Test
    public void testGet1() throws Exception {
        String name1 = "Insecta";
        String name2 = "Insects";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name1, false, false);
        classifier.add(TestFactory.SCIENTIFIC_NAME, name2, false, false);
        assertEquals(name1, classifier.get(TestFactory.SCIENTIFIC_NAME));
    }

    @Test
    public void testGetAll1() throws Exception {
        String name1 = "Insecta";
        String name2 = "Insects";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.add(TestFactory.SCIENTIFIC_NAME, name1, false, false);
        classifier.add(TestFactory.SCIENTIFIC_NAME, name2, false, false);
        Set<String> names = classifier.getAll(TestFactory.SCIENTIFIC_NAME);
        assertEquals(2, names.size());
        assertTrue(names.contains(name1));
        assertTrue(names.contains(name2));
    }


    @Test
    public void testIdentify1() throws Exception {
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.identify();
        assertNotNull(classifier.getIdentifier());
    }

    @Test
    public void testType1() throws Exception {
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.setType(DwcTerm.Occurrence);
        assertEquals(DwcTerm.Occurrence, classifier.getType());

    }

    @Test
    public void testAnnotate1() throws Exception {
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.annotate(BayesianTerm.isRoot);
        classifier.annotate(BayesianTerm.isSynonym);
        assertTrue(classifier.hasAnnotation(BayesianTerm.isRoot));
        assertTrue(classifier.hasAnnotation(BayesianTerm.isSynonym));
        assertFalse(classifier.hasAnnotation(BayesianTerm.invalidMatch));
    }

    @Test
    public void testParameters() throws Exception {
        LuceneClassifier classifier = new LuceneClassifier();
        ParametersTest.GrassParameters parameters1 = new ParametersTest.GrassParameters();
        double[] values = new double[] { 0.2, 0.01, 0.4, 0.99, 0.8, 0.9, 0.01 };
        parameters1.load(values);
        parameters1.build();
        classifier.storeParameters(parameters1);
        ParametersTest.GrassParameters parameters2 = new ParametersTest.GrassParameters();
        classifier.loadParameters(parameters2);
        assertEquals(0.2, parameters2.prior_t$rain, 0.001);
        assertEquals(0.4, parameters2.inf_t_f$sprinkler, 0.001);
        assertEquals(0.01, parameters2.inf_t_t$sprinkler, 0.001);
        assertEquals(0.99, parameters2.inf_t_tt$wet, 0.001);
        assertEquals(0.8, parameters2.inf_t_tf$wet, 0.001);
        assertEquals(0.9, parameters2.inf_t_ft$wet, 0.001);
        assertEquals(0.01, parameters2.inf_t_ff$wet, 0.001);
    }

    @Test
    public void testIndex1() throws Exception {
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.setIndex(100, 150);
        int[] lr = classifier.getIndex();
        assertEquals(100, lr[0]);
        assertEquals(150, lr[1]);
    }

    @Test
    public void testNames1() throws Exception {
        String name1 = "Osphranter rufus";
        String name2 = "Macropus rufus";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.setNames(Arrays.asList(name1, name2));
        assertEquals(2, classifier.getNames().size());
        assertTrue(classifier.getNames().contains(name1));
        assertTrue(classifier.getNames().contains(name2));
    }

    @Test
    public void testSignature1() throws Exception {
        String signature = "TT";
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.setSignature(signature);
        assertEquals(signature, classifier.getSignature());

    }

    @Test
    public void testTrail1() throws Exception {
        String id1 = "ID1";
        String id2 = "ID2";
        List<String> trail = Arrays.asList(id1, id2);
        LuceneClassifier classifier = new LuceneClassifier();
        classifier.setTrail(trail);
        assertEquals(trail, classifier.getTrail());
    }

    @Test
    public void testLuceneMetadata1() throws Exception {
        assertNotNull(LuceneClassifier.LUCENE_METADATA);
        assertEquals("Lucene", LuceneClassifier.LUCENE_METADATA.getFormat());
    }
}