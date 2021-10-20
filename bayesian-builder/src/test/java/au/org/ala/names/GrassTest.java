package au.org.ala.names;

import au.org.ala.bayesian.Inference;
import au.org.ala.names.generated.GrassInferencer;
import au.org.ala.names.generated.GrassInferencer_;
import au.org.ala.names.generated.GrassParameters_;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Note these tests are for the hypothesis rain = true.
 */
public class GrassTest {
    private double[] PARAMS = {
            0.2, // Prior rain
            0.01, // Sprinkler if rain
            0.4, // Sprinkler if !rain
            0.99, // Wet if rain and sprinkler
            0.8, // Wet if rain and !sprinkler
            0.9, // Wet if !rain and sprinkler
            0.01 // Wet if !rain and !sprinkler (!rain)
    };

    private GrassParameters_ parameters;
    private GrassInferencer_ inferencer;

    @Before
    public void setUp() {
        this.parameters = new GrassParameters_();
        parameters.load(PARAMS);
        this.inferencer = new GrassInferencer_();
    }

    /**
     * p(R = T | S = ?, W  = ?) = p(R = T)
     */
    @Test
    public void testNoneNone() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(1.0, inference.getEvidence(), 0.0001);
        assertEquals(0.2, inference.getPosterior(), 0.0001);
    }

    /**
     * n = p(W = T, S = F, R = T) + p(W = F, S = F, R = T)
     * = p(W = T | S = F, R = T) p(S = F | R = T) p(R = T) + p(W = F | S = F, R = T) p(S = F | R = T) p(R = T)
     * = 0.8 x 0.99 x 0.2 + 0.2 x 0.99 x 0.2 = 0.1980
     * d = n + p(W = T, S = F, R = F) + p(W = F, S = F, R = F)
     * = n + p(W = T | S = F, R = F) p(S = F | R = F) p(R = F) + p(W = F | S = F, R = F) p(S = F | R = F) p(R = F)
     * = 0.1980 + 0.0 x 0.6 x 0.8 + 1.0 x 0.6 x 0.8 = 0.6780
     * n / d = 2920
     */
    @Test
    public void testNoSprinklerNone() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        evidence.e$sprinkler = false;
        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(0.6780, inference.getEvidence(), 0.0001);
        assertEquals(0.2920, inference.getPosterior(), 0.0001);
    }

    /**
     * n = p(W = T, S = T, R = T) + p(W = F, S = T, R = T)
     * = p(W = T | S = T, R = T) p(S = T | R = T) p(R = T) + p(W = F | S = T, R = T) p(S = T | R = T) p(R = T)
     * = 0.99 x 0.01 x 0.2 + 0.01 x 0.01 x 0.2 = 0.0020
     * d = n + p(W = T, S = T, R = F) + p(W = F, S = T, R = F)
     * = n + p(W = T | S = T, R = F) p(S = T | R = F) p(R = F) + p(W = F | S = T, R = F) p(S = T | R = F) p(R = F)
     * = 0.0020 + 0.9 x 0.4 x 0.8 + 0.1 x 0.4 x 0.8 = 0.3220
     * n / d = 0.0062
     */
    @Test
    public void testSprinklerNone() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        evidence.e$sprinkler = true;
        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(0.3220, inference.getEvidence(), 0.0001);
        assertEquals(0.0062, inference.getPosterior(), 0.0001);
    }

    /**
     * n = p(W = F, S = T, R = T) + p(W = F, S = F, R = T)
     * = p(W = F | S = T, R = T) p(S = T | R = T) p(R = T) + p(W = F | S = F, R = T) p(S = F | R = T) p(R = T)
     * = 0.01 x 0.01 x 0.2 + 0.2 x 0.99 x 0.2 = 0.0396
     * d = n + p(W = F, S = T, R = F) + p(W = F, S = F, R = F)
     * = n + p(W = F | S = T, R = F) p(S = T | R = F) p(R = F) + p(W = F | S = F, R = F) p(S = F | R = F) p(R = F)
     * = 0.0396 + 0.1 * 0.4 * 0.8 + 0.99 * 0.6 * 0.8 = 0.0396 + 0.5072 =  0.5468
     * n / d = 0.0724
     */
    @Test
    public void testNoneNoWet() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        evidence.e$wet = false;
        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(0.5468, inference.getEvidence(), 0.0001);
        assertEquals(0.0724, inference.getPosterior(), 0.0001);
    }

    /**
     * n = p(W = T, S = F, R = T) + p(W = T, S = T, R = T)
     * = p(W = T | S = F, R = T) . p(S =F | R = T) . P(R = T) + p(W = T | S = T, R = T) . p(S = T | R = T) . P(R = T)
     * = 0.8 x 0.99 x 0.2 + 0.99 x 0.01 x 0.2 = 0.1604
     * d = p(W = T, S = F, R = T) + p(W = T, S = T, R = T) + p(W = T, S = F, R = F) + p(W = T, S = T, R = F)
     * = n + p(W = T | S = F, R = F) . p(S = F | R = F) . P(R = F) + p(W = T | S = T, R = F) . p(S = T | R = F) . P(R = F)
     * = 0.1604 + 0.01 x 0.6 x 0.8 + 0.9 x 0.4 x 0.8 = 0.1604 + 0.2928 = 0.4532
     * p = n / d = 0.3539
     */
    @Test
    public void testNoneWet() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        evidence.e$wet = true;
        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(0.4532, inference.getEvidence(), 0.0001);
        assertEquals(0.3539, inference.getPosterior(), 0.0001);
    }

    /**
     * n = p(W = T, S = F, R = T)
     * = p(W = T | S = F, R = T) . p(S = F | R = T) . P(R = T)
     * = 0.8 x 0.99 x 0.2 = 0.1584
     * d = p(W = T, S = F, R = T) + p(W = T, S = F, R = F)
     * =  p(W = T | S = F, R = T) . p(S = F | R = T) . P(R = T) + p(W = T | S = F, R = F) . p(S = F | R = F) . P(R = F)
     * = 0.8 x 0.99 x 0.2 + 0.01 x 0.6 x 0.8 = 0.1584 + 0.048 =  0.1632
     * p = 0.9706
     */
    @Test
    public void testNoSprinklerWet() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        evidence.e$wet = true;
        evidence.e$sprinkler = false;
        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(0.1632, inference.getEvidence(), 0.0001);
        assertEquals(0.9706, inference.getPosterior(), 0.0001);
    }

    /**
     * n = p(W = F, S = T, R = T)
     * = p(W = F | S = T, R = T) . p(S = T | R = T) . P(R = T)
     * = 0.01 x 0.01 x 0.2 = 0.00002
     * d = p(W = F, S = T, R = T) + p(W = F, S = T, R = F)
     * =  p(W = F | S = T, R = T) . p(S = T | R = T) . P(R = T) + p(W = F | S = T, R = F) . p(S = T | R = F) . P(R = F)
     * = 0.01 x 0.01 x 0.2 + 0.1 x 0.4 x 0.8
     * = 0.03202
     * p = 0.000625
     */
    @Test
    public void testSprinklerNoWet() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        evidence.e$wet = false;
        evidence.e$sprinkler = true;
        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(0.0320, inference.getEvidence(), 0.0001);
        assertEquals(0.0006, inference.getPosterior(), 0.0001);
    }

    /**
     * n = p(W = T, S = T, R = T)
     * = p(W = T | S = T, R = T) . p(S = T | R = T) . p(R = T)
     * = 0.99 x 0.01 x 0.2 = 0.0020
     * d = p(W = T, S = T, R = T) + p(W = T, S = T, R = F)
     * =  p(W = T | S = T, R = T) . p(S = T | R = T) . P(R = T) + p(W = T | S = T, R = F) . p(S = T | R = F) . P(R = F)
     * = 0.99 x 0.01 x 0.2 + 0.9 x 0.4 x 0.8
     * = 0.2900
     * p = 0.00682
     *
     * Nopte low probability is rain is true if the sprinkler is true (unlikely)
     */
    @Test
    public void testSprinklerWet() {
        GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();

        evidence.e$wet = true;
        evidence.e$sprinkler = true;
        Inference inference = this.inferencer.probability(evidence, this.parameters);
        assertEquals(0.2900, inference.getEvidence(), 0.0001);
        assertEquals(0.0068, inference.getPosterior(), 0.0001);
    }
}
