package au.org.ala.names.generated;

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Fidelity;
import au.org.ala.bayesian.Hints;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.MatchOptions;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.bayesian.fidelity.CompositeFidelity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import au.org.ala.bayesian.analysis.StringAnalysis;
import au.org.ala.bayesian.Analyser;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GrassClassification implements Classification<GrassClassification> {
  private Issues issues;
  private Hints<GrassClassification> hints;


  public java.lang.String rain;
  public java.lang.String sprinkler;
  public java.lang.String wet;

  public GrassClassification() {
    this.issues = new Issues();
    this.hints = new Hints<>();
  }

  public GrassClassification(Classifier classifier) throws BayesianException {
    this();
    this.read(classifier, true);
  }

  @Override
  @SneakyThrows
  public @NonNull GrassClassification clone() {
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
  public Hints<GrassClassification> getHints() {
    return this.hints;
  }

  @Override
  public <T> void addHint(Observable<T> observable, T value) {
        this.hints.addHint(observable, value);
  }

  @Override
  @JsonIgnore
  public @NonNull Term getType() {
    return GrassFactory.CONCEPT;
  }

  @Override
  @JsonIgnore
  public Issues getIssues() {
    return this.issues;
  }

  @JsonProperty("issues")
  public List<String> getIssueStrings() {
    return this.issues.asStrings();
  }

  @JsonProperty("issues")
  public void setIssueStrings(List<String> issues) {
    this.issues = Issues.fromStrings(issues);
  }

  @Override
  @JsonIgnore
  public String getIdentifier() {
    return null;
  }

  @Override
  @JsonIgnore
  public String getName() {
    return null;
  }

  @Override
  @JsonIgnore
  public String getParent() {
    return null;
  }

  @Override
  @JsonIgnore
  public String getAccepted() {
    return null;
  }

  @Override
  public Collection<Observation<?>> toObservations() {
    Collection<Observation<?>> obs = new ArrayList(3);

    if (this.rain != null)
      obs.add(new Observation(true, GrassFactory.rain, this.rain));
    if (this.sprinkler != null)
      obs.add(new Observation(true, GrassFactory.sprinkler, this.sprinkler));
    if (this.wet != null)
      obs.add(new Observation(true, GrassFactory.wet, this.wet));
    return obs;
  }

  @Override
  public void inferForSearch(@NonNull Analyser<GrassClassification> analyser, @NonNull MatchOptions options) throws BayesianException {
    this.rain = GrassFactory.rain.analyse(this.rain);
    this.sprinkler = GrassFactory.sprinkler.analyse(this.sprinkler);
    this.wet = GrassFactory.wet.analyse(this.wet);
    analyser.analyseForSearch(this, options);
  }

  @Override
  public Fidelity<GrassClassification> buildFidelity(GrassClassification actual) throws InferenceException {
    CompositeFidelity<GrassClassification> fidelity = new CompositeFidelity<>(this, actual);
    if (this.rain != null)
      fidelity.add(GrassFactory.rain.getAnalysis().buildFidelity(this.rain, actual.rain));
    if (this.sprinkler != null)
      fidelity.add(GrassFactory.sprinkler.getAnalysis().buildFidelity(this.sprinkler, actual.sprinkler));
    if (this.wet != null)
      fidelity.add(GrassFactory.wet.getAnalysis().buildFidelity(this.wet, actual.wet));
    return fidelity;
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
  public List<List<Function<GrassClassification, GrassClassification>>> hintModificationOrder() {
    List<List<Function<GrassClassification, GrassClassification>>> modifications = new ArrayList();
    this.hints.buildModifications(GrassFactory.rain, java.lang.String.class, (c, v) -> { c.rain = v; }, modifications);
    this.hints.buildModifications(GrassFactory.sprinkler, java.lang.String.class, (c, v) -> { c.sprinkler = v; }, modifications);
    this.hints.buildModifications(GrassFactory.wet, java.lang.String.class, (c, v) -> { c.wet = v; }, modifications);
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
      classifier.clear(GrassFactory.rain);
      classifier.clear(GrassFactory.sprinkler);
      classifier.clear(GrassFactory.wet);
    }
    classifier.add(GrassFactory.rain, this.rain, false, false);
    classifier.add(GrassFactory.sprinkler, this.sprinkler, false, false);
    classifier.add(GrassFactory.wet, this.wet, false, false);
  }


  public GrassInferencer.Evidence match(Classifier classifier) throws BayesianException {
    GrassInferencer.Evidence evidence = new GrassInferencer.Evidence();
    evidence.e$rain = classifier.match(this.rain, GrassFactory.rain);
    evidence.e$sprinkler = classifier.match(this.sprinkler, GrassFactory.sprinkler);
    evidence.e$wet = classifier.match(this.wet, GrassFactory.wet);
    return evidence;
  }

}