package au.org.ala.bayesian;

import au.org.ala.util.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ParametersTest {

    @Test
    public void testStore1() {
        GrassParameters parameters = new GrassParameters();
        parameters.prior_t$rain = 0.2;
        parameters.inf_t_f$sprinkler = 0.4;
        parameters.inf_t_t$sprinkler = 0.01;
        parameters.inf_t_tt$wet = 0.99;
        parameters.inf_t_tf$wet = 0.8;
        parameters.inf_t_ft$wet = 0.9;
        parameters.inf_t_ff$wet = 0.01;
        parameters.build();
        double[] values = parameters.store();
        assertEquals(7, values.length);
        assertEquals(0.2, values[0], 0.001);
        assertEquals(0.01, values[1], 0.001);
        assertEquals(0.4, values[2], 0.001);
        assertEquals(0.99, values[3], 0.001);
        assertEquals(0.8, values[4], 0.001);
        assertEquals(0.9, values[5], 0.001);
        assertEquals(0.01, values[6], 0.001);
    }

    @Test
    public void testStoreAsBytes1() throws Exception {
        GrassParameters parameters = new GrassParameters();
        parameters.prior_t$rain = 0.2;
        parameters.inf_t_f$sprinkler = 0.4;
        parameters.inf_t_t$sprinkler = 0.01;
        parameters.inf_t_tt$wet = 0.99;
        parameters.inf_t_tf$wet = 0.8;
        parameters.inf_t_ft$wet = 0.9;
        parameters.inf_t_ff$wet = 0.01;
        parameters.build();
        byte[] values = parameters.storeAsBytes();
        //FileOutputStream os = new FileOutputStream("/Users/pal155/tmp/params-1.bin");
        //os.write(values);
        //os.close();
        assertEquals(66, values.length);
        assertArrayEquals(TestUtils.getResourceBytes(ParametersTest.class, "params-1.bin"), values);
    }

    @Test
    public void testLoad1() {
        GrassParameters parameters = new GrassParameters();
        double[] values = new double[] { 0.2, 0.01, 0.4, 0.99, 0.8, 0.9, 0.01 };
        parameters.load(values);
        parameters.build();
        assertEquals(0.2, parameters.prior_t$rain, 0.001);
        assertEquals(0.4, parameters.inf_t_f$sprinkler, 0.001);
        assertEquals(0.01, parameters.inf_t_t$sprinkler, 0.001);
        assertEquals(0.99, parameters.inf_t_tt$wet, 0.001);
        assertEquals(0.8, parameters.inf_t_tf$wet, 0.001);
        assertEquals(0.9, parameters.inf_t_ft$wet, 0.001);
        assertEquals(0.01, parameters.inf_t_ff$wet, 0.001);
    }


    @Test
    public void testLoadFromStore1() throws Exception {
        GrassParameters parameters = new GrassParameters();
        parameters.loadFromBytes(TestUtils.getResourceBytes(ParametersTest.class, "params-1.bin"));
        parameters.build();
        assertEquals(0.2, parameters.prior_t$rain, 0.001);
        assertEquals(0.4, parameters.inf_t_f$sprinkler, 0.001);
        assertEquals(0.01, parameters.inf_t_t$sprinkler, 0.001);
        assertEquals(0.99, parameters.inf_t_tt$wet, 0.001);
        assertEquals(0.8, parameters.inf_t_tf$wet, 0.001);
        assertEquals(0.9, parameters.inf_t_ft$wet, 0.001);
        assertEquals(0.01, parameters.inf_t_ff$wet, 0.001);
    }

    // A test class for the parameters load/store
    public static class GrassParameters implements Parameters {
        public double prior_t$rain; // rain prior probability
        public double prior_f$rain; // 1 - rain prior probability
        public double inf_t_t$sprinkler; // p(sprinkler | rain) conditional probability
        public double inf_f_t$sprinkler; // p(¬sprinkler | rain) =  1 - p(sprinkler | rain) conditional probability
        public double inf_t_f$sprinkler; // p(sprinkler | ¬rain) conditional probability
        public double inf_f_f$sprinkler; // p(¬sprinkler | ¬rain) =  1 - p(sprinkler | ¬rain) conditional probability
        public double inf_t_tt$wet; // p(wet | rain, sprinkler) conditional probability
        public double inf_f_tt$wet; // p(¬wet | rain, sprinkler) =  1 - p(wet | rain, sprinkler) conditional probability
        public double inf_t_tf$wet; // p(wet | rain, ¬sprinkler) conditional probability
        public double inf_f_tf$wet; // p(¬wet | rain, ¬sprinkler) =  1 - p(wet | rain, ¬sprinkler) conditional probability
        public double inf_t_ft$wet; // p(wet | ¬rain, sprinkler) conditional probability
        public double inf_f_ft$wet; // p(¬wet | ¬rain, sprinkler) =  1 - p(wet | ¬rain, sprinkler) conditional probability
        public double inf_t_ff$wet; // p(wet | ¬rain, ¬sprinkler) conditional probability
        public double inf_f_ff$wet; // p(¬wet | ¬rain, ¬sprinkler) =  1 - p(wet | ¬rain, ¬sprinkler) conditional probability
        public double derived_t_tt$wet; // p(wet | sprinkler, rain) = p(wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
        public double derived_f_tt$wet; // p(¬wet | sprinkler, rain) = p(¬wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
        public double derived_t_tf$wet; // p(wet | sprinkler, ¬rain) = p(wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
        public double derived_f_tf$wet; // p(¬wet | sprinkler, ¬rain) = p(¬wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
        public double derived_t_ft$wet; // p(wet | ¬sprinkler, rain) = p(wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
        public double derived_f_ft$wet; // p(¬wet | ¬sprinkler, rain) = p(¬wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
        public double derived_t_ff$wet; // p(wet | ¬sprinkler, ¬rain) = p(wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability
        public double derived_f_ff$wet; // p(¬wet | ¬sprinkler, ¬rain) = p(¬wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability

        public GrassParameters() {
        }

        @Override
        public void load(double[] vector) {
            this.prior_t$rain = vector[0];
            this.inf_t_t$sprinkler = vector[1];
            this.inf_t_f$sprinkler = vector[2];
            this.inf_t_tt$wet = vector[3];
            this.inf_t_tf$wet = vector[4];
            this.inf_t_ft$wet = vector[5];
            this.inf_t_ff$wet = vector[6];
            this.build();
        }

        @Override
        public double[] store() {
            double[] vector = new double[7];

            vector[0] = this.prior_t$rain;
            vector[1] = this.inf_t_t$sprinkler;
            vector[2] = this.inf_t_f$sprinkler;
            vector[3] = this.inf_t_tt$wet;
            vector[4] = this.inf_t_tf$wet;
            vector[5] = this.inf_t_ft$wet;
            vector[6] = this.inf_t_ff$wet;
            return vector;
        }

        public void build() {
            this.prior_f$rain = 1.0 - this.prior_t$rain;
            this.inf_f_t$sprinkler = 1.0 - inf_t_t$sprinkler;
            this.inf_f_f$sprinkler = 1.0 - inf_t_f$sprinkler;
            this.inf_f_tt$wet = 1.0 - inf_t_tt$wet;
            this.inf_f_tf$wet = 1.0 - inf_t_tf$wet;
            this.inf_f_ft$wet = 1.0 - inf_t_ft$wet;
            this.inf_f_ff$wet = 1.0 - inf_t_ff$wet;
            this.derived_t_tt$wet = inf_t_tt$wet * inf_t_t$sprinkler;
            this.derived_f_tt$wet = inf_f_tt$wet * inf_t_t$sprinkler;
            this.derived_t_tf$wet = inf_t_ft$wet * inf_t_f$sprinkler;
            this.derived_f_tf$wet = inf_f_ft$wet * inf_t_f$sprinkler;
            this.derived_t_ft$wet = inf_t_tf$wet * inf_f_t$sprinkler;
            this.derived_f_ft$wet = inf_f_tf$wet * inf_f_t$sprinkler;
            this.derived_t_ff$wet = inf_t_ff$wet * inf_f_f$sprinkler;
            this.derived_f_ff$wet = inf_f_ff$wet * inf_f_f$sprinkler;
        }

    }
}
