package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;

public class SimpleLinnaeanParameters_TF implements Parameters {

  public final static String SIGNATURE = "TF";

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
  public double inf_t_t$specificEpithet$t; // p(specificEpithet | taxonID) conditional probability
  public double inf_f_t$specificEpithet$t; // p(¬specificEpithet | taxonID) =  1 - p(specificEpithet | taxonID) conditional probability
  public double inf_t_f$specificEpithet$t; // p(specificEpithet | ¬taxonID) conditional probability
  public double inf_f_f$specificEpithet$t; // p(¬specificEpithet | ¬taxonID) =  1 - p(specificEpithet | ¬taxonID) conditional probability
  public double inf_t_t$specificEpithet$f; // p(specificEpithet | taxonID) conditional probability
  public double inf_f_t$specificEpithet$f; // p(¬specificEpithet | taxonID) =  1 - p(specificEpithet | taxonID) conditional probability
  public double inf_t_f$specificEpithet$f; // p(specificEpithet | ¬taxonID) conditional probability
  public double inf_f_f$specificEpithet$f; // p(¬specificEpithet | ¬taxonID) =  1 - p(specificEpithet | ¬taxonID) conditional probability
  public double inf_t_tt$scientificName$t; // p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_f_tt$scientificName$t; // p(¬scientificName | taxonID, specificEpithet) =  1 - p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_t_tf$scientificName$t; // p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_f_tf$scientificName$t; // p(¬scientificName | taxonID, ¬specificEpithet) =  1 - p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_t_ft$scientificName$t; // p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_f_ft$scientificName$t; // p(¬scientificName | ¬taxonID, specificEpithet) =  1 - p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_t_ff$scientificName$t; // p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double inf_f_ff$scientificName$t; // p(¬scientificName | ¬taxonID, ¬specificEpithet) =  1 - p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double derived_t_tt$scientificName$t; // p(scientificName | specificEpithet, taxonID) = p(scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_f_tt$scientificName$t; // p(¬scientificName | specificEpithet, taxonID) = p(¬scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_t_tf$scientificName$t; // p(scientificName | specificEpithet, ¬taxonID) = p(scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_f_tf$scientificName$t; // p(¬scientificName | specificEpithet, ¬taxonID) = p(¬scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_t_ft$scientificName$t; // p(scientificName | ¬specificEpithet, taxonID) = p(scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_f_ft$scientificName$t; // p(¬scientificName | ¬specificEpithet, taxonID) = p(¬scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_t_ff$scientificName$t; // p(scientificName | ¬specificEpithet, ¬taxonID) = p(scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_f_ff$scientificName$t; // p(¬scientificName | ¬specificEpithet, ¬taxonID) = p(¬scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double inf_t_tt$scientificName$f; // p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_f_tt$scientificName$f; // p(¬scientificName | taxonID, specificEpithet) =  1 - p(scientificName | taxonID, specificEpithet) conditional probability
  public double inf_t_tf$scientificName$f; // p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_f_tf$scientificName$f; // p(¬scientificName | taxonID, ¬specificEpithet) =  1 - p(scientificName | taxonID, ¬specificEpithet) conditional probability
  public double inf_t_ft$scientificName$f; // p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_f_ft$scientificName$f; // p(¬scientificName | ¬taxonID, specificEpithet) =  1 - p(scientificName | ¬taxonID, specificEpithet) conditional probability
  public double inf_t_ff$scientificName$f; // p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double inf_f_ff$scientificName$f; // p(¬scientificName | ¬taxonID, ¬specificEpithet) =  1 - p(scientificName | ¬taxonID, ¬specificEpithet) conditional probability
  public double derived_t_tt$scientificName$f; // p(scientificName | specificEpithet, taxonID) = p(scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_f_tt$scientificName$f; // p(¬scientificName | specificEpithet, taxonID) = p(¬scientificName | taxonID, specificEpithet).p(specificEpithet | taxonID)  derived conditional probability
  public double derived_t_tf$scientificName$f; // p(scientificName | specificEpithet, ¬taxonID) = p(scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_f_tf$scientificName$f; // p(¬scientificName | specificEpithet, ¬taxonID) = p(¬scientificName | ¬taxonID, specificEpithet).p(specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_t_ft$scientificName$f; // p(scientificName | ¬specificEpithet, taxonID) = p(scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_f_ft$scientificName$f; // p(¬scientificName | ¬specificEpithet, taxonID) = p(¬scientificName | taxonID, ¬specificEpithet).p(¬specificEpithet | taxonID)  derived conditional probability
  public double derived_t_ff$scientificName$f; // p(scientificName | ¬specificEpithet, ¬taxonID) = p(scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double derived_f_ff$scientificName$f; // p(¬scientificName | ¬specificEpithet, ¬taxonID) = p(¬scientificName | ¬taxonID, ¬specificEpithet).p(¬specificEpithet | ¬taxonID)  derived conditional probability
  public double inf_t_t$soundexScientificName$t; // p(soundexScientificName | scientificName) conditional probability
  public double inf_f_t$soundexScientificName$t; // p(¬soundexScientificName | scientificName) =  1 - p(soundexScientificName | scientificName) conditional probability
  public double inf_t_f$soundexScientificName$t; // p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_f_f$soundexScientificName$t; // p(¬soundexScientificName | ¬scientificName) =  1 - p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_t_t$soundexScientificName$f; // p(soundexScientificName | scientificName) conditional probability
  public double inf_f_t$soundexScientificName$f; // p(¬soundexScientificName | scientificName) =  1 - p(soundexScientificName | scientificName) conditional probability
  public double inf_t_f$soundexScientificName$f; // p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_f_f$soundexScientificName$f; // p(¬soundexScientificName | ¬scientificName) =  1 - p(soundexScientificName | ¬scientificName) conditional probability
  public double inf_t_tt$family$t; // p(family | scientificName, soundexScientificName) conditional probability
  public double inf_f_tt$family$t; // p(¬family | scientificName, soundexScientificName) =  1 - p(family | scientificName, soundexScientificName) conditional probability
  public double inf_t_tf$family$t; // p(family | scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_tf$family$t; // p(¬family | scientificName, ¬soundexScientificName) =  1 - p(family | scientificName, ¬soundexScientificName) conditional probability
  public double inf_t_ft$family$t; // p(family | ¬scientificName, soundexScientificName) conditional probability
  public double inf_f_ft$family$t; // p(¬family | ¬scientificName, soundexScientificName) =  1 - p(family | ¬scientificName, soundexScientificName) conditional probability
  public double inf_t_ff$family$t; // p(family | ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_ff$family$t; // p(¬family | ¬scientificName, ¬soundexScientificName) =  1 - p(family | ¬scientificName, ¬soundexScientificName) conditional probability
  public double derived_t_tt$family$t; // p(family | soundexScientificName, scientificName) = p(family | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_tt$family$t; // p(¬family | soundexScientificName, scientificName) = p(¬family | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_tf$family$t; // p(family | soundexScientificName, ¬scientificName) = p(family | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_tf$family$t; // p(¬family | soundexScientificName, ¬scientificName) = p(¬family | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_t_ft$family$t; // p(family | ¬soundexScientificName, scientificName) = p(family | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_ft$family$t; // p(¬family | ¬soundexScientificName, scientificName) = p(¬family | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_ff$family$t; // p(family | ¬soundexScientificName, ¬scientificName) = p(family | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_ff$family$t; // p(¬family | ¬soundexScientificName, ¬scientificName) = p(¬family | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double inf_t_tt$family$f; // p(family | scientificName, soundexScientificName) conditional probability
  public double inf_f_tt$family$f; // p(¬family | scientificName, soundexScientificName) =  1 - p(family | scientificName, soundexScientificName) conditional probability
  public double inf_t_tf$family$f; // p(family | scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_tf$family$f; // p(¬family | scientificName, ¬soundexScientificName) =  1 - p(family | scientificName, ¬soundexScientificName) conditional probability
  public double inf_t_ft$family$f; // p(family | ¬scientificName, soundexScientificName) conditional probability
  public double inf_f_ft$family$f; // p(¬family | ¬scientificName, soundexScientificName) =  1 - p(family | ¬scientificName, soundexScientificName) conditional probability
  public double inf_t_ff$family$f; // p(family | ¬scientificName, ¬soundexScientificName) conditional probability
  public double inf_f_ff$family$f; // p(¬family | ¬scientificName, ¬soundexScientificName) =  1 - p(family | ¬scientificName, ¬soundexScientificName) conditional probability
  public double derived_t_tt$family$f; // p(family | soundexScientificName, scientificName) = p(family | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_tt$family$f; // p(¬family | soundexScientificName, scientificName) = p(¬family | scientificName, soundexScientificName).p(soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_tf$family$f; // p(family | soundexScientificName, ¬scientificName) = p(family | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_tf$family$f; // p(¬family | soundexScientificName, ¬scientificName) = p(¬family | ¬scientificName, soundexScientificName).p(soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_t_ft$family$f; // p(family | ¬soundexScientificName, scientificName) = p(family | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_f_ft$family$f; // p(¬family | ¬soundexScientificName, scientificName) = p(¬family | scientificName, ¬soundexScientificName).p(¬soundexScientificName | scientificName)  derived conditional probability
  public double derived_t_ff$family$f; // p(family | ¬soundexScientificName, ¬scientificName) = p(family | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
  public double derived_f_ff$family$f; // p(¬family | ¬soundexScientificName, ¬scientificName) = p(¬family | ¬scientificName, ¬soundexScientificName).p(¬soundexScientificName | ¬scientificName)  derived conditional probability
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

  public SimpleLinnaeanParameters_TF() {
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
    this.inf_t_t$specificEpithet$t = vector[9];
    this.inf_t_t$specificEpithet$f = vector[10];
    this.inf_t_f$specificEpithet$t = vector[11];
    this.inf_t_f$specificEpithet$f = vector[12];
    this.inf_t_tt$scientificName$t = vector[13];
    this.inf_t_tt$scientificName$f = vector[14];
    this.inf_t_tf$scientificName$t = vector[15];
    this.inf_t_tf$scientificName$f = vector[16];
    this.inf_t_ft$scientificName$t = vector[17];
    this.inf_t_ft$scientificName$f = vector[18];
    this.inf_t_ff$scientificName$t = vector[19];
    this.inf_t_ff$scientificName$f = vector[20];
    this.inf_t_t$soundexScientificName$t = vector[21];
    this.inf_t_t$soundexScientificName$f = vector[22];
    this.inf_t_f$soundexScientificName$t = vector[23];
    this.inf_t_f$soundexScientificName$f = vector[24];
    this.inf_t_tt$family$t = vector[25];
    this.inf_t_tt$family$f = vector[26];
    this.inf_t_tf$family$t = vector[27];
    this.inf_t_tf$family$f = vector[28];
    this.inf_t_ft$family$t = vector[29];
    this.inf_t_ft$family$f = vector[30];
    this.inf_t_ff$family$t = vector[31];
    this.inf_t_ff$family$f = vector[32];
    this.inf_t_t$order$t = vector[33];
    this.inf_t_t$order$f = vector[34];
    this.inf_t_f$order$t = vector[35];
    this.inf_t_f$order$f = vector[36];
    this.inf_t_t$class_$t = vector[37];
    this.inf_t_t$class_$f = vector[38];
    this.inf_t_f$class_$t = vector[39];
    this.inf_t_f$class_$f = vector[40];
    this.inf_t_t$phylum$t = vector[41];
    this.inf_t_t$phylum$f = vector[42];
    this.inf_t_f$phylum$t = vector[43];
    this.inf_t_f$phylum$f = vector[44];
    this.inf_t_t$kingdom$t = vector[45];
    this.inf_t_t$kingdom$f = vector[46];
    this.inf_t_f$kingdom$t = vector[47];
    this.inf_t_f$kingdom$f = vector[48];
    this.build();
  }

  @Override
  public double[] store() {
    double[] vector = new double[49];

    vector[0] = this.prior_t$taxonId;
    vector[1] = this.inf_t_t$taxonRank$t;
    vector[2] = this.inf_t_t$taxonRank$f;
    vector[3] = this.inf_t_f$taxonRank$t;
    vector[4] = this.inf_t_f$taxonRank$f;
    vector[5] = this.inf_t_t$scientificNameAuthorship$t;
    vector[6] = this.inf_t_t$scientificNameAuthorship$f;
    vector[7] = this.inf_t_f$scientificNameAuthorship$t;
    vector[8] = this.inf_t_f$scientificNameAuthorship$f;
    vector[9] = this.inf_t_t$specificEpithet$t;
    vector[10] = this.inf_t_t$specificEpithet$f;
    vector[11] = this.inf_t_f$specificEpithet$t;
    vector[12] = this.inf_t_f$specificEpithet$f;
    vector[13] = this.inf_t_tt$scientificName$t;
    vector[14] = this.inf_t_tt$scientificName$f;
    vector[15] = this.inf_t_tf$scientificName$t;
    vector[16] = this.inf_t_tf$scientificName$f;
    vector[17] = this.inf_t_ft$scientificName$t;
    vector[18] = this.inf_t_ft$scientificName$f;
    vector[19] = this.inf_t_ff$scientificName$t;
    vector[20] = this.inf_t_ff$scientificName$f;
    vector[21] = this.inf_t_t$soundexScientificName$t;
    vector[22] = this.inf_t_t$soundexScientificName$f;
    vector[23] = this.inf_t_f$soundexScientificName$t;
    vector[24] = this.inf_t_f$soundexScientificName$f;
    vector[25] = this.inf_t_tt$family$t;
    vector[26] = this.inf_t_tt$family$f;
    vector[27] = this.inf_t_tf$family$t;
    vector[28] = this.inf_t_tf$family$f;
    vector[29] = this.inf_t_ft$family$t;
    vector[30] = this.inf_t_ft$family$f;
    vector[31] = this.inf_t_ff$family$t;
    vector[32] = this.inf_t_ff$family$f;
    vector[33] = this.inf_t_t$order$t;
    vector[34] = this.inf_t_t$order$f;
    vector[35] = this.inf_t_f$order$t;
    vector[36] = this.inf_t_f$order$f;
    vector[37] = this.inf_t_t$class_$t;
    vector[38] = this.inf_t_t$class_$f;
    vector[39] = this.inf_t_f$class_$t;
    vector[40] = this.inf_t_f$class_$f;
    vector[41] = this.inf_t_t$phylum$t;
    vector[42] = this.inf_t_t$phylum$f;
    vector[43] = this.inf_t_f$phylum$t;
    vector[44] = this.inf_t_f$phylum$f;
    vector[45] = this.inf_t_t$kingdom$t;
    vector[46] = this.inf_t_t$kingdom$f;
    vector[47] = this.inf_t_f$kingdom$t;
    vector[48] = this.inf_t_f$kingdom$f;
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
    this.inf_f_t$specificEpithet$t = 1.0 - this.inf_t_t$specificEpithet$t;
    this.inf_f_f$specificEpithet$t = 1.0 - this.inf_t_f$specificEpithet$t;
    this.inf_f_t$specificEpithet$f = 1.0 - this.inf_t_t$specificEpithet$f;
    this.inf_f_f$specificEpithet$f = 1.0 - this.inf_t_f$specificEpithet$f;
    this.inf_f_tt$scientificName$t = 1.0 - this.inf_t_tt$scientificName$t;
    this.inf_f_tf$scientificName$t = 1.0 - this.inf_t_tf$scientificName$t;
    this.inf_f_ft$scientificName$t = 1.0 - this.inf_t_ft$scientificName$t;
    this.inf_f_ff$scientificName$t = 1.0 - this.inf_t_ff$scientificName$t;
    this.derived_t_tt$scientificName$t = this.inf_t_tt$scientificName$t * this.inf_t_t$specificEpithet$t;
    this.derived_f_tt$scientificName$t = this.inf_f_tt$scientificName$t * this.inf_t_t$specificEpithet$t;
    this.derived_t_tf$scientificName$t = this.inf_t_ft$scientificName$t * this.inf_t_f$specificEpithet$t;
    this.derived_f_tf$scientificName$t = this.inf_f_ft$scientificName$t * this.inf_t_f$specificEpithet$t;
    this.derived_t_ft$scientificName$t = this.inf_t_tf$scientificName$t * this.inf_f_t$specificEpithet$t;
    this.derived_f_ft$scientificName$t = this.inf_f_tf$scientificName$t * this.inf_f_t$specificEpithet$t;
    this.derived_t_ff$scientificName$t = this.inf_t_ff$scientificName$t * this.inf_f_f$specificEpithet$t;
    this.derived_f_ff$scientificName$t = this.inf_f_ff$scientificName$t * this.inf_f_f$specificEpithet$t;
    this.inf_f_tt$scientificName$f = 1.0 - this.inf_t_tt$scientificName$f;
    this.inf_f_tf$scientificName$f = 1.0 - this.inf_t_tf$scientificName$f;
    this.inf_f_ft$scientificName$f = 1.0 - this.inf_t_ft$scientificName$f;
    this.inf_f_ff$scientificName$f = 1.0 - this.inf_t_ff$scientificName$f;
    this.derived_t_tt$scientificName$f = this.inf_t_tt$scientificName$f * this.inf_t_t$specificEpithet$f;
    this.derived_f_tt$scientificName$f = this.inf_f_tt$scientificName$f * this.inf_t_t$specificEpithet$f;
    this.derived_t_tf$scientificName$f = this.inf_t_ft$scientificName$f * this.inf_t_f$specificEpithet$f;
    this.derived_f_tf$scientificName$f = this.inf_f_ft$scientificName$f * this.inf_t_f$specificEpithet$f;
    this.derived_t_ft$scientificName$f = this.inf_t_tf$scientificName$f * this.inf_f_t$specificEpithet$f;
    this.derived_f_ft$scientificName$f = this.inf_f_tf$scientificName$f * this.inf_f_t$specificEpithet$f;
    this.derived_t_ff$scientificName$f = this.inf_t_ff$scientificName$f * this.inf_f_f$specificEpithet$f;
    this.derived_f_ff$scientificName$f = this.inf_f_ff$scientificName$f * this.inf_f_f$specificEpithet$f;
    this.inf_f_t$soundexScientificName$t = 1.0 - this.inf_t_t$soundexScientificName$t;
    this.inf_f_f$soundexScientificName$t = 1.0 - this.inf_t_f$soundexScientificName$t;
    this.inf_f_t$soundexScientificName$f = 1.0 - this.inf_t_t$soundexScientificName$f;
    this.inf_f_f$soundexScientificName$f = 1.0 - this.inf_t_f$soundexScientificName$f;
    this.inf_f_tt$family$t = 1.0 - this.inf_t_tt$family$t;
    this.inf_f_tf$family$t = 1.0 - this.inf_t_tf$family$t;
    this.inf_f_ft$family$t = 1.0 - this.inf_t_ft$family$t;
    this.inf_f_ff$family$t = 1.0 - this.inf_t_ff$family$t;
    this.derived_t_tt$family$t = this.inf_t_tt$family$t * this.inf_t_t$soundexScientificName$t;
    this.derived_f_tt$family$t = this.inf_f_tt$family$t * this.inf_t_t$soundexScientificName$t;
    this.derived_t_tf$family$t = this.inf_t_ft$family$t * this.inf_t_f$soundexScientificName$t;
    this.derived_f_tf$family$t = this.inf_f_ft$family$t * this.inf_t_f$soundexScientificName$t;
    this.derived_t_ft$family$t = this.inf_t_tf$family$t * this.inf_f_t$soundexScientificName$t;
    this.derived_f_ft$family$t = this.inf_f_tf$family$t * this.inf_f_t$soundexScientificName$t;
    this.derived_t_ff$family$t = this.inf_t_ff$family$t * this.inf_f_f$soundexScientificName$t;
    this.derived_f_ff$family$t = this.inf_f_ff$family$t * this.inf_f_f$soundexScientificName$t;
    this.inf_f_tt$family$f = 1.0 - this.inf_t_tt$family$f;
    this.inf_f_tf$family$f = 1.0 - this.inf_t_tf$family$f;
    this.inf_f_ft$family$f = 1.0 - this.inf_t_ft$family$f;
    this.inf_f_ff$family$f = 1.0 - this.inf_t_ff$family$f;
    this.derived_t_tt$family$f = this.inf_t_tt$family$f * this.inf_t_t$soundexScientificName$f;
    this.derived_f_tt$family$f = this.inf_f_tt$family$f * this.inf_t_t$soundexScientificName$f;
    this.derived_t_tf$family$f = this.inf_t_ft$family$f * this.inf_t_f$soundexScientificName$f;
    this.derived_f_tf$family$f = this.inf_f_ft$family$f * this.inf_t_f$soundexScientificName$f;
    this.derived_t_ft$family$f = this.inf_t_tf$family$f * this.inf_f_t$soundexScientificName$f;
    this.derived_f_ft$family$f = this.inf_f_tf$family$f * this.inf_f_t$soundexScientificName$f;
    this.derived_t_ff$family$f = this.inf_t_ff$family$f * this.inf_f_f$soundexScientificName$f;
    this.derived_f_ff$family$f = this.inf_f_ff$family$f * this.inf_f_f$soundexScientificName$f;
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