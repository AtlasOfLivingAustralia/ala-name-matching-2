package au.org.ala.bayesian.fidelity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompositeFidelityTest {

    @Test
    public void testGetFidelity1() {
        CompositeFidelity<String> fidelity = new CompositeFidelity<>("A", "B");
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

    @Test
    public void testFidelity2() {
        CompositeFidelity<String> fidelity = new CompositeFidelity<>("A", "B");
        fidelity.add(new SimpleFidelity<>(10, 10, 1.0));
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }

    @Test
    public void testFidelity3() {
        CompositeFidelity<String> fidelity = new CompositeFidelity<>("A", "B");
        fidelity.add(new SimpleFidelity<>(10, 10, 1.0));
        fidelity.add(new SimpleFidelity<>("A", "B", 0.0));
        assertEquals(0.5, fidelity.getFidelity(), 0.00001);
    }

    @Test
    public void testFidelity4() {
        CompositeFidelity<String> fidelity = new CompositeFidelity<>("A", "B");
        fidelity.add(new SimpleFidelity<>(10, 10, 1.0));
        fidelity.add(new SimpleFidelity<>("A", "B", 0.0));
        fidelity.add(new SimpleFidelity<>("Aa", "Ab", 0.5));
        assertEquals(0.5, fidelity.getFidelity(), 0.00001);
    }

    @Test
    public void testFidelity5() {
        CompositeFidelity<String> fidelity = new CompositeFidelity<>("A", "B");
        fidelity.add(new SimpleFidelity<>(10, 10, 1.0));
        fidelity.add(new SimpleFidelity<>("A", "B", 0.0));
        fidelity.add(new SimpleFidelity<>("Aa", "Ab", 0.5));
        fidelity.add(new SimpleFidelity<>("Aaa", "Aab", 0.66667));
        assertEquals(0.54167, fidelity.getFidelity(), 0.00001);
    }

    @Test
    public void testAdd1() {
        CompositeFidelity<String> fidelity = new CompositeFidelity<>("A", "B");
        fidelity.add(null);
        assertTrue(fidelity.getComponents().isEmpty());
        assertEquals(0.0, fidelity.getFidelity(), 0.00001);
    }

    @Test
    public void testAdd2() {
        CompositeFidelity<String> fidelity = new CompositeFidelity<>("A", "B");
        fidelity.add(new SimpleFidelity<>(10, 10, 1.0));
        assertEquals(1, fidelity.getComponents().size());
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }
}