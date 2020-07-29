package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;

public class GrassParameters implements Parameters {
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
    this.inf_f_t$sprinkler = 1.0 - this.inf_t_t$sprinkler;
    this.inf_f_f$sprinkler = 1.0 - this.inf_t_f$sprinkler;
    this.inf_f_tt$wet = 1.0 - this.inf_t_tt$wet;
    this.inf_f_tf$wet = 1.0 - this.inf_t_tf$wet;
    this.inf_f_ft$wet = 1.0 - this.inf_t_ft$wet;
    this.inf_f_ff$wet = 1.0 - this.inf_t_ff$wet;
    this.derived_t_tt$wet = this.inf_t_tt$wet * this.inf_t_t$sprinkler;
    this.derived_f_tt$wet = this.inf_f_tt$wet * this.inf_t_t$sprinkler;
    this.derived_t_tf$wet = this.inf_t_ft$wet * this.inf_t_f$sprinkler;
    this.derived_f_tf$wet = this.inf_f_ft$wet * this.inf_t_f$sprinkler;
    this.derived_t_ft$wet = this.inf_t_tf$wet * this.inf_f_t$sprinkler;
    this.derived_f_ft$wet = this.inf_f_tf$wet * this.inf_f_t$sprinkler;
    this.derived_t_ff$wet = this.inf_t_ff$wet * this.inf_f_f$sprinkler;
    this.derived_f_ff$wet = this.inf_f_ff$wet * this.inf_f_f$sprinkler;
  }

}