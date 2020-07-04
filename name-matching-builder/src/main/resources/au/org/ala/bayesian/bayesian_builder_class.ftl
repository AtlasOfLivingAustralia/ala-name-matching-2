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

public class ${className} extends Builder<${parametersClassName}> {
  <#list builderVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
  </#list>

  public ${className}() {
  <#list builderVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
  </#list>
  }

  @Override
  public void infer(Classifier<?> classifier) throws InferenceException, StoreException {
<#list orderedNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.derivation??>
    <#assign derivation = observable.derivation>
    <#if derivation.hasExtra()>
    String e_${node?index} = ${derivation.generateExtra("classifier", observablesClassName)};
    </#if>
    for (String v: ${derivation.generateValues("classifier", observablesClassName)}) {
      String d = ${derivation.generateBuilderTransform("v", "e_${node?index}", "classifier")};
      classifier.add(${observablesClassName}.${observable.javaVariable}, d);
    }
  </#if>
</#list>
  }

    @Override
    public void expand(Classifier<?> classifier, Deque<Classifier<?>> parents) throws InferenceException, StoreException {
<#list orderedNodes as node>
  <#assign observable = node.observable >
  <#if observable?? && observable.base??>
    <#assign derivation = observable.base>
    <#assign docVar = "classifier">
    <#assign condition = derivation.generateCondition("c", "classifier", observablesClassName, "parents")>
    <#if condition??>
      <#assign docVar = "d_${node?index}">
    Optional<Classifier<?>> ${docVar} = parents.stream().filter(c -> ${condition}).findFirst();
    if (${docVar}.isPresent()){
    <#if derivation.hasExtra()>
      String e_${node?index} = ${derivation.generateExtra("classifier", observablesClassName)};
    </#if>
      for(String v: ${derivation.generateValues("${docVar}.get()", observablesClassName)}){
        String d = ${derivation.generateBuilderTransform("v", "e_${node?index}", "classifier")};
        classifier.add(${observablesClassName}.${observable.javaVariable}, d);
      }
    }
    </#if>
  </#if>
</#list>
  }

  @Override
  public ${parametersClassName} createParameters() {
      return new ${parametersClassName}();
  }

  @Override
  public void calculate(${parametersClassName} parameters, ParameterAnalyser analyser, Classifier<?> classifier) throws InferenceException, StoreException {
    <#list inputs as inc>
    parameters.${inc.prior.id} = analyser.computePrior(analyser.getObservation(true, ${observablesClassName}.${inc.observable.javaVariable}, classifier));
    </#list>
    <#list orderedNodes as node>
        <#list node.inference as inf>
            <#if !inf.derived>
    parameters.${inf.id} = analyser.computeConditional(analyser.getObservation(true, ${observablesClassName}.${inf.outcome.observable.javaVariable}, classifier) <#list inf.contributors as c>, analyser.getObservation(${c.match?c}, ${observablesClassName}.${c.observable.javaVariable}, classifier)</#list>);
            </#if>
        </#list>
    </#list>
  }
}
