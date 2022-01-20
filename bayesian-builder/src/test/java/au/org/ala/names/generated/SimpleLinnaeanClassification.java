package au.org.ala.names.generated;

import au.org.ala.bayesian.*;
import au.org.ala.bayesian.derivation.SoundexGenerator;
import lombok.SneakyThrows;
import org.gbif.dwc.terms.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SimpleLinnaeanClassification implements Classification<SimpleLinnaeanClassification> {
  private Issues issues;
  private Hints<SimpleLinnaeanClassification> hints;

  private SoundexGenerator soundex;
  private static Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification> REMOVE_CLASS =
    c -> {
      SimpleLinnaeanClassification nc;
      if (c.class_ == null) return c;
      nc = c.clone();
      nc.class_ = null;
      nc.addIssue(SimpleLinnaeanFactory.REMOVED_CLASS);
      return nc;
    };
  private static Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification> REMOVE_ORDER =
    c -> {
      SimpleLinnaeanClassification nc;
      if (c.order == null) return c;
      nc = c.clone();
      nc.order = null;
      nc.addIssue(SimpleLinnaeanFactory.REMOVED_ORDER);
      return nc;
    };
  private static Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification> REMOVE_PHYLUM =
    c -> {
      SimpleLinnaeanClassification nc;
      if (!(c.class_ != null || c.order != null || c.family != null) || (c.phylum == null)) return c;
      nc = c.clone();
      nc.phylum = null;
      nc.addIssue(SimpleLinnaeanFactory.REMOVED_PHYLUM);
      return nc;
    };
  private static Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification> REMOVE_RANK =
    c -> {
      SimpleLinnaeanClassification nc;
      if (c.taxonRank == null) return c;
      nc = c.clone();
      nc.taxonRank = null;
      nc.addIssue(SimpleLinnaeanFactory.REMOVED_RANK);
      return nc;
    };

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
  // Additional stored classification information not used in inference
  public java.lang.String acceptedNameUsageId;
  public java.lang.String parentNameUsageId;
  public java.lang.String taxonomicStatus;

  public SimpleLinnaeanClassification() {
    this.issues = new Issues();
    this.hints = new Hints<>();
    this.soundex = new SoundexGenerator();
  }

  public SimpleLinnaeanClassification(Classifier classifier) throws BayesianException {
    this();
    this.read(classifier, true);
  }

  @Override
  @SneakyThrows
  public SimpleLinnaeanClassification clone() {
      SimpleLinnaeanClassification clone = (SimpleLinnaeanClassification) super.clone();
      clone.issues = new Issues(this.issues);
      return clone;
  }

  @Override
  public void addIssue(Term issue) {
    this.issues = this.issues.with(issue);
  }

  @Override
  public void addIssues(Issues issues) {
        this.issues = this.issues.merge(issues);
  }

  @Override
  public Hints<SimpleLinnaeanClassification> getHints() {
    return this.hints;
  }

  @Override
  public <T> void addHint(Observable observable, T value) {
        this.hints.addHint(observable, value);
  }

  @Override
  public Term getType() {
    return SimpleLinnaeanFactory.CONCEPT;
  }

  @Override
  public Issues getIssues() {
    return this.issues;
  }


  @Override
  public String getIdentifier() {
    return this.taxonId;
  }

  @Override
  public String getName() {
    return this.scientificName;
  }

  @Override
  public String getParent() {
    return this.parentNameUsageId;
  }

  @Override
  public String getAccepted() {
    return this.acceptedNameUsageId;
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
  public void inferForSearch(Analyser<SimpleLinnaeanClassification> analyser) throws BayesianException {
    this.taxonId = SimpleLinnaeanFactory.taxonId.analyse(this.taxonId);
    this.taxonRank = SimpleLinnaeanFactory.taxonRank.analyse(this.taxonRank);
    this.specificEpithet = SimpleLinnaeanFactory.specificEpithet.analyse(this.specificEpithet);
    this.scientificNameAuthorship = SimpleLinnaeanFactory.scientificNameAuthorship.analyse(this.scientificNameAuthorship);
    this.scientificName = SimpleLinnaeanFactory.scientificName.analyse(this.scientificName);
    this.soundexScientificName = SimpleLinnaeanFactory.soundexScientificName.analyse(this.soundexScientificName);
    this.genus = SimpleLinnaeanFactory.genus.analyse(this.genus);
    this.family = SimpleLinnaeanFactory.family.analyse(this.family);
    this.order = SimpleLinnaeanFactory.order.analyse(this.order);
    this.class_ = SimpleLinnaeanFactory.class_.analyse(this.class_);
    this.phylum = SimpleLinnaeanFactory.phylum.analyse(this.phylum);
    this.kingdom = SimpleLinnaeanFactory.kingdom.analyse(this.kingdom);
    this.acceptedNameUsageId = SimpleLinnaeanFactory.acceptedNameUsageId.analyse(this.acceptedNameUsageId);
    this.parentNameUsageId = SimpleLinnaeanFactory.parentNameUsageId.analyse(this.parentNameUsageId);
    this.taxonomicStatus = SimpleLinnaeanFactory.taxonomicStatus.analyse(this.taxonomicStatus);
    analyser.analyseForSearch(this);
    if (this.soundexScientificName == null) {
      this.soundexScientificName = this.soundex.soundex(this.scientificName);
    }
  }

  @Override
  public List<List<Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification>>> searchModificationOrder() {
        List<List<Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification>>> modifications = new ArrayList();
    return modifications;
  }

  @Override
  public List<List<Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification>>> matchModificationOrder() {
    List<List<Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification>>> modifications = new ArrayList();
    List<Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification>> ml;
    ml = new ArrayList();
    ml.add(null);
    if (this.order != null)
      ml.add(REMOVE_ORDER);
    if (this.class_ != null)
      ml.add(REMOVE_CLASS);
    if ((this.class_ != null || this.order != null || this.family != null) && (this.phylum != null))
      ml.add(REMOVE_PHYLUM);
    if (ml.size() > 1)
      modifications.add(ml);
    ml = new ArrayList();
    ml.add(null);
    if (this.taxonRank != null)
      ml.add(REMOVE_RANK);
    if (ml.size() > 1)
      modifications.add(ml);
    return modifications;
  }


  @Override
  public List<List<Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification>>> hintModificationOrder() {
    List<List<Function<SimpleLinnaeanClassification, SimpleLinnaeanClassification>>> modifications = new ArrayList();
    this.hints.buildModifications(SimpleLinnaeanFactory.taxonId, java.lang.String.class, (c, v) -> { c.taxonId = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.taxonRank, java.lang.String.class, (c, v) -> { c.taxonRank = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.specificEpithet, java.lang.String.class, (c, v) -> { c.specificEpithet = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.scientificNameAuthorship, java.lang.String.class, (c, v) -> { c.scientificNameAuthorship = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.scientificName, java.lang.String.class, (c, v) -> { c.scientificName = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.soundexScientificName, java.lang.String.class, (c, v) -> { c.soundexScientificName = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.genus, java.lang.String.class, (c, v) -> { c.genus = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.family, java.lang.String.class, (c, v) -> { c.family = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.order, java.lang.String.class, (c, v) -> { c.order = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.class_, java.lang.String.class, (c, v) -> { c.class_ = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.phylum, java.lang.String.class, (c, v) -> { c.phylum = v; }, modifications);
    this.hints.buildModifications(SimpleLinnaeanFactory.kingdom, java.lang.String.class, (c, v) -> { c.kingdom = v; }, modifications);
    return modifications;
  }

  @Override
  public void read(Classifier classifier, boolean overwrite) throws BayesianException {
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
    if (overwrite || this.acceptedNameUsageId == null) {
      this.acceptedNameUsageId = classifier.get(SimpleLinnaeanFactory.acceptedNameUsageId);
    }
    if (overwrite || this.parentNameUsageId == null) {
      this.parentNameUsageId = classifier.get(SimpleLinnaeanFactory.parentNameUsageId);
    }
    if (overwrite || this.taxonomicStatus == null) {
      this.taxonomicStatus = classifier.get(SimpleLinnaeanFactory.taxonomicStatus);
    }
  }

  @Override
  public void write(Classifier classifier, boolean overwrite) throws BayesianException {
    if(overwrite){
      classifier.clear(SimpleLinnaeanFactory.taxonId);
      classifier.clear(SimpleLinnaeanFactory.taxonRank);
      classifier.clear(SimpleLinnaeanFactory.specificEpithet);
      classifier.clear(SimpleLinnaeanFactory.scientificNameAuthorship);
      classifier.clear(SimpleLinnaeanFactory.scientificName);
      classifier.clear(SimpleLinnaeanFactory.soundexScientificName);
      classifier.clear(SimpleLinnaeanFactory.genus);
      classifier.clear(SimpleLinnaeanFactory.family);
      classifier.clear(SimpleLinnaeanFactory.order);
      classifier.clear(SimpleLinnaeanFactory.class_);
      classifier.clear(SimpleLinnaeanFactory.phylum);
      classifier.clear(SimpleLinnaeanFactory.kingdom);
      classifier.clear(SimpleLinnaeanFactory.acceptedNameUsageId);
      classifier.clear(SimpleLinnaeanFactory.parentNameUsageId);
      classifier.clear(SimpleLinnaeanFactory.taxonomicStatus);
    }
    classifier.add(SimpleLinnaeanFactory.taxonId, this.taxonId, false);
    classifier.add(SimpleLinnaeanFactory.taxonRank, this.taxonRank, false);
    classifier.add(SimpleLinnaeanFactory.specificEpithet, this.specificEpithet, false);
    classifier.add(SimpleLinnaeanFactory.scientificNameAuthorship, this.scientificNameAuthorship, false);
    classifier.add(SimpleLinnaeanFactory.scientificName, this.scientificName, false);
    classifier.add(SimpleLinnaeanFactory.soundexScientificName, this.soundexScientificName, false);
    classifier.add(SimpleLinnaeanFactory.genus, this.genus, false);
    classifier.add(SimpleLinnaeanFactory.family, this.family, false);
    classifier.add(SimpleLinnaeanFactory.order, this.order, false);
    classifier.add(SimpleLinnaeanFactory.class_, this.class_, false);
    classifier.add(SimpleLinnaeanFactory.phylum, this.phylum, false);
    classifier.add(SimpleLinnaeanFactory.kingdom, this.kingdom, false);
    classifier.add(SimpleLinnaeanFactory.acceptedNameUsageId, this.acceptedNameUsageId, false);
    classifier.add(SimpleLinnaeanFactory.parentNameUsageId, this.parentNameUsageId, false);
    classifier.add(SimpleLinnaeanFactory.taxonomicStatus, this.taxonomicStatus, false);
  }


  public SimpleLinnaeanInferencer.Evidence match(Classifier classifier) throws BayesianException {
    SimpleLinnaeanInferencer.Evidence evidence = new SimpleLinnaeanInferencer.Evidence();
    evidence.e$taxonId = classifier.match(this.taxonId, SimpleLinnaeanFactory.taxonId);
    evidence.e$taxonRank = classifier.match(this.taxonRank, SimpleLinnaeanFactory.taxonRank);
    evidence.e$specificEpithet = classifier.match(this.specificEpithet, SimpleLinnaeanFactory.specificEpithet);
    evidence.e$scientificNameAuthorship = classifier.match(this.scientificNameAuthorship, SimpleLinnaeanFactory.scientificNameAuthorship);
    evidence.e$scientificName = classifier.match(this.scientificName, SimpleLinnaeanFactory.scientificName);
    evidence.e$soundexScientificName = classifier.match(this.soundexScientificName, SimpleLinnaeanFactory.soundexScientificName);
    evidence.e$genus = classifier.match(this.genus, SimpleLinnaeanFactory.genus);
    evidence.e$family = classifier.match(this.family, SimpleLinnaeanFactory.family);
    evidence.e$order = classifier.match(this.order, SimpleLinnaeanFactory.order);
    evidence.e$class_ = classifier.match(this.class_, SimpleLinnaeanFactory.class_);
    evidence.e$phylum = classifier.match(this.phylum, SimpleLinnaeanFactory.phylum);
    evidence.e$kingdom = classifier.match(this.kingdom, SimpleLinnaeanFactory.kingdom);
    return evidence;
  }

}