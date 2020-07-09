package au.org.ala.util;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.ParametersTest;
import au.org.ala.bayesian.StoreException;
import au.org.ala.vocab.ALATerm;
import org.gbif.dwc.terms.DwcTerm;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class SimpleClassifierTest {
    private static final Observable SN = new Observable(DwcTerm.scientificName);
    private static final Observable RANK = new Observable(DwcTerm.taxonRank);

    @Test
    public void testGet1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(SN, "Acacia");
        classifier.add(RANK, "genus");
        assertEquals("Acacia", classifier.get(SN));
        assertEquals("genus", classifier.get(RANK));
    }

    @Test
    public void testGetAll1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(SN, "Acacia");
        Collection<String> names = classifier.getAll(SN);
        assertNotNull(names);
        assertEquals(1, names.size());
        assertTrue(names.contains("Acacia"));
    }

    @Test
    public void testAdd1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(SN, "Acacia");
        assertEquals("Acacia", classifier.get(SN));
    }

    @Test
    public void testAdd2() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(SN, "Acacia");
        try {
            classifier.add(SN, "Akacia");
            fail("Expecting store exception");
        } catch (StoreException ex) {
        }
    }

    @Test
    public void testAddAll1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(SN, "Acacia");
        classifier.add(RANK, "genus");
        SimpleClassifier classifier2 = new SimpleClassifier();
        classifier2.addAll(SN, classifier);
        assertEquals("Acacia", classifier2.get(SN));
        assertNull(classifier2.get(RANK));
    }

    @Test
    public void testReplace1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.add(SN, "Acacia");
        classifier.replace(SN, "Eucalyptus");
        assertEquals("Eucalyptus", classifier.get(SN));
    }

    @Test
    public void testIdentify1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        String id = classifier.identify();
        assertNotNull(id);
        assertEquals(id, classifier.getIdentifier());
    }

    @Test
    public void testType1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.setType(DwcTerm.Taxon);
        assertEquals(DwcTerm.Taxon, classifier.getType());
    }

    @Test
    public void testAnnotate1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.annotate(ALATerm.isRoot);
        assertTrue(classifier.hasAnnotation(ALATerm.isRoot));
        assertFalse(classifier.hasAnnotation(ALATerm.isSynonym));
    }

    @Test
    public void testParameters1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        ParametersTest.GrassParameters parameters = new ParametersTest.GrassParameters();
        parameters.prior_t$rain = 1.0;
        classifier.storeParameters(parameters);
        ParametersTest.GrassParameters parameters2 = new ParametersTest.GrassParameters();
        classifier.loadParameters(parameters2);
        assertEquals(1.0, parameters2.prior_t$rain, 0.00001);
    }

    @Test
    public void testIndex1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.setIndex(1, 2);
        int[] indexes = classifier.getIndex();
        assertNotNull(indexes);
        assertEquals(2, indexes.length);
        assertEquals(1, indexes[0]);
        assertEquals(2, indexes[1]);
    }

    @Test
    public void testNames1() throws Exception {
        SimpleClassifier classifier = new SimpleClassifier();
        classifier.setNames(Arrays.asList("Bill", "Ben"));
        Collection<String> names = classifier.getNames();
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Bill"));
        assertTrue(names.contains("Ben"));
    }

}
