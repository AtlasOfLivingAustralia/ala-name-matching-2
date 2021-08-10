package ${packageName};

import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.StoreException;

public class ${className} implements Inferencer<${classificationClassName}> {
  public final static String SIGNATURE = "${network.signature}";

  private ThreadLocal<${parametersClassName}> parameters = ThreadLocal.withInitial(() -> new ${parametersClassName}());

  public ${className}() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

  public double infer(${parentClassName}.Evidence evidence, ${parametersClassName} parameters<#list inputs as inp>, double ${inp.CE}</#list>) {
<#list orderedNodes as node>
    <#if node.source>
        <#if node.inference?size == 0>
    double ${node.CNotE} = 1.0 - ${node.CE};
        <#else>
    double ${node.CE} = 0.0;
    double ${node.CNotE} = 0.0;
        </#if>
    <#else>
    double ${node.CE} = evidence.isT$${node.evidence.id}() ? 1.0 : 0.0;
    double ${node.CNotE} = evidence.isF$${node.evidence.id}() ? 1.0 : 0.0;
    </#if>
</#list>
<#list orderedNodes as node>
  <#if !node.source>
    // Ignoring non-base ${node.observable.id}
  <#elseif node.interior?size gt 0>
    if (evidence.isT$${node.evidence.id}()) {
        <#list node.interior as inf>
            <#if inf.outcome.match>
      ${node.CE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
            </#if>
        </#list>
    }
    if (evidence.isF$${node.evidence.id}()) {
      <#list node.interior as inf>
            <#if !inf.outcome.match>
      ${node.CNotE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
            </#if>
      </#list>
    }
  <#elseif node.inference?size gt 0>
    if (evidence.isT$${node.evidence.id}()) {
      <#list node.inference as inf>
          <#if inf.outcome.match>
      ${node.CE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
          </#if>
      </#list>
    }
    if (evidence.isF$${node.evidence.id}()) {
      <#list node.inference as inf>
          <#if !inf.outcome.match>
      ${node.CNotE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
          </#if>
      </#list>
    }
  </#if>
</#list>
    return <#list outputs as node><#if node?index gt 0> * </#if>(${node.CE.id} + ${node.CNotE.id})</#list><#list inputs as node> * (parameters.${node.prior.id} * ${node.CE.id} + parameters.${node.invertedPrior.id} * ${node.CNotE})</#list>;
  }

  public Inference probability(${parentClassName}.Evidence evidence, ${parametersClassName} parameters) {
    double p;
    double prior = <#list inputs as node><#if node?index gt 0> * </#if>parameters.${node.prior.id}</#list>;
    double ph = 0.0;
    double pe = 0.0;

<#list inputSignatures as sig>
    p = <#list sig as s><#assign node = inputs[s?index]>(evidence.is<#if s>T<#else>F</#if>$${node.evidence.id}() ? 1.0 : 0.0) * </#list>this.infer(evidence, parameters<#list sig as s>, <#if s>1.0<#else>0.0</#if></#list>);
    <#if sig?index == 0>
    ph += p;
    </#if>
    pe += p;
</#list>
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(${classificationClassName} classification, Classifier classifier) throws StoreException, InferenceException {
    ${parentClassName}.Evidence evidence = classification.match(classifier);
    ${parametersClassName} params = this.parameters.get();
    classifier.loadParameters(params);
    return this.probability(evidence, params);
  }

}