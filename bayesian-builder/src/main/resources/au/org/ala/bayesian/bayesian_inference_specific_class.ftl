package ${packageName};

import au.org.ala.bayesian.BayesianException;
import au.org.ala.bayesian.Analyser;
import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.Inference;
import au.org.ala.bayesian.Inferencer;
import au.org.ala.bayesian.Trace;

public class ${className} implements Inferencer<${classificationClassName}> {
  public final static String SIGNATURE = "${network.signature}";

  public ${className}() {
  }

  @Override
  public String getSignature() {
    return SIGNATURE;
  }

<#list inputSignatures as sig>
  <#assign signature><#list sig as s><#if s>t<#else>f</#if></#list></#assign>
  public double infer_${signature}(${parentClassName}.Evidence evidence, ${parametersClassName} parameters, Trace trace) {
<#list inputs as node>
    double ${node.CE} = <#if sig[node?index]>1.0<#else>0.0</#if>;
    double ${node.CNotE} = <#if sig[node?index]>0.0<#else>1.0</#if>;
</#list>
<#list orderedNodes as node>
    <#if node.source>
        <#if node.inference?size gt 0>
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
        <#list node.matchingInterior(signature) as inf>
            <#if inf.outcome.match>
      ${node.CE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
            </#if>
        </#list>
    }
    if (evidence.isF$${node.evidence.id}()) {
      <#list node.matchingInterior(signature) as inf>
            <#if !inf.outcome.match>
      ${node.CNotE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
            </#if>
      </#list>
    }
  <#elseif node.inference?size gt 0>
    if (evidence.isT$${node.evidence.id}()) {
      <#list node.matchingInference(signature) as inf>
          <#if inf.outcome.match>
      ${node.CE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
          </#if>
      </#list>
    }
    if (evidence.isF$${node.evidence.id}()) {
      <#list node.matchingInference(signature) as inf>
          <#if !inf.outcome.match>
      ${node.CNotE} += parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.CE}<#else>${base.CNotE}</#if></#list>;
          </#if>
      </#list>
    }
  </#if>
    if (trace != null) {
        trace.add("${node.observable.id}", ${node.CE});
        trace.add("!${node.observable.id}", ${node.CNotE});
    }
</#list>
    return <#list outputs as node>(${node.CE.id} + ${node.CNotE.id})<#if node?has_next> * </#if></#list>;
  }

</#list>

  public Inference probability(${parentClassName}.Evidence evidence, ${parametersClassName} parameters, Trace trace) {
    double p;
    double prior = <#list inputs as node><#if node?index gt 0> * </#if>parameters.${node.prior.id}</#list>;
    double ph = 0.0;
    double pe = 0.0;

<#list inputSignatures as sig>
    <#assign signature><#list sig as s><#if s>t<#else>f</#if></#list></#assign>
    if (trace != null)
        trace.push("${signature}");
    try {
        if (<#list sig as s><#assign node = inputs[s?index]>evidence.is<#if s>T<#else>F</#if>$${node.evidence.id}()<#if s?has_next> && </#if></#list>) {
          p = this.infer_${signature}(evidence, parameters, trace)<#list inputs as node> * <#if sig[node?index]>parameters.${node.prior.id}<#else>parameters.${node.invertedPrior.id}</#if></#list>;
    <#if sig?is_first>
          ph += p;
    </#if>
          pe += p;
        }
    } finally {
        if (trace != null)
            trace.pop();
    }
</#list>
    return Inference.forPEH(prior, pe, ph);
  }

  @Override
  public Inference probability(${classificationClassName} classification, Classifier classifier, Trace trace) throws BayesianException {
    if (trace != null)
        trace.push("inference");
    try {
        ${parentClassName}.Evidence evidence = classification.match(classifier);
        if (trace != null)
            trace.add("evidence", evidence);
        ${parametersClassName} params = (${parametersClassName}) classifier.getCachedParameters();
        if (params == null) {
          params = new ${parametersClassName}();
          classifier.loadParameters(params);
        }
        return this.probability(evidence, params, trace);
    } finally {
        if (trace != null)
            trace.pop();
    }
  }
}