package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic = true)
public class GrassParameters_ implements Parameters {

  public final static String SIGNATURE = "";

  public double prior_rain_t; // rain prior probability
  public double prior_rain_f; // 1 - rain prior probability
  public double inf_sprinkler_t$t; // p(sprinkler | rain) conditional probability
  public double inf_sprinkler_f$t; // p(¬sprinkler | rain) =  1 - p(sprinkler | rain) conditional probability
  public double inf_sprinkler_t$f; // p(sprinkler | ¬rain) conditional probability
  public double inf_sprinkler_f$f; // p(¬sprinkler | ¬rain) =  1 - p(sprinkler | ¬rain) conditional probability
  public double inf_wet_t$t_t; // p(wet | rain, sprinkler) conditional probability
  public double inf_wet_f$t_t; // p(¬wet | rain, sprinkler) =  1 - p(wet | rain, sprinkler) conditional probability
  public double inf_wet_t$t_f; // p(wet | rain, ¬sprinkler) conditional probability
  public double inf_wet_f$t_f; // p(¬wet | rain, ¬sprinkler) =  1 - p(wet | rain, ¬sprinkler) conditional probability
  public double inf_wet_t$f_t; // p(wet | ¬rain, sprinkler) conditional probability
  public double inf_wet_f$f_t; // p(¬wet | ¬rain, sprinkler) =  1 - p(wet | ¬rain, sprinkler) conditional probability
  public double inf_wet_t$f_f; // p(wet | ¬rain, ¬sprinkler) conditional probability
  public double inf_wet_f$f_f; // p(¬wet | ¬rain, ¬sprinkler) =  1 - p(wet | ¬rain, ¬sprinkler) conditional probability
  public double derived_wet_t$t_t; // p(wet | rain, sprinkler) = p(wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
  public double derived_wet_f$t_t; // p(¬wet | rain, sprinkler) = p(¬wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
  public double derived_wet_t$t_f; // p(wet | rain, ¬sprinkler) = p(wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
  public double derived_wet_f$t_f; // p(¬wet | rain, ¬sprinkler) = p(¬wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
  public double derived_wet_t$f_t; // p(wet | ¬rain, sprinkler) = p(wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
  public double derived_wet_f$f_t; // p(¬wet | ¬rain, sprinkler) = p(¬wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
  public double derived_wet_t$f_f; // p(wet | ¬rain, ¬sprinkler) = p(wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability
  public double derived_wet_f$f_f; // p(¬wet | ¬rain, ¬sprinkler) = p(¬wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability

  public GrassParameters_() {
  }

  @Override
  public void load(double[] vector) {
    this.prior_rain_t = vector[0];
    this.inf_sprinkler_t$t = vector[1];
    this.inf_sprinkler_t$f = vector[2];
    this.inf_wet_t$t_t = vector[3];
    this.inf_wet_t$t_f = vector[4];
    this.inf_wet_t$f_t = vector[5];
    this.inf_wet_t$f_f = vector[6];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[7];

    vector[0] = this.prior_rain_t;
    vector[1] = this.inf_sprinkler_t$t;
    vector[2] = this.inf_sprinkler_t$f;
    vector[3] = this.inf_wet_t$t_t;
    vector[4] = this.inf_wet_t$t_f;
    vector[5] = this.inf_wet_t$f_t;
    vector[6] = this.inf_wet_t$f_f;
    return vector;
  }

  public void build() {
    this.prior_rain_f = 1.0 - this.prior_rain_t;
    this.inf_sprinkler_f$t = 1.0 - this.inf_sprinkler_t$t;
    this.inf_sprinkler_f$f = 1.0 - this.inf_sprinkler_t$f;
    this.inf_wet_f$t_t = 1.0 - this.inf_wet_t$t_t;
    this.inf_wet_f$t_f = 1.0 - this.inf_wet_t$t_f;
    this.inf_wet_f$f_t = 1.0 - this.inf_wet_t$f_t;
    this.inf_wet_f$f_f = 1.0 - this.inf_wet_t$f_f;
    this.derived_wet_t$t_t = this.inf_wet_t$t_t * this.inf_sprinkler_t$t;
    this.derived_wet_f$t_t = this.inf_wet_f$t_t * this.inf_sprinkler_t$t;
    this.derived_wet_t$t_f = this.inf_wet_t$t_f * this.inf_sprinkler_f$t;
    this.derived_wet_f$t_f = this.inf_wet_f$t_f * this.inf_sprinkler_f$t;
    this.derived_wet_t$f_t = this.inf_wet_t$f_t * this.inf_sprinkler_t$f;
    this.derived_wet_f$f_t = this.inf_wet_f$f_t * this.inf_sprinkler_t$f;
    this.derived_wet_t$f_f = this.inf_wet_t$f_f * this.inf_sprinkler_f$f;
    this.derived_wet_f$f_f = this.inf_wet_f$f_f * this.inf_sprinkler_f$f;
  }

}