package ${packageName};

import au.org.ala.bayesian.Classification;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Observation;

import java.util.ArrayList;
import java.util.Collection;

import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

<#list classificationVariables as variable>
import ${variable.clazz.name};
</#list>

public class ${className} extends Classification {
<#list classificationVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
</#list>

<#list orderedNodes as node>
  public ${node.observable.type.name} ${node.observable.javaVariable};
</#list>

  public ${className}() {
<#list classificationVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
</#list>
  }

  public ${className}(Classifier classifier) throws InferenceException {
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
    Collection<Observation> obs = new ArrayList(${orderedNodes?size});

<#list orderedNodes as node>
    if (this.${node.observable.javaVariable} != null)
      obs.add(new Observation(true, ${observablesClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable}));
</#list>
    return obs;
  }

  @Override
  public void infer() throws InferenceException {
<#list orderedNodes as node>
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
  public void populate(Classifier classifier, boolean overwrite) throws InferenceException {
<#list orderedNodes as node>
    if (overwrite || this.${node.observable.javaVariable} == null) {
      this.${node.observable.javaVariable} = classifier.get(${observablesClassName}.${node.observable.javaVariable});
    }
</#list>
  }

  public ${inferenceClassName}.Evidence match(Classifier classifier) throws InferenceException {
    ${inferenceClassName}.Evidence evidence = new ${inferenceClassName}.Evidence();
<#list orderedNodes as node>
    evidence.${node.evidence.id} = classifier.match(${observablesClassName}.${node.observable.javaVariable}, this.${node.observable.javaVariable});
</#list>
    return evidence;
  }

}