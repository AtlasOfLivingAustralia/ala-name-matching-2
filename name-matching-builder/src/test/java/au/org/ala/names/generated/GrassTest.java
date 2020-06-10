package au.org.ala.names.generated;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GrassTest {
    private double[] PARAMS = {
            0.2, // Prior rain
            0.01, // Sprinkler if rain
            0.4, // Sprinkler if !rain
            0.99, // Wet if rain and sprinkler
            0.8, // Wet if rain and !sprinkler
            0.9, // Wet if !rain and sprinkler
            0.01 // Wet if !rain and !sprinkler
    };

    private GrassInference inference;

    @Before
    public void setUp() {
        GrassParameters parameters = new GrassParameters();
        parameters.load(PARAMS);
        this.inference = new GrassInference();
        this.inference.parameters = parameters;
    }

    /**
     * p(R = T | S = ?, W  = ?) = p(R = T)
     */
    @Test
    public void testNoneNone() {
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        assertEquals(0.2, this.inference.probability(evidence), 0.0001);
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
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        evidence.e$sprinkler = false;
        assertEquals(0.2920, this.inference.probability(evidence), 0.0001);
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
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        evidence.e$sprinkler = true;
        assertEquals(0.0062, this.inference.probability(evidence), 0.0001);
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
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        evidence.e$wet = false;
        assertEquals(0.0724, this.inference.probability(evidence), 0.0001);
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
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        evidence.e$wet = true;
        assertEquals(0.3539, this.inference.probability(evidence), 0.0001);
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
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        evidence.e$wet = true;
        evidence.e$sprinkler = false;
        assertEquals(0.9706, this.inference.probability(evidence), 0.0001);
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
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        evidence.e$wet = false;
        evidence.e$sprinkler = true;
        assertEquals(0.0006, this.inference.probability(evidence), 0.0001);
    }

    /**
     * n = p(W = T, S = T, R = T)
     * = p(W = T | S = T, R = T) . p(S = T | R = T) . P(R = T)
     * = 0.99 x 0.01 x 0.2 = 0.0020
     * d = p(W = T, S = T, R = T) + p(W = T, S = T, R = F)
     * =  p(W = T | S = T, R = T) . p(S = T | R = T) . P(R = T) + p(W = T | S = T, R = F) . p(S = T | R = F) . P(R = F)
     * = 0.99 x 0.01 x 0.2 + 0.9 x 0.4 x 0.8
     * = 0.2900
     * p = 0.000625
     */
    @Test
    public void testSprinklerWet() {
        GrassInference.Evidence evidence = new GrassInference.Evidence();

        evidence.e$wet = true;
        evidence.e$sprinkler = true;
        assertEquals(0.0069, this.inference.probability(evidence), 0.0001);
    }
}
