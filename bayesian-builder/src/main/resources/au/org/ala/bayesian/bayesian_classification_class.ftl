<#assign analyserType><#if analyserImplementationClassName??>${analyserImplementationClassName}<#else>Analyser<${className}></#if></#assign>
package ${packageName};

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Hints;
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

<#list imports as import>
import ${import};
</#list>

public class ${className}<#if superClassName??> extends ${superClassName}</#if> implements Classification<${className}> {
  private Issues issues;
  private Hints<${className}> hints;

<#list classificationVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
</#list>
<#list modifications as modifier>
  private static Function<${className}, ${className}> ${modifier.javaConstant} =
    c -> {
      ${className} nc;
  <#list modifier.generate(compiler, "c", "nc") as statement>
      ${statement}
  </#list>
  <#if modifier.issues??>
    <#list modifier.issues as issue>
      nc.addIssue(${factoryClassName}.${issue.javaConstant});
    </#list>
  </#if>
      return nc;
    };
</#list>

<#list orderedNodes as node>
  public ${node.observable.type.name} ${node.observable.javaVariable};
</#list>
<#if additionalNodes?size gt 0>
  // Additional stored classification information not used in inference
</#if>
<#list additionalNodes as node>
  public ${node.observable.type.name} ${node.observable.javaVariable};
</#list>

  public ${className}() {
    this.issues = new Issues();
    this.hints = new Hints<>();
<#list classificationVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
</#list>
  }

  public ${className}(Classifier classifier) throws BayesianException {
    this();
    this.read(classifier, true);
  }

  @Override
  @SneakyThrows
  public ${className} clone() {
      ${className} clone = (${className}) super.clone();
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
  public Hints<${className}> getHints() {
    return this.hints;
  }

  @Override
  public <T> void addHint(Observable observable, T value) {
        this.hints.addHint(observable, value);
  }

  @Override
  public Term getType() {
    return ${factoryClassName}.CONCEPT;
  }

  @Override
  public Issues getIssues() {
    return this.issues;
  }


  @Override
  public String getIdentifier() {
    return <#if network.identifierObservable??>this.${network.identifierObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  public String getName() {
    return <#if network.nameObservable??>this.${network.nameObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  public String getParent() {
    return <#if network.parentObservable??>this.${network.parentObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  public String getAccepted() {
    return <#if network.acceptedObservable??>this.${network.acceptedObservable.javaVariable}<#else>null</#if>;
  }

  @Override
  public Collection<Observation> toObservations() {
    Collection<Observation> obs = new ArrayList(${orderedNodes?size});

<#list orderedNodes as node>
    if (this.${node.observable.javaVariable} != null)
      obs.add(new Observation(true, ${factoryClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable}));
</#list>
    return obs;
  }

  @Override
  public void inferForSearch(Analyser<${className}> analyser) throws BayesianException {
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.analysis??>
    this.${observable.javaVariable} = ${factoryClassName}.${observable.javaVariable}.analyse(this.${observable.javaVariable});
  </#if>
</#list>
<#list derivationOrder as observable>
  <#assign derivation = observable.derivation>
  <#if derivation.preAnalysis>
    <#if derivation.hasTransform()>
    if (this.${observable.javaVariable} == null) {
      this.${observable.javaVariable} = ${derivation.generateClassificationTransform()};
    }
    </#if>
  </#if>
</#list>
    analyser.analyseForSearch(this);
<#list derivationOrder as observable>
  <#assign derivation = observable.derivation>
  <#if derivation.postAnalysis>
    <#if derivation.hasTransform()>
    if (this.${observable.javaVariable} == null) {
      this.${observable.javaVariable} = ${derivation.generateClassificationTransform()};
    }
    </#if>
  </#if>
</#list>
  }

  @Override
  public List<List<Function<${className}, ${className}>>> searchModificationOrder() {
        List<List<Function<${className}, ${className}>>> modifications = new ArrayList();
<#if network.sourceModifiers?? && network.sourceModifiers?size gt 0>
    List<Function<${className}, ${className}>> ml;
  <#list network.sourceModifiers as ml>
    ml = new ArrayList();
    ml.add(null);
    <#list ml as modifier>
      <#assign check = modifier.buildCheck(compiler, "this", true)>
      <#if modifier.buildCheck(compiler, "this", true)??>
    if (${modifier.buildCheck(compiler, "this", true)})
      ml.add(${modifier.javaConstant});
      <#else>
    ml.add(${modifier.javaConstant});
      </#if>
    </#list>
    if (ml.size() > 1)
      modifications.add(ml);
  </#list>
</#if>
    return modifications;
  }

  @Override
  public List<List<Function<${className}, ${className}>>> matchModificationOrder() {
    List<List<Function<${className}, ${className}>>> modifications = new ArrayList();
<#if network.matchModifiers?? && network.matchModifiers?size gt 0>
    List<Function<${className}, ${className}>> ml;
<#list network.matchModifiers as ml>
    ml = new ArrayList();
    ml.add(null);
    <#list ml as modifier>
      <#if modifier.buildCheck(compiler, "this", true)??>
    if (${modifier.buildCheck(compiler, "this", true)})
      ml.add(${modifier.javaConstant});
      <#else>
    ml.add(${modifier.javaConstant});
      </#if>
    </#list>
    if (ml.size() > 1)
      modifications.add(ml);
</#list>
</#if>
    return modifications;
  }


  @Override
  public List<List<Function<${className}, ${className}>>> hintModificationOrder() {
    List<List<Function<${className}, ${className}>>> modifications = new ArrayList();
<#list orderedNodes as node>
    this.hints.buildModifications(${factoryClassName}.${node.observable.javaVariable}, ${node.observable.type.name}.class, (c, v) -> { c.${node.observable.javaVariable} = v; }, modifications);
</#list>
    return modifications;
  }

  @Override
  public void read(Classifier classifier, boolean overwrite) throws BayesianException {
<#list orderedNodes + additionalNodes as node>
    if (overwrite || this.${node.observable.javaVariable} == null) {
      this.${node.observable.javaVariable} = classifier.get(${factoryClassName}.${node.observable.javaVariable});
    }
</#list>
  }

  @Override
  public void write(Classifier classifier, boolean overwrite) throws BayesianException {
    if(overwrite){
<#list orderedNodes + additionalNodes as node>
      classifier.clear(${factoryClassName}.${node.observable.javaVariable});
</#list>
    }
<#list orderedNodes + additionalNodes as node>
    classifier.add(${factoryClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable}, false, false);
</#list>
  }


  public ${inferencerClassName}.Evidence match(Classifier classifier) throws BayesianException {
    ${inferencerClassName}.Evidence evidence = new ${inferencerClassName}.Evidence();
<#list orderedNodes as node>
  <#assign observable = node.observable >
    evidence.${node.evidence.id} = classifier.match(this.${observable.javaVariable}<#list observable.matchers as matcher>, ${factoryClassName}.${matcher.javaVariable}</#list>);
</#list>
    return evidence;
  }

}