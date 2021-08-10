<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.bayesian.Parameters;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.builder.Builder;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

<#list imports as import>
import ${import};
</#list>

public class ${className} implements Builder {
  // Assumed to be stateless
  private static final Builder[] BUILDERS = new Builder[] {
<#list children as child>
    new ${className}_${child.network.signature}()<#if child_has_next>,</#if>
</#list>
  };

  private Map<String, Builder> subBuilders;

  <#list builderVariables as variable>
  private ${variable.clazz.simpleName} ${variable.name};
  </#list>

  public ${className}() {
  <#list builderVariables as variable>
    this.${variable.name} = new ${variable.clazz.simpleName}();
  </#list>
    this.subBuilders = new HashMap<>(BUILDERS.length);
    for (Builder b: BUILDERS)
      this.subBuilders.put(b.getSignature(), b);
  }

  @Override
  public String getSignature() {
    return null;
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
  public String buildSignature(Classifier classifier) {
    char[] sig = new char[${erasureStructure?size}];
<#list erasureStructure as erasure>
    sig[${erasure_index}] = (<#list erasure as observable>classifier.has(${factoryClassName}.${observable.javaVariable})<#if observable_has_next> || </#if></#list>) ? 'T' : 'F';
</#list>
    return new String(sig);
  }

  @Override
  public Parameters calculate(ParameterAnalyser analyser, Classifier classifier) throws InferenceException, StoreException {
    Builder sub = this.subBuilders.get(classifier.getSignature());
    if (sub == null)
        throw new IllegalArgumentException("Signature " + classifier.getSignature() + " not found");
    return sub.calculate(analyser, classifier);
  }
}
