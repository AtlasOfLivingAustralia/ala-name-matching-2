<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Builder;

import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;

<#list builderVariables as variable>
import ${variable.clazz.name};
</#list>

public class ${className} implements Builder<${parametersClassName}> {
  <#list builderVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
  </#list>

  public ${className}() {
  <#list builderVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
  </#list>
  }

  @Override
  public void infer(Classifier classifier) throws InferenceException, StoreException {
<#list orderedNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.derivation??>
    <#assign derivation = observable.derivation>
    <#if derivation.hasExtra()>
    ${derivation.extra.type.name} e_${node?index} = ${derivation.generateExtra("classifier", factoryClassName)};
    </#if>
    for (Object v: ${derivation.generateValues("classifier", factoryClassName)}) {
      Object d = ${derivation.generateBuilderTransform("v", "e_${node?index}", "classifier")};
      classifier.add(${factoryClassName}.${observable.javaVariable}, d);
    }
  </#if>
</#list>
  }

    @Override
    public void expand(Classifier classifier, Deque<Classifier> parents) throws InferenceException, StoreException {
<#list orderedNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.base??>
    <#assign derivation = observable.base>
    <#assign docVar = "classifier">
    <#assign condition = derivation.generateCondition("c", "classifier", factoryClassName, "parents")>
    <#if condition??>
      <#assign docVar = "d_${node?index}">
    Optional<Classifier> ${docVar} = parents.stream().filter(c -> ${condition}).findFirst();
    if (${docVar}.isPresent()){
    <#if derivation.hasExtra()>
      ${derivation.extra.type.name} e_${node?index} = ${derivation.generateExtra("classifier", factoryClassName)};
    </#if>
      for(Object v: ${derivation.generateValues("${docVar}.get()", factoryClassName)}){
        Object d = ${derivation.generateBuilderTransform("v", "e_${node?index}", "classifier")};
        classifier.add(${factoryClassName}.${observable.javaVariable}, d);
      }
    }
    </#if>
  </#if>
</#list>
  }

  @Override
  public void calculate(${parametersClassName} parameters, ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
    <#list inputs as inc>
    parameters.${inc.prior.id} = analyser.computePrior(analyser.getObservation(true, ${factoryClassName}.${inc.observable.javaVariable}, classifier));
    </#list>
    <#list orderedNodes as node>
        <#list node.inference as inf>
            <#if !inf.derived>
    parameters.${inf.id} = analyser.computeConditional(analyser.getObservation(true, ${factoryClassName}.${inf.outcome.observable.javaVariable}, classifier) <#list inf.contributors as c>, analyser.getObservation(${c.match?c}, ${factoryClassName}.${c.observable.javaVariable}, classifier)</#list>);
            </#if>
        </#list>
    </#list>
  }
}
