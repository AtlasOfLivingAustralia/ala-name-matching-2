package au.org.ala.bayesian.fidelity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleFidelityTest {

    @Test
    public void testGetFidelity1() {
        SimpleFidelity<String> fidelity = new SimpleFidelity<>("A", "A", 1.0);
        assertEquals(1.0, fidelity.getFidelity(), 0.00001);
    }
}