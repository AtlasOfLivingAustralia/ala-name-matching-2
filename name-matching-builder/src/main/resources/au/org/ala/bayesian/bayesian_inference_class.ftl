package ${packageName};

import au.org.ala.bayesian.Classifier;
import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.Inferencer;

public class ${className}<#if superClassName??> extends ${superClassName}</#if> implements Inferencer<${classificationClassName}, ${parametersClassName}> {

  public double infer(Evidence evidence, ${parametersClassName} parameters<#list inputs as inp>, double ${inp.CE}</#list>) {
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

  public double probability(Evidence evidence, ${parametersClassName} parameters) {
    double p;
    double ph = 0.0;
    double pe = 0.0;

<#list inputSignatures as sig>
    p = <#list sig as s><#assign node = inputs[s?index]>(evidence.is<#if s>T<#else>F</#if>$${node.evidence.id}() ? 1.0 : 0.0) * </#list>this.infer(evidence, parameters<#list sig as s>, <#if s>1.0<#else>0.0</#if></#list>);
    <#if sig?index == 0>
    ph += p;
    </#if>
    pe += p;
</#list>
    return pe == 0.0 ? 0.0 : ph / pe;
  }

  @Override
  public double probability(${classificationClassName} classification, Classifier classifier, ${parametersClassName} parameters) throws InferenceException {
    Evidence evidence = classification.match(classifier);
    return this.probability(evidence, parameters);
  }

  public static class Evidence {
<#list orderedNodes as node>
    public Boolean ${node.evidence.id};
</#list>

<#list orderedNodes as node>
    public boolean isT$${node.evidence.id}() {
      return this.${node.evidence.id} == null || this.${node.evidence.id};
    }

    public boolean isF$${node.evidence.id}() {
      return this.${node.evidence.id} == null || !this.${node.evidence.id};
    }

</#list>
  }
}