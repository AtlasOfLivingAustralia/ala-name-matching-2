package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;

public class GrassParameters_ implements Parameters {

  public final static String SIGNATURE = "";

  public double prior_t$rain; // rain prior probability
  public double prior_f$rain; // 1 - rain prior probability
  public double inf_t_t$sprinkler$t; // p(sprinkler | rain) conditional probability
  public double inf_f_t$sprinkler$t; // p(¬sprinkler | rain) =  1 - p(sprinkler | rain) conditional probability
  public double inf_t_f$sprinkler$t; // p(sprinkler | ¬rain) conditional probability
  public double inf_f_f$sprinkler$t; // p(¬sprinkler | ¬rain) =  1 - p(sprinkler | ¬rain) conditional probability
  public double inf_t_t$sprinkler$f; // p(sprinkler | rain) conditional probability
  public double inf_f_t$sprinkler$f; // p(¬sprinkler | rain) =  1 - p(sprinkler | rain) conditional probability
  public double inf_t_f$sprinkler$f; // p(sprinkler | ¬rain) conditional probability
  public double inf_f_f$sprinkler$f; // p(¬sprinkler | ¬rain) =  1 - p(sprinkler | ¬rain) conditional probability
  public double inf_t_tt$wet$t; // p(wet | rain, sprinkler) conditional probability
  public double inf_f_tt$wet$t; // p(¬wet | rain, sprinkler) =  1 - p(wet | rain, sprinkler) conditional probability
  public double inf_t_tf$wet$t; // p(wet | rain, ¬sprinkler) conditional probability
  public double inf_f_tf$wet$t; // p(¬wet | rain, ¬sprinkler) =  1 - p(wet | rain, ¬sprinkler) conditional probability
  public double inf_t_ft$wet$t; // p(wet | ¬rain, sprinkler) conditional probability
  public double inf_f_ft$wet$t; // p(¬wet | ¬rain, sprinkler) =  1 - p(wet | ¬rain, sprinkler) conditional probability
  public double inf_t_ff$wet$t; // p(wet | ¬rain, ¬sprinkler) conditional probability
  public double inf_f_ff$wet$t; // p(¬wet | ¬rain, ¬sprinkler) =  1 - p(wet | ¬rain, ¬sprinkler) conditional probability
  public double derived_t_tt$wet$t; // p(wet | sprinkler, rain) = p(wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
  public double derived_f_tt$wet$t; // p(¬wet | sprinkler, rain) = p(¬wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
  public double derived_t_tf$wet$t; // p(wet | sprinkler, ¬rain) = p(wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
  public double derived_f_tf$wet$t; // p(¬wet | sprinkler, ¬rain) = p(¬wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
  public double derived_t_ft$wet$t; // p(wet | ¬sprinkler, rain) = p(wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
  public double derived_f_ft$wet$t; // p(¬wet | ¬sprinkler, rain) = p(¬wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
  public double derived_t_ff$wet$t; // p(wet | ¬sprinkler, ¬rain) = p(wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability
  public double derived_f_ff$wet$t; // p(¬wet | ¬sprinkler, ¬rain) = p(¬wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability
  public double inf_t_tt$wet$f; // p(wet | rain, sprinkler) conditional probability
  public double inf_f_tt$wet$f; // p(¬wet | rain, sprinkler) =  1 - p(wet | rain, sprinkler) conditional probability
  public double inf_t_tf$wet$f; // p(wet | rain, ¬sprinkler) conditional probability
  public double inf_f_tf$wet$f; // p(¬wet | rain, ¬sprinkler) =  1 - p(wet | rain, ¬sprinkler) conditional probability
  public double inf_t_ft$wet$f; // p(wet | ¬rain, sprinkler) conditional probability
  public double inf_f_ft$wet$f; // p(¬wet | ¬rain, sprinkler) =  1 - p(wet | ¬rain, sprinkler) conditional probability
  public double inf_t_ff$wet$f; // p(wet | ¬rain, ¬sprinkler) conditional probability
  public double inf_f_ff$wet$f; // p(¬wet | ¬rain, ¬sprinkler) =  1 - p(wet | ¬rain, ¬sprinkler) conditional probability
  public double derived_t_tt$wet$f; // p(wet | sprinkler, rain) = p(wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
  public double derived_f_tt$wet$f; // p(¬wet | sprinkler, rain) = p(¬wet | rain, sprinkler).p(sprinkler | rain)  derived conditional probability
  public double derived_t_tf$wet$f; // p(wet | sprinkler, ¬rain) = p(wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
  public double derived_f_tf$wet$f; // p(¬wet | sprinkler, ¬rain) = p(¬wet | ¬rain, sprinkler).p(sprinkler | ¬rain)  derived conditional probability
  public double derived_t_ft$wet$f; // p(wet | ¬sprinkler, rain) = p(wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
  public double derived_f_ft$wet$f; // p(¬wet | ¬sprinkler, rain) = p(¬wet | rain, ¬sprinkler).p(¬sprinkler | rain)  derived conditional probability
  public double derived_t_ff$wet$f; // p(wet | ¬sprinkler, ¬rain) = p(wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability
  public double derived_f_ff$wet$f; // p(¬wet | ¬sprinkler, ¬rain) = p(¬wet | ¬rain, ¬sprinkler).p(¬sprinkler | ¬rain)  derived conditional probability

  public GrassParameters_() {
  }

  @Override
  public void load(double[] vector) {
    this.prior_t$rain = vector[0];
    this.inf_t_t$sprinkler$t = vector[1];
    this.inf_t_t$sprinkler$f = vector[2];
    this.inf_t_f$sprinkler$t = vector[3];
    this.inf_t_f$sprinkler$f = vector[4];
    this.inf_t_tt$wet$t = vector[5];
    this.inf_t_tt$wet$f = vector[6];
    this.inf_t_tf$wet$t = vector[7];
    this.inf_t_tf$wet$f = vector[8];
    this.inf_t_ft$wet$t = vector[9];
    this.inf_t_ft$wet$f = vector[10];
    this.inf_t_ff$wet$t = vector[11];
    this.inf_t_ff$wet$f = vector[12];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[13];

    vector[0] = this.prior_t$rain;
    vector[1] = this.inf_t_t$sprinkler$t;
    vector[2] = this.inf_t_t$sprinkler$f;
    vector[3] = this.inf_t_f$sprinkler$t;
    vector[4] = this.inf_t_f$sprinkler$f;
    vector[5] = this.inf_t_tt$wet$t;
    vector[6] = this.inf_t_tt$wet$f;
    vector[7] = this.inf_t_tf$wet$t;
    vector[8] = this.inf_t_tf$wet$f;
    vector[9] = this.inf_t_ft$wet$t;
    vector[10] = this.inf_t_ft$wet$f;
    vector[11] = this.inf_t_ff$wet$t;
    vector[12] = this.inf_t_ff$wet$f;
    return vector;
  }

  public void build() {
    this.prior_f$rain = 1.0 - this.prior_t$rain;
    this.inf_f_t$sprinkler$t = 1.0 - this.inf_t_t$sprinkler$t;
    this.inf_f_f$sprinkler$t = 1.0 - this.inf_t_f$sprinkler$t;
    this.inf_f_t$sprinkler$f = 1.0 - this.inf_t_t$sprinkler$f;
    this.inf_f_f$sprinkler$f = 1.0 - this.inf_t_f$sprinkler$f;
    this.inf_f_tt$wet$t = 1.0 - this.inf_t_tt$wet$t;
    this.inf_f_tf$wet$t = 1.0 - this.inf_t_tf$wet$t;
    this.inf_f_ft$wet$t = 1.0 - this.inf_t_ft$wet$t;
    this.inf_f_ff$wet$t = 1.0 - this.inf_t_ff$wet$t;
    this.derived_t_tt$wet$t = this.inf_t_tt$wet$t * this.inf_t_t$sprinkler$t;
    this.derived_f_tt$wet$t = this.inf_f_tt$wet$t * this.inf_t_t$sprinkler$t;
    this.derived_t_tf$wet$t = this.inf_t_ft$wet$t * this.inf_t_f$sprinkler$t;
    this.derived_f_tf$wet$t = this.inf_f_ft$wet$t * this.inf_t_f$sprinkler$t;
    this.derived_t_ft$wet$t = this.inf_t_tf$wet$t * this.inf_f_t$sprinkler$t;
    this.derived_f_ft$wet$t = this.inf_f_tf$wet$t * this.inf_f_t$sprinkler$t;
    this.derived_t_ff$wet$t = this.inf_t_ff$wet$t * this.inf_f_f$sprinkler$t;
    this.derived_f_ff$wet$t = this.inf_f_ff$wet$t * this.inf_f_f$sprinkler$t;
    this.inf_f_tt$wet$f = 1.0 - this.inf_t_tt$wet$f;
    this.inf_f_tf$wet$f = 1.0 - this.inf_t_tf$wet$f;
    this.inf_f_ft$wet$f = 1.0 - this.inf_t_ft$wet$f;
    this.inf_f_ff$wet$f = 1.0 - this.inf_t_ff$wet$f;
    this.derived_t_tt$wet$f = this.inf_t_tt$wet$f * this.inf_t_t$sprinkler$f;
    this.derived_f_tt$wet$f = this.inf_f_tt$wet$f * this.inf_t_t$sprinkler$f;
    this.derived_t_tf$wet$f = this.inf_t_ft$wet$f * this.inf_t_f$sprinkler$f;
    this.derived_f_tf$wet$f = this.inf_f_ft$wet$f * this.inf_t_f$sprinkler$f;
    this.derived_t_ft$wet$f = this.inf_t_tf$wet$f * this.inf_f_t$sprinkler$f;
    this.derived_f_ft$wet$f = this.inf_f_tf$wet$f * this.inf_f_t$sprinkler$f;
    this.derived_t_ff$wet$f = this.inf_t_ff$wet$f * this.inf_f_f$sprinkler$f;
    this.derived_f_ff$wet$f = this.inf_f_ff$wet$f * this.inf_f_f$sprinkler$f;
  }

}