package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;

public class SimpleLinnaeanParameters_FT implements Parameters {

  public final static String SIGNATURE = "FT";

  public double prior_t$taxonId; // taxonID prior probability
  public double prior_f$taxonId; // 1 - taxonID prior probability
  public double inf_t_t$taxonRank$t; // p(taxonRank | taxonID) conditional probability
  public double inf_f_t$taxonRank$t; // p(¬taxonRank | taxonID) =  1 - p(taxonRank | taxonID) conditional probability
  public double inf_t_f$taxonRank$t; // p(taxonRank | ¬taxonID) conditional probability
  public double inf_f_f$taxonRank$t; // p(¬taxonRank | ¬taxonID) =  1 - p(taxonRank | ¬taxonID) conditional probability
  public double inf_t_t$taxonRank$f; // p(taxonRank | taxonID) conditional probability
  public double inf_f_t$taxonRank$f; // p(¬taxonRank | taxonID) =  1 - p(taxonRank | taxonID) conditional probability
  public double inf_t_f$taxonRank$f; // p(taxonRank | ¬taxonID) conditional probability
  public double inf_f_f$taxonRank$f; // p(¬taxonRank | ¬taxonID) =  1 - p(taxonRank | ¬taxonID) conditional probability
  public double inf_t_t$scientificNameAuthorship$t; // p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_f_t$scientificNameAuthorship$t; // p(¬scientificNameAuthorship | taxonID) =  1 - p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_t_f$scientificNameAuthorship$t; // p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_f_f$scientificNameAuthorship$t; // p(¬scientificNameAuthorship | ¬taxonID) =  1 - p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_t_t$scientificNameAuthorship$f; // p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_f_t$scientificNameAuthorship$f; // p(¬scientificNameAuthorship | taxonID) =  1 - p(scientificNameAuthorship | taxonID) conditional probability
  public double inf_t_f$scientificNameAuthorship$f; // p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_f_f$scientificNameAuthorship$f; // p(¬scientificNameAuthorship | ¬taxonID) =  1 - p(scientificNameAuthorship | ¬taxonID) conditional probability
  public double inf_t_t$scientificName$t; // p(scientificName | taxonID) conditional probability
  public double inf_f_t$scientificName$t; // p(¬scientificName | taxonID) =  1 - p(scientificName | taxonID) conditional probability
  public double inf_t_f$scientificName$t; // p(scientificName | ¬taxonID) conditional probability
  public double inf_f_f$scientificName$t; // p(¬scientificName | ¬taxonID) =  1 - p(scientificName | ¬taxonID) conditional probability
  public double inf_t_t$scientificName$f; // p(scientificName | taxonID) conditional probability
  public double inf_f_t$scientificName$f; // p(¬scientificName | taxonID) =  1 - p(scientificName | taxonID) conditional probability
  public double inf_t_f$scientificName$f; // p(scientificName | ¬taxonID) conditional probability
  public double inf_f_f$scientificName$f; // p(¬scientificName | ¬taxonID) =  1 - p(scientificName | ¬taxonID) conditional probability
  public double inf_t_t$soundexScientificName$t; // p(soundexScientificName | scientificName) conditional probability
  public double inf_f_t$soundexScientificName$t; // p(¬soundexScientificName | scientificName) =  1 - p(soundexScientificName | scientificName) conditional probability
  public double inf_t_f$soundexScientificName$t; // p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_f_f$soundexScientificName$t; // p(¬soundexScientificName | ¬scientificName) =  1 - p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_t_t$soundexScientificName$f; // p(soundexScientificName | scientificName) conditional probability
  public double inf_f_t$soundexScientificName$f; // p(¬soundexScientificName | scientificName) =  1 - p(soundexScientificName | scientificName) conditional probability
  public double inf_t_f$soundexScientificName$f; // p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_f_f$soundexScientificName$f; // p(¬soundexScientificName | ¬scientificName) =  1 - p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_t_tt$genus$t; // p(genus | scientificName, soundexScientificName) conditional probability
  public double inf_f_tt$genus$t; // p(¬genus | scientificName, soundexScientificName) =  1 - p(genus | scientificName, soundexScientificName) conditional probability
  public double inf_t_tf$genus$t; // p(genus | scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_tf$genus$t; // p(¬genus | scientificName, ¬soundexScientificName) =  1 - p(genus | scientificName, ¬soundexScientificName) conditional probability
  public double inf_t_ft$genus$t; // p(genus | ¬scientificName, soundexScientificName) conditional probability
  public double inf_f_ft$genus$t; // p(¬genus | ¬scientificName, soundexScientificName) =  1 - p(genus | ¬scientificName, soundexScientificName) conditional probability
  public double inf_t_ff$genus$t; // p(genus | ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_ff$genus$t; // p(¬genus | ¬scientificName, ¬soundexScientificName) =  1 - p(genus | ¬scientificName, ¬soundexScientificName) conditional probability
  public double derived_t_tt$genus$t; // p(genus | soundexScientificName, scientificName) = p(genus | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_tt$genus$t; // p(¬genus | soundexScientificName, scientificName) = p(¬genus | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_tf$genus$t; // p(genus | soundexScientificName, ¬scientificName) = p(genus | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_tf$genus$t; // p(¬genus | soundexScientificName, ¬scientificName) = p(¬genus | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_t_ft$genus$t; // p(genus | ¬soundexScientificName, scientificName) = p(genus | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_ft$genus$t; // p(¬genus | ¬soundexScientificName, scientificName) = p(¬genus | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_ff$genus$t; // p(genus | ¬soundexScientificName, ¬scientificName) = p(genus | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_ff$genus$t; // p(¬genus | ¬soundexScientificName, ¬scientificName) = p(¬genus | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double inf_t_tt$genus$f; // p(genus | scientificName, soundexScientificName) conditional probability
  public double inf_f_tt$genus$f; // p(¬genus | scientificName, soundexScientificName) =  1 - p(genus | scientificName, soundexScientificName) conditional probability
  public double inf_t_tf$genus$f; // p(genus | scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_tf$genus$f; // p(¬genus | scientificName, ¬soundexScientificName) =  1 - p(genus | scientificName, ¬soundexScientificName) conditional probability
  public double inf_t_ft$genus$f; // p(genus | ¬scientificName, soundexScientificName) conditional probability
  public double inf_f_ft$genus$f; // p(¬genus | ¬scientificName, soundexScientificName) =  1 - p(genus | ¬scientificName, soundexScientificName) conditional probability
  public double inf_t_ff$genus$f; // p(genus | ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_ff$genus$f; // p(¬genus | ¬scientificName, ¬soundexScientificName) =  1 - p(genus | ¬scientificName, ¬soundexScientificName) conditional probability
  public double derived_t_tt$genus$f; // p(genus | soundexScientificName, scientificName) = p(genus | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_tt$genus$f; // p(¬genus | soundexScientificName, scientificName) = p(¬genus | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_tf$genus$f; // p(genus | soundexScientificName, ¬scientificName) = p(genus | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_tf$genus$f; // p(¬genus | soundexScientificName, ¬scientificName) = p(¬genus | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_t_ft$genus$f; // p(genus | ¬soundexScientificName, scientificName) = p(genus | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_ft$genus$f; // p(¬genus | ¬soundexScientificName, scientificName) = p(¬genus | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_ff$genus$f; // p(genus | ¬soundexScientificName, ¬scientificName) = p(genus | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_ff$genus$f; // p(¬genus | ¬soundexScientificName, ¬scientificName) = p(¬genus | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double inf_t_t$family$t; // p(family | genus) conditional probability
  public double inf_f_t$family$t; // p(¬family | genus) =  1 - p(family | genus) conditional probability
  public double inf_t_f$family$t; // p(family | ¬genus) conditional probability
  public double inf_f_f$family$t; // p(¬family | ¬genus) =  1 - p(family | ¬genus) conditional probability
  public double inf_t_t$family$f; // p(family | genus) conditional probability
  public double inf_f_t$family$f; // p(¬family | genus) =  1 - p(family | genus) conditional probability
  public double inf_t_f$family$f; // p(family | ¬genus) conditional probability
  public double inf_f_f$family$f; // p(¬family | ¬genus) =  1 - p(family | ¬genus) conditional probability
  public double inf_t_t$order$t; // p(order | family) conditional probability
  public double inf_f_t$order$t; // p(¬order | family) =  1 - p(order | family) conditional probability
  public double inf_t_f$order$t; // p(order | ¬family) conditional probability
  public double inf_f_f$order$t; // p(¬order | ¬family) =  1 - p(order | ¬family) conditional probability
  public double inf_t_t$order$f; // p(order | family) conditional probability
  public double inf_f_t$order$f; // p(¬order | family) =  1 - p(order | family) conditional probability
  public double inf_t_f$order$f; // p(order | ¬family) conditional probability
  public double inf_f_f$order$f; // p(¬order | ¬family) =  1 - p(order | ¬family) conditional probability
  public double inf_t_t$class_$t; // p(class | order) conditional probability
  public double inf_f_t$class_$t; // p(¬class | order) =  1 - p(class | order) conditional probability
  public double inf_t_f$class_$t; // p(class | ¬order) conditional probability
  public double inf_f_f$class_$t; // p(¬class | ¬order) =  1 - p(class | ¬order) conditional probability
  public double inf_t_t$class_$f; // p(class | order) conditional probability
  public double inf_f_t$class_$f; // p(¬class | order) =  1 - p(class | order) conditional probability
  public double inf_t_f$class_$f; // p(class | ¬order) conditional probability
  public double inf_f_f$class_$f; // p(¬class | ¬order) =  1 - p(class | ¬order) conditional probability
  public double inf_t_t$phylum$t; // p(phylum | class) conditional probability
  public double inf_f_t$phylum$t; // p(¬phylum | class) =  1 - p(phylum | class) conditional probability
  public double inf_t_f$phylum$t; // p(phylum | ¬class) conditional probability
  public double inf_f_f$phylum$t; // p(¬phylum | ¬class) =  1 - p(phylum | ¬class) conditional probability
  public double inf_t_t$phylum$f; // p(phylum | class) conditional probability
  public double inf_f_t$phylum$f; // p(¬phylum | class) =  1 - p(phylum | class) conditional probability
  public double inf_t_f$phylum$f; // p(phylum | ¬class) conditional probability
  public double inf_f_f$phylum$f; // p(¬phylum | ¬class) =  1 - p(phylum | ¬class) conditional probability
  public double inf_t_t$kingdom$t; // p(kingdom | phylum) conditional probability
  public double inf_f_t$kingdom$t; // p(¬kingdom | phylum) =  1 - p(kingdom | phylum) conditional probability
  public double inf_t_f$kingdom$t; // p(kingdom | ¬phylum) conditional probability
  public double inf_f_f$kingdom$t; // p(¬kingdom | ¬phylum) =  1 - p(kingdom | ¬phylum) conditional probability
  public double inf_t_t$kingdom$f; // p(kingdom | phylum) conditional probability
  public double inf_f_t$kingdom$f; // p(¬kingdom | phylum) =  1 - p(kingdom | phylum) conditional probability
  public double inf_t_f$kingdom$f; // p(kingdom | ¬phylum) conditional probability
  public double inf_f_f$kingdom$f; // p(¬kingdom | ¬phylum) =  1 - p(kingdom | ¬phylum) conditional probability

  public SimpleLinnaeanParameters_FT() {
  }

  @Override
  public void load(double[] vector) {
    this.prior_t$taxonId = vector[0];
    this.inf_t_t$taxonRank$t = vector[1];
    this.inf_t_t$taxonRank$f = vector[2];
    this.inf_t_f$taxonRank$t = vector[3];
    this.inf_t_f$taxonRank$f = vector[4];
    this.inf_t_t$scientificNameAuthorship$t = vector[5];
    this.inf_t_t$scientificNameAuthorship$f = vector[6];
    this.inf_t_f$scientificNameAuthorship$t = vector[7];
    this.inf_t_f$scientificNameAuthorship$f = vector[8];
    this.inf_t_t$scientificName$t = vector[9];
    this.inf_t_t$scientificName$f = vector[10];
    this.inf_t_f$scientificName$t = vector[11];
    this.inf_t_f$scientificName$f = vector[12];
    this.inf_t_t$soundexScientificName$t = vector[13];
    this.inf_t_t$soundexScientificName$f = vector[14];
    this.inf_t_f$soundexScientificName$t = vector[15];
    this.inf_t_f$soundexScientificName$f = vector[16];
    this.inf_t_tt$genus$t = vector[17];
    this.inf_t_tt$genus$f = vector[18];
    this.inf_t_tf$genus$t = vector[19];
    this.inf_t_tf$genus$f = vector[20];
    this.inf_t_ft$genus$t = vector[21];
    this.inf_t_ft$genus$f = vector[22];
    this.inf_t_ff$genus$t = vector[23];
    this.inf_t_ff$genus$f = vector[24];
    this.inf_t_t$family$t = vector[25];
    this.inf_t_t$family$f = vector[26];
    this.inf_t_f$family$t = vector[27];
    this.inf_t_f$family$f = vector[28];
    this.inf_t_t$order$t = vector[29];
    this.inf_t_t$order$f = vector[30];
    this.inf_t_f$order$t = vector[31];
    this.inf_t_f$order$f = vector[32];
    this.inf_t_t$class_$t = vector[33];
    this.inf_t_t$class_$f = vector[34];
    this.inf_t_f$class_$t = vector[35];
    this.inf_t_f$class_$f = vector[36];
    this.inf_t_t$phylum$t = vector[37];
    this.inf_t_t$phylum$f = vector[38];
    this.inf_t_f$phylum$t = vector[39];
    this.inf_t_f$phylum$f = vector[40];
    this.inf_t_t$kingdom$t = vector[41];
    this.inf_t_t$kingdom$f = vector[42];
    this.inf_t_f$kingdom$t = vector[43];
    this.inf_t_f$kingdom$f = vector[44];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[45];

    vector[0] = this.prior_t$taxonId;
    vector[1] = this.inf_t_t$taxonRank$t;
    vector[2] = this.inf_t_t$taxonRank$f;
    vector[3] = this.inf_t_f$taxonRank$t;
    vector[4] = this.inf_t_f$taxonRank$f;
    vector[5] = this.inf_t_t$scientificNameAuthorship$t;
    vector[6] = this.inf_t_t$scientificNameAuthorship$f;
    vector[7] = this.inf_t_f$scientificNameAuthorship$t;
    vector[8] = this.inf_t_f$scientificNameAuthorship$f;
    vector[9] = this.inf_t_t$scientificName$t;
    vector[10] = this.inf_t_t$scientificName$f;
    vector[11] = this.inf_t_f$scientificName$t;
    vector[12] = this.inf_t_f$scientificName$f;
    vector[13] = this.inf_t_t$soundexScientificName$t;
    vector[14] = this.inf_t_t$soundexScientificName$f;
    vector[15] = this.inf_t_f$soundexScientificName$t;
    vector[16] = this.inf_t_f$soundexScientificName$f;
    vector[17] = this.inf_t_tt$genus$t;
    vector[18] = this.inf_t_tt$genus$f;
    vector[19] = this.inf_t_tf$genus$t;
    vector[20] = this.inf_t_tf$genus$f;
    vector[21] = this.inf_t_ft$genus$t;
    vector[22] = this.inf_t_ft$genus$f;
    vector[23] = this.inf_t_ff$genus$t;
    vector[24] = this.inf_t_ff$genus$f;
    vector[25] = this.inf_t_t$family$t;
    vector[26] = this.inf_t_t$family$f;
    vector[27] = this.inf_t_f$family$t;
    vector[28] = this.inf_t_f$family$f;
    vector[29] = this.inf_t_t$order$t;
    vector[30] = this.inf_t_t$order$f;
    vector[31] = this.inf_t_f$order$t;
    vector[32] = this.inf_t_f$order$f;
    vector[33] = this.inf_t_t$class_$t;
    vector[34] = this.inf_t_t$class_$f;
    vector[35] = this.inf_t_f$class_$t;
    vector[36] = this.inf_t_f$class_$f;
    vector[37] = this.inf_t_t$phylum$t;
    vector[38] = this.inf_t_t$phylum$f;
    vector[39] = this.inf_t_f$phylum$t;
    vector[40] = this.inf_t_f$phylum$f;
    vector[41] = this.inf_t_t$kingdom$t;
    vector[42] = this.inf_t_t$kingdom$f;
    vector[43] = this.inf_t_f$kingdom$t;
    vector[44] = this.inf_t_f$kingdom$f;
    return vector;
  }

  public void build() {
    this.prior_f$taxonId = 1.0 - this.prior_t$taxonId;
    this.inf_f_t$taxonRank$t = 1.0 - this.inf_t_t$taxonRank$t;
    this.inf_f_f$taxonRank$t = 1.0 - this.inf_t_f$taxonRank$t;
    this.inf_f_t$taxonRank$f = 1.0 - this.inf_t_t$taxonRank$f;
    this.inf_f_f$taxonRank$f = 1.0 - this.inf_t_f$taxonRank$f;
    this.inf_f_t$scientificNameAuthorship$t = 1.0 - this.inf_t_t$scientificNameAuthorship$t;
    this.inf_f_f$scientificNameAuthorship$t = 1.0 - this.inf_t_f$scientificNameAuthorship$t;
    this.inf_f_t$scientificNameAuthorship$f = 1.0 - this.inf_t_t$scientificNameAuthorship$f;
    this.inf_f_f$scientificNameAuthorship$f = 1.0 - this.inf_t_f$scientificNameAuthorship$f;
    this.inf_f_t$scientificName$t = 1.0 - this.inf_t_t$scientificName$t;
    this.inf_f_f$scientificName$t = 1.0 - this.inf_t_f$scientificName$t;
    this.inf_f_t$scientificName$f = 1.0 - this.inf_t_t$scientificName$f;
    this.inf_f_f$scientificName$f = 1.0 - this.inf_t_f$scientificName$f;
    this.inf_f_t$soundexScientificName$t = 1.0 - this.inf_t_t$soundexScientificName$t;
    this.inf_f_f$soundexScientificName$t = 1.0 - this.inf_t_f$soundexScientificName$t;
    this.inf_f_t$soundexScientificName$f = 1.0 - this.inf_t_t$soundexScientificName$f;
    this.inf_f_f$soundexScientificName$f = 1.0 - this.inf_t_f$soundexScientificName$f;
    this.inf_f_tt$genus$t = 1.0 - this.inf_t_tt$genus$t;
    this.inf_f_tf$genus$t = 1.0 - this.inf_t_tf$genus$t;
    this.inf_f_ft$genus$t = 1.0 - this.inf_t_ft$genus$t;
    this.inf_f_ff$genus$t = 1.0 - this.inf_t_ff$genus$t;
    this.derived_t_tt$genus$t = this.inf_t_tt$genus$t * this.inf_t_t$soundexScientificName$t;
    this.derived_f_tt$genus$t = this.inf_f_tt$genus$t * this.inf_t_t$soundexScientificName$t;
    this.derived_t_tf$genus$t = this.inf_t_ft$genus$t * this.inf_t_f$soundexScientificName$t;
    this.derived_f_tf$genus$t = this.inf_f_ft$genus$t * this.inf_t_f$soundexScientificName$t;
    this.derived_t_ft$genus$t = this.inf_t_tf$genus$t * this.inf_f_t$soundexScientificName$t;
    this.derived_f_ft$genus$t = this.inf_f_tf$genus$t * this.inf_f_t$soundexScientificName$t;
    this.derived_t_ff$genus$t = this.inf_t_ff$genus$t * this.inf_f_f$soundexScientificName$t;
    this.derived_f_ff$genus$t = this.inf_f_ff$genus$t * this.inf_f_f$soundexScientificName$t;
    this.inf_f_tt$genus$f = 1.0 - this.inf_t_tt$genus$f;
    this.inf_f_tf$genus$f = 1.0 - this.inf_t_tf$genus$f;
    this.inf_f_ft$genus$f = 1.0 - this.inf_t_ft$genus$f;
    this.inf_f_ff$genus$f = 1.0 - this.inf_t_ff$genus$f;
    this.derived_t_tt$genus$f = this.inf_t_tt$genus$f * this.inf_t_t$soundexScientificName$f;
    this.derived_f_tt$genus$f = this.inf_f_tt$genus$f * this.inf_t_t$soundexScientificName$f;
    this.derived_t_tf$genus$f = this.inf_t_ft$genus$f * this.inf_t_f$soundexScientificName$f;
    this.derived_f_tf$genus$f = this.inf_f_ft$genus$f * this.inf_t_f$soundexScientificName$f;
    this.derived_t_ft$genus$f = this.inf_t_tf$genus$f * this.inf_f_t$soundexScientificName$f;
    this.derived_f_ft$genus$f = this.inf_f_tf$genus$f * this.inf_f_t$soundexScientificName$f;
    this.derived_t_ff$genus$f = this.inf_t_ff$genus$f * this.inf_f_f$soundexScientificName$f;
    this.derived_f_ff$genus$f = this.inf_f_ff$genus$f * this.inf_f_f$soundexScientificName$f;
    this.inf_f_t$family$t = 1.0 - this.inf_t_t$family$t;
    this.inf_f_f$family$t = 1.0 - this.inf_t_f$family$t;
    this.inf_f_t$family$f = 1.0 - this.inf_t_t$family$f;
    this.inf_f_f$family$f = 1.0 - this.inf_t_f$family$f;
    this.inf_f_t$order$t = 1.0 - this.inf_t_t$order$t;
    this.inf_f_f$order$t = 1.0 - this.inf_t_f$order$t;
    this.inf_f_t$order$f = 1.0 - this.inf_t_t$order$f;
    this.inf_f_f$order$f = 1.0 - this.inf_t_f$order$f;
    this.inf_f_t$class_$t = 1.0 - this.inf_t_t$class_$t;
    this.inf_f_f$class_$t = 1.0 - this.inf_t_f$class_$t;
    this.inf_f_t$class_$f = 1.0 - this.inf_t_t$class_$f;
    this.inf_f_f$class_$f = 1.0 - this.inf_t_f$class_$f;
    this.inf_f_t$phylum$t = 1.0 - this.inf_t_t$phylum$t;
    this.inf_f_f$phylum$t = 1.0 - this.inf_t_f$phylum$t;
    this.inf_f_t$phylum$f = 1.0 - this.inf_t_t$phylum$f;
    this.inf_f_f$phylum$f = 1.0 - this.inf_t_f$phylum$f;
    this.inf_f_t$kingdom$t = 1.0 - this.inf_t_t$kingdom$t;
    this.inf_f_f$kingdom$t = 1.0 - this.inf_t_f$kingdom$t;
    this.inf_f_t$kingdom$f = 1.0 - this.inf_t_t$kingdom$f;
    this.inf_f_f$kingdom$f = 1.0 - this.inf_t_f$kingdom$f;
  }

}