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
import java.util.Collection;

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
    this.infer();
  }

  @Override
  public Term getType() {
    return DwcTerm.Taxon;
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
  public Collection<Observation> toObservations() {
    Collection<Observation> obs = new ArrayList(${orderedNodes?size});

<#list orderedNodes as node>
    if (this.${node.observable.javaVariable} != null)
      obs.add(new Observation(true, ${factoryClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable}));
</#list>
    return obs;
  }

  @Override
  public void infer() throws InferenceException, StoreException {
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.analysis??>
    this.${observable.javaVariable} = (${observable.type.simpleName}) ${factoryClassName}.${observable.javaVariable}.getAnalysis().analyse(this.${observable.javaVariable});
  </#if>
</#list>
    this.analyser.analyse(this);
<#list orderedNodes + additionalNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.derivation??>
    <#assign derivation = observable.derivation>
    if (this.${node.observable.javaVariable} == null) {
      this.${node.observable.javaVariable} = ${derivation.generateClassificationTransform()};
    }
  </#if>
</#list>
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
    evidence.${node.evidence.id} = classifier.match(${factoryClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable});
</#list>
    return evidence;
  }

}