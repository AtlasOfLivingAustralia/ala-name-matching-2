<#assign analyserType><#if analyserImplementationClassName??>${analyserImplementationClassName}<#else>Analyser<${className}></#if></#assign>
package ${packageName};

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Issues;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.Observation;
import au.org.ala.bayesian.StoreException;

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
  private ${analyserType} analyser;
  private Issues issues;
<#list classificationVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
</#list>
<#list modifications as modifier>
  private Function<${className}, ${className}> ${modifier.javaConstant} =
    c -> {
      ${className} nc;
  <#list modifier.generate(compiler, "c", "nc") as statement>
      ${statement}
  </#list>
  <#if modifier.issue??>
      nc.addIssue(${factoryClassName}.${modifier.issue.javaConstant});
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

  public ${className}(${analyserType} analyser) {
    this.analyser = ${factoryClassName}.instance().createAnalyser();
    this.issues = new Issues();
<#list classificationVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
</#list>
  }

  public ${className}() {
    this(${factoryClassName}.instance().createAnalyser());
  }

  public ${className}(Classifier classifier, ${analyserType} analyser) throws InferenceException, StoreException {
    this(analyser);
    this.read(classifier, true);
    this.inferForIndex();
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
  public Term getType() {
    return ${factoryClassName}.CONCEPT;
  }

  @Override
  public ${analyserType} getAnalyser() {
    return this.analyser;
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
  public void inferForIndex() throws InferenceException, StoreException {
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.analysis??>
    this.${observable.javaVariable} = (${observable.type.simpleName}) ${factoryClassName}.${observable.javaVariable}.getAnalysis().analyse(this.${observable.javaVariable});
  </#if>
</#list>
    this.analyser.analyseForIndex(this);
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.derivation?? && !observable.derivation.generator>
    <#assign derivation = observable.derivation>
    <#if derivation.hasTransform()>
    if (this.${node.observable.javaVariable} == null) {
      this.${node.observable.javaVariable} = ${derivation.generateClassificationTransform()};
    }
    </#if>
  </#if>
</#list>
  }


  @Override
  public void inferForSearch() throws InferenceException, StoreException {
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.analysis??>
    this.${observable.javaVariable} = (${observable.type.simpleName}) ${factoryClassName}.${observable.javaVariable}.getAnalysis().analyse(this.${observable.javaVariable});
  </#if>
</#list>
        this.analyser.analyseForSearch(this);
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.derivation?? && !observable.derivation.generator>
    <#assign derivation = observable.derivation>
    <#if derivation.hasTransform()>
    if (this.${node.observable.javaVariable} == null) {
      this.${node.observable.javaVariable} = ${derivation.generateClassificationTransform()};
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
    if (<#list modifier.conditions as var><#if var?index gt 0><#if modifier.anyCondition> || <#else> && </#if></#if>this.${var.javaVariable} != null</#list>)
      ml.add(${modifier.javaConstant});
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
    if (<#list modifier.conditions as var><#if var?index gt 0><#if modifier.anyCondition> || <#else> && </#if></#if>this.${var.javaVariable} != null</#list>)
      ml.add(${modifier.javaConstant});
    </#list>
    if (ml.size() > 1)
      modifications.add(ml);
</#list>
</#if>
    return modifications;
  }

  @Override
  public void read(Classifier classifier, boolean overwrite) throws InferenceException {
<#list orderedNodes + additionalNodes as node>
    if (overwrite || this.${node.observable.javaVariable} == null) {
      this.${node.observable.javaVariable} = classifier.get(${factoryClassName}.${node.observable.javaVariable});
    }
</#list>
  }

  @Override
  public void write(Classifier classifier, boolean overwrite) throws InferenceException, StoreException{
    if(overwrite){
<#list orderedNodes + additionalNodes as node>
      classifier.replace(${factoryClassName}.${node.observable.javaVariable},this.${node.observable.javaVariable});
</#list>
    } else {
<#list orderedNodes + additionalNodes as node>
      classifier.add(${factoryClassName}.${node.observable.javaVariable},this.${node.observable.javaVariable});
</#list>
    }
  }


  public ${inferencerClassName}.Evidence match(Classifier classifier) throws StoreException, InferenceException {
    ${inferencerClassName}.Evidence evidence = new ${inferencerClassName}.Evidence();
<#list orderedNodes as node>
  <#assign observable = node.observable >
    evidence.${node.evidence.id} = classifier.match(this.${node.observable.javaVariable}, ${factoryClassName}.${node.observable.javaVariable}<#if network.nameObservable?? && network.altNameObservable?? && node.observable.id == network.nameObservable.id>, ${factoryClassName}.${network.altNameObservable.javaVariable}</#if>);
</#list>
    return evidence;
  }

}