package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.Trace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;


public class SimpleLinnaeanInferencer implements Inferencer<SimpleLinnaeanClassification> {
  private Map<String, Inferencer<SimpleLinnaeanClassification>> subInferencers;

  // Assumed to be stateless
  private static final Inferencer<SimpleLinnaeanClassification>[] INFERENCERS = new Inferencer[] {
    new SimpleLinnaeanInferencer_TT(),
    new SimpleLinnaeanInferencer_TF(),
    new SimpleLinnaeanInferencer_FT(),
    new SimpleLinnaeanInferencer_FF()
  };

  public SimpleLinnaeanInferencer() {
    this.subInferencers = new HashMap<>(INFERENCERS.length);
    for (Inferencer<SimpleLinnaeanClassification> i: INFERENCERS)
      this.subInferencers.put(i.getSignature(), i);
  }

  @Override
  public String getSignature() {
    return null;
  }

  @Override
  public Inference probability(SimpleLinnaeanClassification classification, Classifier classifier, Trace trace) throws BayesianException {
    Inferencer<SimpleLinnaeanClassification> sub = this.subInferencers.get(classifier.getSignature());
    if (sub == null)
      throw new IllegalArgumentException("Signature '" + classifier.getSignature() + "' is not recognised");
    return sub.probability(classification, classifier, trace);
  }

  @JsonPropertyOrder(alphabetic = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Evidence {
    public Boolean e$taxonId;
    public Boolean e$taxonRank;
    public Boolean e$specificEpithet;
    public Boolean e$scientificNameAuthorship;
    public Boolean e$scientificName;
    public Boolean e$soundexScientificName;
    public Boolean e$prefixScientificName;
    public Boolean e$genus;
    public Boolean e$family;
    public Boolean e$order;
    public Boolean e$class_;
    public Boolean e$phylum;
    public Boolean e$kingdom;

    @JsonIgnore
    public boolean isT$e$taxonId() {
      return this.e$taxonId == null || this.e$taxonId;
    }

    @JsonIgnore
    public boolean isF$e$taxonId() {
      return this.e$taxonId == null || !this.e$taxonId;
    }

    @JsonIgnore
    public boolean isT$e$taxonRank() {
      return this.e$taxonRank == null || this.e$taxonRank;
    }

    @JsonIgnore
    public boolean isF$e$taxonRank() {
      return this.e$taxonRank == null || !this.e$taxonRank;
    }

    @JsonIgnore
    public boolean isT$e$specificEpithet() {
      return this.e$specificEpithet == null || this.e$specificEpithet;
    }

    @JsonIgnore
    public boolean isF$e$specificEpithet() {
      return this.e$specificEpithet == null || !this.e$specificEpithet;
    }

    @JsonIgnore
    public boolean isT$e$scientificNameAuthorship() {
      return this.e$scientificNameAuthorship == null || this.e$scientificNameAuthorship;
    }

    @JsonIgnore
    public boolean isF$e$scientificNameAuthorship() {
      return this.e$scientificNameAuthorship == null || !this.e$scientificNameAuthorship;
    }

    @JsonIgnore
    public boolean isT$e$scientificName() {
      return this.e$scientificName == null || this.e$scientificName;
    }

    @JsonIgnore
    public boolean isF$e$scientificName() {
      return this.e$scientificName == null || !this.e$scientificName;
    }

    @JsonIgnore
    public boolean isT$e$soundexScientificName() {
      return this.e$soundexScientificName == null || this.e$soundexScientificName;
    }

    @JsonIgnore
    public boolean isF$e$soundexScientificName() {
      return this.e$soundexScientificName == null || !this.e$soundexScientificName;
    }

    @JsonIgnore
    public boolean isT$e$prefixScientificName() {
      return this.e$prefixScientificName == null || this.e$prefixScientificName;
    }

    @JsonIgnore
    public boolean isF$e$prefixScientificName() {
      return this.e$prefixScientificName == null || !this.e$prefixScientificName;
    }

    @JsonIgnore
    public boolean isT$e$genus() {
      return this.e$genus == null || this.e$genus;
    }

    @JsonIgnore
    public boolean isF$e$genus() {
      return this.e$genus == null || !this.e$genus;
    }

    @JsonIgnore
    public boolean isT$e$family() {
      return this.e$family == null || this.e$family;
    }

    @JsonIgnore
    public boolean isF$e$family() {
      return this.e$family == null || !this.e$family;
    }

    @JsonIgnore
    public boolean isT$e$order() {
      return this.e$order == null || this.e$order;
    }

    @JsonIgnore
    public boolean isF$e$order() {
      return this.e$order == null || !this.e$order;
    }

    @JsonIgnore
    public boolean isT$e$class_() {
      return this.e$class_ == null || this.e$class_;
    }

    @JsonIgnore
    public boolean isF$e$class_() {
      return this.e$class_ == null || !this.e$class_;
    }

    @JsonIgnore
    public boolean isT$e$phylum() {
      return this.e$phylum == null || this.e$phylum;
    }

    @JsonIgnore
    public boolean isF$e$phylum() {
      return this.e$phylum == null || !this.e$phylum;
    }

    @JsonIgnore
    public boolean isT$e$kingdom() {
      return this.e$kingdom == null || this.e$kingdom;
    }

    @JsonIgnore
    public boolean isF$e$kingdom() {
      return this.e$kingdom == null || !this.e$kingdom;
    }

  }
}