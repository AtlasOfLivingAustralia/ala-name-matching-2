package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;

public class SimpleLinnaeanParameters_FF implements Parameters {

  public final static String SIGNATURE = "FF";

  public double prior_t$taxonId; // taxonID prior probability
  public double prior_f$taxonId; // 1 - taxonID prior probability
  public double inf_t_t$taxonRank; // p(taxonRank | taxonID) conditional probability
  public double inf_f_t$taxonRank; // p(¬taxonRank | taxonID) =  1 - p(taxonRank | taxonID) conditional probability
  public double inf_t_f$taxonRank; // p(taxonRank | ¬taxonID) conditional probability
  public double inf_f_f$taxonRank; // p(¬taxonRank | ¬taxonID) =  1 - p(taxonRank | ¬taxonID) conditional probability
  public double inf_t_t$scientificNameAuthorship; // p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_f_t$scientificNameAuthorship; // p(¬scientificNameAuthorship | taxonID) =  1 - p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_t_f$scientificNameAuthorship; // p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_f_f$scientificNameAuthorship; // p(¬scientificNameAuthorship | ¬taxonID) =  1 - p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_t_t$scientificName; // p(scientificName | taxonID) conditional probability
  public double inf_f_t$scientificName; // p(¬scientificName | taxonID) =  1 - p(scientificName | taxonID) conditional probability
  public double inf_t_f$scientificName; // p(scientificName | ¬taxonID) conditional probability
  public double inf_f_f$scientificName; // p(¬scientificName | ¬taxonID) =  1 - p(scientificName | ¬taxonID) conditional probability
  public double inf_t_t$soundexScientificName; // p(soundexScientificName | scientificName) conditional probability
  public double inf_f_t$soundexScientificName; // p(¬soundexScientificName | scientificName) =  1 - p(soundexScientificName | scientificName) conditional probability
  public double inf_t_f$soundexScientificName; // p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_f_f$soundexScientificName; // p(¬soundexScientificName | ¬scientificName) =  1 - p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_t_tt$family; // p(family | scientificName, soundexScientificName) conditional probability
  public double inf_f_tt$family; // p(¬family | scientificName, soundexScientificName) =  1 - p(family | scientificName, soundexScientificName) conditional probability
  public double inf_t_tf$family; // p(family | scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_tf$family; // p(¬family | scientificName, ¬soundexScientificName) =  1 - p(family | scientificName, ¬soundexScientificName) conditional probability
  public double inf_t_ft$family; // p(family | ¬scientificName, soundexScientificName) conditional probability
  public double inf_f_ft$family; // p(¬family | ¬scientificName, soundexScientificName) =  1 - p(family | ¬scientificName, soundexScientificName) conditional probability
  public double inf_t_ff$family; // p(family | ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_ff$family; // p(¬family | ¬scientificName, ¬soundexScientificName) =  1 - p(family | ¬scientificName, ¬soundexScientificName) conditional probability
  public double derived_t_tt$family; // p(family | soundexScientificName, scientificName) = p(family | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_tt$family; // p(¬family | soundexScientificName, scientificName) = p(¬family | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_tf$family; // p(family | soundexScientificName, ¬scientificName) = p(family | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_tf$family; // p(¬family | soundexScientificName, ¬scientificName) = p(¬family | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_t_ft$family; // p(family | ¬soundexScientificName, scientificName) = p(family | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_ft$family; // p(¬family | ¬soundexScientificName, scientificName) = p(¬family | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_ff$family; // p(family | ¬soundexScientificName, ¬scientificName) = p(family | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_ff$family; // p(¬family | ¬soundexScientificName, ¬scientificName) = p(¬family | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double inf_t_t$order; // p(order | family) conditional probability
  public double inf_f_t$order; // p(¬order | family) =  1 - p(order | family) conditional probability
  public double inf_t_f$order; // p(order | ¬family) conditional probability
  public double inf_f_f$order; // p(¬order | ¬family) =  1 - p(order | ¬family) conditional probability
  public double inf_t_t$class_; // p(class | order) conditional probability
  public double inf_f_t$class_; // p(¬class | order) =  1 - p(class | order) conditional probability
  public double inf_t_f$class_; // p(class | ¬order) conditional probability
  public double inf_f_f$class_; // p(¬class | ¬order) =  1 - p(class | ¬order) conditional probability
  public double inf_t_t$phylum; // p(phylum | class) conditional probability
  public double inf_f_t$phylum; // p(¬phylum | class) =  1 - p(phylum | class) conditional probability
  public double inf_t_f$phylum; // p(phylum | ¬class) conditional probability
  public double inf_f_f$phylum; // p(¬phylum | ¬class) =  1 - p(phylum | ¬class) conditional probability
  public double inf_t_t$kingdom; // p(kingdom | phylum) conditional probability
  public double inf_f_t$kingdom; // p(¬kingdom | phylum) =  1 - p(kingdom | phylum) conditional probability
  public double inf_t_f$kingdom; // p(kingdom | ¬phylum) conditional probability
  public double inf_f_f$kingdom; // p(¬kingdom | ¬phylum) =  1 - p(kingdom | ¬phylum) conditional probability

  public SimpleLinnaeanParameters_FF() {
  }

  @Override
  public void load(double[] vector) {
    this.prior_t$taxonId = vector[0];
    this.inf_t_t$taxonRank = vector[1];
    this.inf_t_f$taxonRank = vector[2];
    this.inf_t_t$scientificNameAuthorship = vector[3];
    this.inf_t_f$scientificNameAuthorship = vector[4];
    this.inf_t_t$scientificName = vector[5];
    this.inf_t_f$scientificName = vector[6];
    this.inf_t_t$soundexScientificName = vector[7];
    this.inf_t_f$soundexScientificName = vector[8];
    this.inf_t_tt$family = vector[9];
    this.inf_t_tf$family = vector[10];
    this.inf_t_ft$family = vector[11];
    this.inf_t_ff$family = vector[12];
    this.inf_t_t$order = vector[13];
    this.inf_t_f$order = vector[14];
    this.inf_t_t$class_ = vector[15];
    this.inf_t_f$class_ = vector[16];
    this.inf_t_t$phylum = vector[17];
    this.inf_t_f$phylum = vector[18];
    this.inf_t_t$kingdom = vector[19];
    this.inf_t_f$kingdom = vector[20];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[21];

    vector[0] = this.prior_t$taxonId;
    vector[1] = this.inf_t_t$taxonRank;
    vector[2] = this.inf_t_f$taxonRank;
    vector[3] = this.inf_t_t$scientificNameAuthorship;
    vector[4] = this.inf_t_f$scientificNameAuthorship;
    vector[5] = this.inf_t_t$scientificName;
    vector[6] = this.inf_t_f$scientificName;
    vector[7] = this.inf_t_t$soundexScientificName;
    vector[8] = this.inf_t_f$soundexScientificName;
    vector[9] = this.inf_t_tt$family;
    vector[10] = this.inf_t_tf$family;
    vector[11] = this.inf_t_ft$family;
    vector[12] = this.inf_t_ff$family;
    vector[13] = this.inf_t_t$order;
    vector[14] = this.inf_t_f$order;
    vector[15] = this.inf_t_t$class_;
    vector[16] = this.inf_t_f$class_;
    vector[17] = this.inf_t_t$phylum;
    vector[18] = this.inf_t_f$phylum;
    vector[19] = this.inf_t_t$kingdom;
    vector[20] = this.inf_t_f$kingdom;
    return vector;
  }

  public void build() {
    this.prior_f$taxonId = 1.0 - this.prior_t$taxonId;
    this.inf_f_t$taxonRank = 1.0 - this.inf_t_t$taxonRank;
    this.inf_f_f$taxonRank = 1.0 - this.inf_t_f$taxonRank;
    this.inf_f_t$scientificNameAuthorship = 1.0 - this.inf_t_t$scientificNameAuthorship;
    this.inf_f_f$scientificNameAuthorship = 1.0 - this.inf_t_f$scientificNameAuthorship;
    this.inf_f_t$scientificName = 1.0 - this.inf_t_t$scientificName;
    this.inf_f_f$scientificName = 1.0 - this.inf_t_f$scientificName;
    this.inf_f_t$soundexScientificName = 1.0 - this.inf_t_t$soundexScientificName;
    this.inf_f_f$soundexScientificName = 1.0 - this.inf_t_f$soundexScientificName;
    this.inf_f_tt$family = 1.0 - this.inf_t_tt$family;
    this.inf_f_tf$family = 1.0 - this.inf_t_tf$family;
    this.inf_f_ft$family = 1.0 - this.inf_t_ft$family;
    this.inf_f_ff$family = 1.0 - this.inf_t_ff$family;
    this.derived_t_tt$family = this.inf_t_tt$family * this.inf_t_t$soundexScientificName;
    this.derived_f_tt$family = this.inf_f_tt$family * this.inf_t_t$soundexScientificName;
    this.derived_t_tf$family = this.inf_t_ft$family * this.inf_t_f$soundexScientificName;
    this.derived_f_tf$family = this.inf_f_ft$family * this.inf_t_f$soundexScientificName;
    this.derived_t_ft$family = this.inf_t_tf$family * this.inf_f_t$soundexScientificName;
    this.derived_f_ft$family = this.inf_f_tf$family * this.inf_f_t$soundexScientificName;
    this.derived_t_ff$family = this.inf_t_ff$family * this.inf_f_f$soundexScientificName;
    this.derived_f_ff$family = this.inf_f_ff$family * this.inf_f_f$soundexScientificName;
    this.inf_f_t$order = 1.0 - this.inf_t_t$order;
    this.inf_f_f$order = 1.0 - this.inf_t_f$order;
    this.inf_f_t$class_ = 1.0 - this.inf_t_t$class_;
    this.inf_f_f$class_ = 1.0 - this.inf_t_f$class_;
    this.inf_f_t$phylum = 1.0 - this.inf_t_t$phylum;
    this.inf_f_f$phylum = 1.0 - this.inf_t_f$phylum;
    this.inf_f_t$kingdom = 1.0 - this.inf_t_t$kingdom;
    this.inf_f_f$kingdom = 1.0 - this.inf_t_f$kingdom;
  }

}