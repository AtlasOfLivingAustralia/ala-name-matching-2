package au.org.ala.names.generated;

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.bayesian.StoreException;

import java.util.ArrayList;
import java.util.Collection;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.bayesian.Analyser;
import org.apache.commons.codec.language.Soundex;
import au.org.ala.bayesian.analysis.DoubleAnalysis;

public class SimpleLinnaeanClassification implements Classification<SimpleLinnaeanClassification> {
  private Analyser<SimpleLinnaeanClassification> analyser;
  private Issues issues;
  private Soundex soundex;

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

  public SimpleLinnaeanClassification(Analyser<SimpleLinnaeanClassification> analyser) {
    this.analyser = SimpleLinnaeanFactory.instance().createAnalyser();
    this.issues = new Issues();
    this.soundex = new Soundex();
  }

  public SimpleLinnaeanClassification() {
    this(SimpleLinnaeanFactory.instance().createAnalyser());
  }

  public SimpleLinnaeanClassification(Classifier classifier, Analyser<SimpleLinnaeanClassification> analyser) throws InferenceException, StoreException {
    this(analyser);
    this.read(classifier, true);
    this.infer();
  }

  @Override
  public Term getType() {
    return DwcTerm.Taxon;
  }

  @Override
  public Analyser<SimpleLinnaeanClassification> getAnalyser() {
    return this.analyser;
  }

  @Override
  public Issues getIssues() {
    return this.issues;
  }

  @Override
  public Collection<Observation> toObservations() {
    Collection<Observation> obs = new ArrayList(12);

    if (this.taxonId != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.taxonId, this.taxonId));
    if (this.taxonRank != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.taxonRank, this.taxonRank));
    if (this.specificEpithet != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.specificEpithet, this.specificEpithet));
    if (this.scientificNameAuthorship != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.scientificNameAuthorship, this.scientificNameAuthorship));
    if (this.scientificName != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.scientificName, this.scientificName));
    if (this.soundexScientificName != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.soundexScientificName, this.soundexScientificName));
    if (this.genus != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.genus, this.genus));
    if (this.family != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.family, this.family));
    if (this.order != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.order, this.order));
    if (this.class_ != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.class_, this.class_));
    if (this.phylum != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.phylum, this.phylum));
    if (this.kingdom != null)
      obs.add(new Observation(true, SimpleLinnaeanFactory.kingdom, this.kingdom));
    return obs;
  }

  @Override
  public void infer() throws InferenceException, StoreException {
    this.taxonId = (String) SimpleLinnaeanFactory.taxonId.getAnalysis().analyse(this.taxonId);
    this.taxonRank = (String) SimpleLinnaeanFactory.taxonRank.getAnalysis().analyse(this.taxonRank);
    this.specificEpithet = (String) SimpleLinnaeanFactory.specificEpithet.getAnalysis().analyse(this.specificEpithet);
    this.scientificNameAuthorship = (String) SimpleLinnaeanFactory.scientificNameAuthorship.getAnalysis().analyse(this.scientificNameAuthorship);
    this.scientificName = (String) SimpleLinnaeanFactory.scientificName.getAnalysis().analyse(this.scientificName);
    this.soundexScientificName = (String) SimpleLinnaeanFactory.soundexScientificName.getAnalysis().analyse(this.soundexScientificName);
    this.genus = (String) SimpleLinnaeanFactory.genus.getAnalysis().analyse(this.genus);
    this.family = (String) SimpleLinnaeanFactory.family.getAnalysis().analyse(this.family);
    this.order = (String) SimpleLinnaeanFactory.order.getAnalysis().analyse(this.order);
    this.class_ = (String) SimpleLinnaeanFactory.class_.getAnalysis().analyse(this.class_);
    this.phylum = (String) SimpleLinnaeanFactory.phylum.getAnalysis().analyse(this.phylum);
    this.kingdom = (String) SimpleLinnaeanFactory.kingdom.getAnalysis().analyse(this.kingdom);
    this.analyser.analyse(this);
    if (this.soundexScientificName == null) {
      this.soundexScientificName = this.soundex.soundex(this.scientificName);
    }
  }

  @Override
  public void read(Classifier classifier, boolean overwrite) throws InferenceException {
    if (overwrite || this.taxonId == null) {
      this.taxonId = classifier.get(SimpleLinnaeanFactory.taxonId);
    }
    if (overwrite || this.taxonRank == null) {
      this.taxonRank = classifier.get(SimpleLinnaeanFactory.taxonRank);
    }
    if (overwrite || this.specificEpithet == null) {
      this.specificEpithet = classifier.get(SimpleLinnaeanFactory.specificEpithet);
    }
    if (overwrite || this.scientificNameAuthorship == null) {
      this.scientificNameAuthorship = classifier.get(SimpleLinnaeanFactory.scientificNameAuthorship);
    }
    if (overwrite || this.scientificName == null) {
      this.scientificName = classifier.get(SimpleLinnaeanFactory.scientificName);
    }
    if (overwrite || this.soundexScientificName == null) {
      this.soundexScientificName = classifier.get(SimpleLinnaeanFactory.soundexScientificName);
    }
    if (overwrite || this.genus == null) {
      this.genus = classifier.get(SimpleLinnaeanFactory.genus);
    }
    if (overwrite || this.family == null) {
      this.family = classifier.get(SimpleLinnaeanFactory.family);
    }
    if (overwrite || this.order == null) {
      this.order = classifier.get(SimpleLinnaeanFactory.order);
    }
    if (overwrite || this.class_ == null) {
      this.class_ = classifier.get(SimpleLinnaeanFactory.class_);
    }
    if (overwrite || this.phylum == null) {
      this.phylum = classifier.get(SimpleLinnaeanFactory.phylum);
    }
    if (overwrite || this.kingdom == null) {
      this.kingdom = classifier.get(SimpleLinnaeanFactory.kingdom);
    }
  }

  @Override
  public void write(Classifier classifier, boolean overwrite) throws InferenceException, StoreException{
    if(overwrite){
      classifier.replace(SimpleLinnaeanFactory.taxonId,this.taxonId);
      classifier.replace(SimpleLinnaeanFactory.taxonRank,this.taxonRank);
      classifier.replace(SimpleLinnaeanFactory.specificEpithet,this.specificEpithet);
      classifier.replace(SimpleLinnaeanFactory.scientificNameAuthorship,this.scientificNameAuthorship);
      classifier.replace(SimpleLinnaeanFactory.scientificName,this.scientificName);
      classifier.replace(SimpleLinnaeanFactory.soundexScientificName,this.soundexScientificName);
      classifier.replace(SimpleLinnaeanFactory.genus,this.genus);
      classifier.replace(SimpleLinnaeanFactory.family,this.family);
      classifier.replace(SimpleLinnaeanFactory.order,this.order);
      classifier.replace(SimpleLinnaeanFactory.class_,this.class_);
      classifier.replace(SimpleLinnaeanFactory.phylum,this.phylum);
      classifier.replace(SimpleLinnaeanFactory.kingdom,this.kingdom);
    } else {
      classifier.add(SimpleLinnaeanFactory.taxonId,this.taxonId);
      classifier.add(SimpleLinnaeanFactory.taxonRank,this.taxonRank);
      classifier.add(SimpleLinnaeanFactory.specificEpithet,this.specificEpithet);
      classifier.add(SimpleLinnaeanFactory.scientificNameAuthorship,this.scientificNameAuthorship);
      classifier.add(SimpleLinnaeanFactory.scientificName,this.scientificName);
      classifier.add(SimpleLinnaeanFactory.soundexScientificName,this.soundexScientificName);
      classifier.add(SimpleLinnaeanFactory.genus,this.genus);
      classifier.add(SimpleLinnaeanFactory.family,this.family);
      classifier.add(SimpleLinnaeanFactory.order,this.order);
      classifier.add(SimpleLinnaeanFactory.class_,this.class_);
      classifier.add(SimpleLinnaeanFactory.phylum,this.phylum);
      classifier.add(SimpleLinnaeanFactory.kingdom,this.kingdom);
    }
  }


  public SimpleLinnaeanInferencer.Evidence match(Classifier classifier) throws StoreException, InferenceException {
    SimpleLinnaeanInferencer.Evidence evidence = new SimpleLinnaeanInferencer.Evidence();
    evidence.e$taxonId = classifier.match(SimpleLinnaeanFactory.taxonId, this.taxonId);
    evidence.e$taxonRank = classifier.match(SimpleLinnaeanFactory.taxonRank, this.taxonRank);
    evidence.e$specificEpithet = classifier.match(SimpleLinnaeanFactory.specificEpithet, this.specificEpithet);
    evidence.e$scientificNameAuthorship = classifier.match(SimpleLinnaeanFactory.scientificNameAuthorship, this.scientificNameAuthorship);
    evidence.e$scientificName = classifier.match(SimpleLinnaeanFactory.scientificName, this.scientificName);
    evidence.e$soundexScientificName = classifier.match(SimpleLinnaeanFactory.soundexScientificName, this.soundexScientificName);
    evidence.e$genus = classifier.match(SimpleLinnaeanFactory.genus, this.genus);
    evidence.e$family = classifier.match(SimpleLinnaeanFactory.family, this.family);
    evidence.e$order = classifier.match(SimpleLinnaeanFactory.order, this.order);
    evidence.e$class_ = classifier.match(SimpleLinnaeanFactory.class_, this.class_);
    evidence.e$phylum = classifier.match(SimpleLinnaeanFactory.phylum, this.phylum);
    evidence.e$kingdom = classifier.match(SimpleLinnaeanFactory.kingdom, this.kingdom);
    return evidence;
  }

}