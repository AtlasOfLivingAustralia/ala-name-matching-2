package au.org.ala.bayesian;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InferenceTest {
    @Test
    public void testAnd1() {
        Inference p1 = Inference.forPEC(0.1, 0.11, 0.9);
        Inference p2 = Inference.forPEC(0.2, 0.1, 0.4);
        Inference and = p1.and(p2);
        assertEquals(0.65455, and.getPosterior(), 0.0001);
        assertEquals(0.01100, and.getEvidence(), 0.0001);
        assertEquals(0.36000, and.getConditional(), 0.0001);
        assertEquals(0.02000, and.getPrior(), 0.0001);
        and = p2.and(p1);
        assertEquals(0.65455, and.getPosterior(), 0.0001);
        assertEquals(0.01100, and.getEvidence(), 0.0001);
        assertEquals(0.36000, and.getConditional(), 0.0001);
        assertEquals(0.02000, and.getPrior(), 0.0001);
    }

    @Test
    public void testOr1() {
        Inference p1 = Inference.forPEC(0.1, 0.11, 0.9);
        Inference p2 = Inference.forPEC(0.2, 0.1, 0.4);
        Inference or = p1.or(p2);
        assertEquals(0.96364, or.getPosterior(), 0.0001);
        assertEquals(0.19900, or.getEvidence(), 0.0001);
        assertEquals(0.94000, or.getConditional(), 0.0001);
        assertEquals(0.20400, or.getPrior(), 0.0001);
        or = p2.or(p1);
        assertEquals(0.96364, or.getPosterior(), 0.0001);
        assertEquals(0.19900, or.getEvidence(), 0.0001);
        assertEquals(0.94000, or.getConditional(), 0.0001);
        assertEquals(0.20400, or.getPrior(), 0.0001);
    }

}
