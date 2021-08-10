package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;

public class SimpleLinnaeanParameters_TT implements Parameters {

  public final static String SIGNATURE = "TT";

  public double prior_t$taxonId; // taxonID prior probability
  public double prior_f$taxonId; // 1 - taxonID prior probability
  public double inf_t_t$taxonRank; // p(taxonRank | taxonID) conditional probability
  public double inf_f_t$taxonRank; // p(¬taxonRank | taxonID) =  1 - p(taxonRank | taxonID) conditional probability
  public double inf_t_f$taxonRank; // p(taxonRank | ¬taxonID) conditional probability
  public double inf_f_f$taxonRank; // p(¬taxonRank | ¬taxonID) =  1 - p(taxonRank | ¬taxonID) conditional probability
  public double inf_t_t$specificEpithet; // p(specificEpithet | taxonID) conditional probability
  public double inf_f_t$specificEpithet; // p(¬specificEpithet | taxonID) =  1 - p(specificEpithet | taxonID) conditional probability
  public double inf_t_f$specificEpithet; // p(specificEpithet | ¬taxonID) conditional probability
  public double inf_f_f$specificEpithet; // p(¬specificEpithet | ¬taxonID) =  1 - p(specificEpithet | ¬taxonID) conditional probability
  public double inf_t_t$scientificNameAuthorship; // p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_f_t$scientificNameAuthorship; // p(¬scientificNameAuthorship | taxonID) =  1 - p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_t_f$scientificNameAuthorship; // p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_f_f$scientificNameAuthorship; // p(¬scientificNameAuthorship | ¬taxonID) =  1 - p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_t_tt$scientificName; // p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_f_tt$scientificName; // p(¬scientificName | taxonID, specificEpithet) =  1 - p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_t_tf$scientificName; // p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_f_tf$scientificName; // p(¬scientificName | taxonID, ¬specificEpithet) =  1 - p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_t_ft$scientificName; // p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_f_ft$scientificName; // p(¬scientificName | ¬taxonID, specificEpithet) =  1 - p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_t_ff$scientificName; // p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double inf_f_ff$scientificName; // p(¬scientificName | ¬taxonID, ¬specificEpithet) =  1 - p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double derived_t_tt$scientificName; // p(scientificName | specificEpithet, taxonID) = p(scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_f_tt$scientificName; // p(¬scientificName | specificEpithet, taxonID) = p(¬scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_t_tf$scientificName; // p(scientificName | specificEpithet, ¬taxonID) = p(scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_f_tf$scientificName; // p(¬scientificName | specificEpithet, ¬taxonID) = p(¬scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_t_ft$scientificName; // p(scientificName | ¬specificEpithet, taxonID) = p(scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_f_ft$scientificName; // p(¬scientificName | ¬specificEpithet, taxonID) = p(¬scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_t_ff$scientificName; // p(scientificName | ¬specificEpithet, ¬taxonID) = p(scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_f_ff$scientificName; // p(¬scientificName | ¬specificEpithet, ¬taxonID) = p(¬scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double inf_t_t$soundexScientificName; // p(soundexScientificName | scientificName) conditional probability
  public double inf_f_t$soundexScientificName; // p(¬soundexScientificName | scientificName) =  1 - p(soundexScientificName | scientificName) conditional probability
  public double inf_t_f$soundexScientificName; // p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_f_f$soundexScientificName; // p(¬soundexScientificName | ¬scientificName) =  1 - p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_t_tt$genus; // p(genus | scientificName, soundexScientificName) conditional probability
  public double inf_f_tt$genus; // p(¬genus | scientificName, soundexScientificName) =  1 - p(genus | scientificName, soundexScientificName) conditional probability
  public double inf_t_tf$genus; // p(genus | scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_tf$genus; // p(¬genus | scientificName, ¬soundexScientificName) =  1 - p(genus | scientificName, ¬soundexScientificName) conditional probability
  public double inf_t_ft$genus; // p(genus | ¬scientificName, soundexScientificName) conditional probability
  public double inf_f_ft$genus; // p(¬genus | ¬scientificName, soundexScientificName) =  1 - p(genus | ¬scientificName, soundexScientificName) conditional probability
  public double inf_t_ff$genus; // p(genus | ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_ff$genus; // p(¬genus | ¬scientificName, ¬soundexScientificName) =  1 - p(genus | ¬scientificName, ¬soundexScientificName) conditional probability
  public double derived_t_tt$genus; // p(genus | soundexScientificName, scientificName) = p(genus | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_tt$genus; // p(¬genus | soundexScientificName, scientificName) = p(¬genus | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_tf$genus; // p(genus | soundexScientificName, ¬scientificName) = p(genus | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_tf$genus; // p(¬genus | soundexScientificName, ¬scientificName) = p(¬genus | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_t_ft$genus; // p(genus | ¬soundexScientificName, scientificName) = p(genus | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_ft$genus; // p(¬genus | ¬soundexScientificName, scientificName) = p(¬genus | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_ff$genus; // p(genus | ¬soundexScientificName, ¬scientificName) = p(genus | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_ff$genus; // p(¬genus | ¬soundexScientificName, ¬scientificName) = p(¬genus | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double inf_t_t$family; // p(family | genus) conditional probability
  public double inf_f_t$family; // p(¬family | genus) =  1 - p(family | genus) conditional probability
  public double inf_t_f$family; // p(family | ¬genus) conditional probability
  public double inf_f_f$family; // p(¬family | ¬genus) =  1 - p(family | ¬genus) conditional probability
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

  public SimpleLinnaeanParameters_TT() {
  }

  @Override
  public void load(double[] vector) {
    this.prior_t$taxonId = vector[0];
    this.inf_t_t$taxonRank = vector[1];
    this.inf_t_f$taxonRank = vector[2];
    this.inf_t_t$specificEpithet = vector[3];
    this.inf_t_f$specificEpithet = vector[4];
    this.inf_t_t$scientificNameAuthorship = vector[5];
    this.inf_t_f$scientificNameAuthorship = vector[6];
    this.inf_t_tt$scientificName = vector[7];
    this.inf_t_tf$scientificName = vector[8];
    this.inf_t_ft$scientificName = vector[9];
    this.inf_t_ff$scientificName = vector[10];
    this.inf_t_t$soundexScientificName = vector[11];
    this.inf_t_f$soundexScientificName = vector[12];
    this.inf_t_tt$genus = vector[13];
    this.inf_t_tf$genus = vector[14];
    this.inf_t_ft$genus = vector[15];
    this.inf_t_ff$genus = vector[16];
    this.inf_t_t$family = vector[17];
    this.inf_t_f$family = vector[18];
    this.inf_t_t$order = vector[19];
    this.inf_t_f$order = vector[20];
    this.inf_t_t$class_ = vector[21];
    this.inf_t_f$class_ = vector[22];
    this.inf_t_t$phylum = vector[23];
    this.inf_t_f$phylum = vector[24];
    this.inf_t_t$kingdom = vector[25];
    this.inf_t_f$kingdom = vector[26];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[27];

    vector[0] = this.prior_t$taxonId;
    vector[1] = this.inf_t_t$taxonRank;
    vector[2] = this.inf_t_f$taxonRank;
    vector[3] = this.inf_t_t$specificEpithet;
    vector[4] = this.inf_t_f$specificEpithet;
    vector[5] = this.inf_t_t$scientificNameAuthorship;
    vector[6] = this.inf_t_f$scientificNameAuthorship;
    vector[7] = this.inf_t_tt$scientificName;
    vector[8] = this.inf_t_tf$scientificName;
    vector[9] = this.inf_t_ft$scientificName;
    vector[10] = this.inf_t_ff$scientificName;
    vector[11] = this.inf_t_t$soundexScientificName;
    vector[12] = this.inf_t_f$soundexScientificName;
    vector[13] = this.inf_t_tt$genus;
    vector[14] = this.inf_t_tf$genus;
    vector[15] = this.inf_t_ft$genus;
    vector[16] = this.inf_t_ff$genus;
    vector[17] = this.inf_t_t$family;
    vector[18] = this.inf_t_f$family;
    vector[19] = this.inf_t_t$order;
    vector[20] = this.inf_t_f$order;
    vector[21] = this.inf_t_t$class_;
    vector[22] = this.inf_t_f$class_;
    vector[23] = this.inf_t_t$phylum;
    vector[24] = this.inf_t_f$phylum;
    vector[25] = this.inf_t_t$kingdom;
    vector[26] = this.inf_t_f$kingdom;
    return vector;
  }

  public void build() {
    this.prior_f$taxonId = 1.0 - this.prior_t$taxonId;
    this.inf_f_t$taxonRank = 1.0 - this.inf_t_t$taxonRank;
    this.inf_f_f$taxonRank = 1.0 - this.inf_t_f$taxonRank;
    this.inf_f_t$specificEpithet = 1.0 - this.inf_t_t$specificEpithet;
    this.inf_f_f$specificEpithet = 1.0 - this.inf_t_f$specificEpithet;
    this.inf_f_t$scientificNameAuthorship = 1.0 - this.inf_t_t$scientificNameAuthorship;
    this.inf_f_f$scientificNameAuthorship = 1.0 - this.inf_t_f$scientificNameAuthorship;
    this.inf_f_tt$scientificName = 1.0 - this.inf_t_tt$scientificName;
    this.inf_f_tf$scientificName = 1.0 - this.inf_t_tf$scientificName;
    this.inf_f_ft$scientificName = 1.0 - this.inf_t_ft$scientificName;
    this.inf_f_ff$scientificName = 1.0 - this.inf_t_ff$scientificName;
    this.derived_t_tt$scientificName = this.inf_t_tt$scientificName * this.inf_t_t$specificEpithet;
    this.derived_f_tt$scientificName = this.inf_f_tt$scientificName * this.inf_t_t$specificEpithet;
    this.derived_t_tf$scientificName = this.inf_t_ft$scientificName * this.inf_t_f$specificEpithet;
    this.derived_f_tf$scientificName = this.inf_f_ft$scientificName * this.inf_t_f$specificEpithet;
    this.derived_t_ft$scientificName = this.inf_t_tf$scientificName * this.inf_f_t$specificEpithet;
    this.derived_f_ft$scientificName = this.inf_f_tf$scientificName * this.inf_f_t$specificEpithet;
    this.derived_t_ff$scientificName = this.inf_t_ff$scientificName * this.inf_f_f$specificEpithet;
    this.derived_f_ff$scientificName = this.inf_f_ff$scientificName * this.inf_f_f$specificEpithet;
    this.inf_f_t$soundexScientificName = 1.0 - this.inf_t_t$soundexScientificName;
    this.inf_f_f$soundexScientificName = 1.0 - this.inf_t_f$soundexScientificName;
    this.inf_f_tt$genus = 1.0 - this.inf_t_tt$genus;
    this.inf_f_tf$genus = 1.0 - this.inf_t_tf$genus;
    this.inf_f_ft$genus = 1.0 - this.inf_t_ft$genus;
    this.inf_f_ff$genus = 1.0 - this.inf_t_ff$genus;
    this.derived_t_tt$genus = this.inf_t_tt$genus * this.inf_t_t$soundexScientificName;
    this.derived_f_tt$genus = this.inf_f_tt$genus * this.inf_t_t$soundexScientificName;
    this.derived_t_tf$genus = this.inf_t_ft$genus * this.inf_t_f$soundexScientificName;
    this.derived_f_tf$genus = this.inf_f_ft$genus * this.inf_t_f$soundexScientificName;
    this.derived_t_ft$genus = this.inf_t_tf$genus * this.inf_f_t$soundexScientificName;
    this.derived_f_ft$genus = this.inf_f_tf$genus * this.inf_f_t$soundexScientificName;
    this.derived_t_ff$genus = this.inf_t_ff$genus * this.inf_f_f$soundexScientificName;
    this.derived_f_ff$genus = this.inf_f_ff$genus * this.inf_f_f$soundexScientificName;
    this.inf_f_t$family = 1.0 - this.inf_t_t$family;
    this.inf_f_f$family = 1.0 - this.inf_t_f$family;
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