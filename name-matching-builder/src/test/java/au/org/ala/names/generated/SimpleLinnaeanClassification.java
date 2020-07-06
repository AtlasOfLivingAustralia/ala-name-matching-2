package au.org.ala.names.generated;

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Observation;

import java.util.ArrayList;
import java.util.Collection;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import au.org.ala.util.TaxonNameSoundEx;

public class SimpleLinnaeanClassification extends Classification {
  private TaxonNameSoundEx soundex;

  public java.lang.String taxonId;
  public java.lang.String taxonRank;
  public java.lang.String specificEpithet;
  public java.lang.String scientificNameAuthorship;
  public java.lang.String scientificName;
  public java.lang.String soundexScientificName;
  public java.lang.String genus;
  public java.lang.String family;
  public java.lang.String order;
  public java.lang.String class_;
  public java.lang.String phylum;
  public java.lang.String kingdom;

  public SimpleLinnaeanClassification() {
    this.soundex = new TaxonNameSoundEx();
  }

  public SimpleLinnaeanClassification(Classifier classifier) throws InferenceException {
    this();
    this.populate(classifier, true);
    this.infer();
  }

  @Override
  public Term getType() {
    return DwcTerm.Taxon;
  }

  @Override
  public Collection<Observation> toObservations() {
    Collection<Observation> obs = new ArrayList(12);

    if (this.taxonId != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.taxonId, this.taxonId));
    if (this.taxonRank != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.taxonRank, this.taxonRank));
    if (this.specificEpithet != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.specificEpithet, this.specificEpithet));
    if (this.scientificNameAuthorship != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.scientificNameAuthorship, this.scientificNameAuthorship));
    if (this.scientificName != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.scientificName, this.scientificName));
    if (this.soundexScientificName != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.soundexScientificName, this.soundexScientificName));
    if (this.genus != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.genus, this.genus));
    if (this.family != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.family, this.family));
    if (this.order != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.order, this.order));
    if (this.class_ != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.class_, this.class_));
    if (this.phylum != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.phylum, this.phylum));
    if (this.kingdom != null)
      obs.add(new Observation(true, SimpleLinnaeanObservables.kingdom, this.kingdom));
    return obs;
  }

  @Override
  public void infer() throws InferenceException {
    if (this.soundexScientificName == null) {
      this.soundexScientificName = this.soundex.treatWord(this.scientificName, this.taxonRank);
    }
  }


  @Override
  public void populate(Classifier classifier, boolean overwrite) throws InferenceException {
    if (overwrite || this.taxonId == null) {
      this.taxonId = classifier.get(SimpleLinnaeanObservables.taxonId);
    }
    if (overwrite || this.taxonRank == null) {
      this.taxonRank = classifier.get(SimpleLinnaeanObservables.taxonRank);
    }
    if (overwrite || this.specificEpithet == null) {
      this.specificEpithet = classifier.get(SimpleLinnaeanObservables.specificEpithet);
    }
    if (overwrite || this.scientificNameAuthorship == null) {
      this.scientificNameAuthorship = classifier.get(SimpleLinnaeanObservables.scientificNameAuthorship);
    }
    if (overwrite || this.scientificName == null) {
      this.scientificName = classifier.get(SimpleLinnaeanObservables.scientificName);
    }
    if (overwrite || this.soundexScientificName == null) {
      this.soundexScientificName = classifier.get(SimpleLinnaeanObservables.soundexScientificName);
    }
    if (overwrite || this.genus == null) {
      this.genus = classifier.get(SimpleLinnaeanObservables.genus);
    }
    if (overwrite || this.family == null) {
      this.family = classifier.get(SimpleLinnaeanObservables.family);
    }
    if (overwrite || this.order == null) {
      this.order = classifier.get(SimpleLinnaeanObservables.order);
    }
    if (overwrite || this.class_ == null) {
      this.class_ = classifier.get(SimpleLinnaeanObservables.class_);
    }
    if (overwrite || this.phylum == null) {
      this.phylum = classifier.get(SimpleLinnaeanObservables.phylum);
    }
    if (overwrite || this.kingdom == null) {
      this.kingdom = classifier.get(SimpleLinnaeanObservables.kingdom);
    }
  }

  public SimpleLinnaeanInference.Evidence match(Classifier classifier) throws InferenceException {
    SimpleLinnaeanInference.Evidence evidence = new SimpleLinnaeanInference.Evidence();
    evidence.e$taxonId = classifier.match(SimpleLinnaeanObservables.taxonId, this.taxonId);
    evidence.e$taxonRank = classifier.match(SimpleLinnaeanObservables.taxonRank, this.taxonRank);
    evidence.e$specificEpithet = classifier.match(SimpleLinnaeanObservables.specificEpithet, this.specificEpithet);
    evidence.e$scientificNameAuthorship = classifier.match(SimpleLinnaeanObservables.scientificNameAuthorship, this.scientificNameAuthorship);
    evidence.e$scientificName = classifier.match(SimpleLinnaeanObservables.scientificName, this.scientificName);
    evidence.e$soundexScientificName = classifier.match(SimpleLinnaeanObservables.soundexScientificName, this.soundexScientificName);
    evidence.e$genus = classifier.match(SimpleLinnaeanObservables.genus, this.genus);
    evidence.e$family = classifier.match(SimpleLinnaeanObservables.family, this.family);
    evidence.e$order = classifier.match(SimpleLinnaeanObservables.order, this.order);
    evidence.e$class_ = classifier.match(SimpleLinnaeanObservables.class_, this.class_);
    evidence.e$phylum = classifier.match(SimpleLinnaeanObservables.phylum, this.phylum);
    evidence.e$kingdom = classifier.match(SimpleLinnaeanObservables.kingdom, this.kingdom);
    return evidence;
  }

}