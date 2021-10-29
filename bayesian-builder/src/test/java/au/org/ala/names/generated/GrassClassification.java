package au.org.ala.names.generated;

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import lombok.SneakyThrows;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.bayesian.Analyser;

public class GrassClassification implements Classification<GrassClassification> {
  private Analyser<GrassClassification> analyser;
  private Issues issues;

  public java.lang.String rain;
  public java.lang.String sprinkler;
  public java.lang.String wet;

  public GrassClassification(Analyser<GrassClassification> analyser) {
    this.analyser = GrassFactory.instance().createAnalyser();
    this.issues = new Issues();
  }

  public GrassClassification() {
    this(GrassFactory.instance().createAnalyser());
  }

  public GrassClassification(Classifier classifier, Analyser<GrassClassification> analyser) throws BayesianException {
    this(analyser);
    this.read(classifier, true);
    this.inferForIndex();
  }

  @Override
  @SneakyThrows
  public GrassClassification clone() {
      GrassClassification clone = (GrassClassification) super.clone();
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
  public Term getType() {
    return GrassFactory.CONCEPT;
  }

  @Override
  public Analyser<GrassClassification> getAnalyser() {
    return this.analyser;
  }

  @Override
  public Issues getIssues() {
    return this.issues;
  }


  @Override
  public String getIdentifier() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getParent() {
    return null;
  }

  @Override
  public String getAccepted() {
    return null;
  }

  @Override
  public Collection<Observation> toObservations() {
    Collection<Observation> obs = new ArrayList(3);

    if (this.rain != null)
      obs.add(new Observation(true, GrassFactory.rain, this.rain));
    if (this.sprinkler != null)
      obs.add(new Observation(true, GrassFactory.sprinkler, this.sprinkler));
    if (this.wet != null)
      obs.add(new Observation(true, GrassFactory.wet, this.wet));
    return obs;
  }

  @Override
  public void inferForIndex() throws BayesianException {
    this.rain = GrassFactory.rain.analyse(this.rain);
    this.sprinkler = GrassFactory.sprinkler.analyse(this.sprinkler);
    this.wet = GrassFactory.wet.analyse(this.wet);
    this.analyser.analyseForIndex(this);
  }


  @Override
  public void inferForSearch() throws BayesianException {
    this.rain = GrassFactory.rain.analyse(this.rain);
    this.sprinkler = GrassFactory.sprinkler.analyse(this.sprinkler);
    this.wet = GrassFactory.wet.analyse(this.wet);
        this.analyser.analyseForSearch(this);
  }


  @Override
  public List<List<Function<GrassClassification, GrassClassification>>> searchModificationOrder() {
        List<List<Function<GrassClassification, GrassClassification>>> modifications = new ArrayList();
    return modifications;
  }

  @Override
  public List<List<Function<GrassClassification, GrassClassification>>> matchModificationOrder() {
    List<List<Function<GrassClassification, GrassClassification>>> modifications = new ArrayList();
    return modifications;
  }

  @Override
  public void read(Classifier classifier, boolean overwrite) throws BayesianException {
    if (overwrite || this.rain == null) {
      this.rain = classifier.get(GrassFactory.rain);
    }
    if (overwrite || this.sprinkler == null) {
      this.sprinkler = classifier.get(GrassFactory.sprinkler);
    }
    if (overwrite || this.wet == null) {
      this.wet = classifier.get(GrassFactory.wet);
    }
  }

  @Override
  public void write(Classifier classifier, boolean overwrite) throws BayesianException {
    if(overwrite){
      classifier.replace(GrassFactory.rain,this.rain);
      classifier.replace(GrassFactory.sprinkler,this.sprinkler);
      classifier.replace(GrassFactory.wet,this.wet);
    } else {
      classifier.add(GrassFactory.rain,this.rain);
      classifier.add(GrassFactory.sprinkler,this.sprinkler);
      classifier.add(GrassFactory.wet,this.wet);
    }
  }


  public GrassInferencer.Evidence match(Classifier classifier) throws BayesianException {
    GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();
    evidence.e$rain = classifier.match(this.rain, GrassFactory.rain);
    evidence.e$sprinkler = classifier.match(this.sprinkler, GrassFactory.sprinkler);
    evidence.e$wet = classifier.match(this.wet, GrassFactory.wet);
    return evidence;
  }

}