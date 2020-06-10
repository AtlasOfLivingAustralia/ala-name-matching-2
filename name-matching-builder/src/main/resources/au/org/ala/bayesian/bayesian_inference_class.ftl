package ${packageName};

public class ${className} {
  public ${parameterClassName} parameters;

  public double infer(Evidence evidence<#list inputs as inp>, double ${inp.cE}</#list>) {
<#list orderedNodes as node>
    <#if node.source>
        <#if node.inference?size == 0>
    double ${node.cNotE} = 1.0 - ${node.cE};
        <#else>
    double ${node.cE} = 0;
    double ${node.cNotE} = 0;
        </#if>
    <#else>
    double ${node.cE} = evidence.isT$${node.observable.id}();
    double ${node.cNotE} = evidence.isF$${node.observable.id}();
    </#if>
</#list>
<#list orderedNodes as node>
  <#if !node.source>
    // Ignoring non-base ${node.observable.id}
  <#elseif node.interior?size gt 0>
    if (evidence.${node.evidence.id} == null || evidence.${node.evidence.id}) {
        <#list node.interior as inf>
            <#if inf.outcome.match>
      ${node.cE} += this.parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.cE}<#else>${base.cNotE}</#if></#list>;
            </#if>
        </#list>
    }
    if (evidence.${node.evidence.id} == null || !evidence.${node.evidence.id}) {
      <#list node.interior as inf>
            <#if !inf.outcome.match>
      ${node.cNotE} += this.parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.cE}<#else>${base.cNotE}</#if></#list>;
            </#if>
      </#list>
    }
  <#elseif node.inference?size gt 0>
    if (evidence.${node.evidence.id} == null || evidence.${node.evidence.id}) {
      <#list node.inference as inf>
          <#if inf.outcome.match>
      ${node.cE} += this.parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.cE}<#else>${base.cNotE}</#if></#list>;
          </#if>
      </#list>
    }
    if (evidence.${node.evidence.id} == null || !evidence.${node.evidence.id}) {
      <#list node.inference as inf>
          <#if !inf.outcome.match>
      ${node.cNotE} += this.parameters.${inf.id}<#list inf.contributors as c><#assign base = nodes[c.observable.id]> * <#if c.match>${base.cE}<#else>${base.cNotE}</#if></#list>;
          </#if>
      </#list>
    }
  </#if>
</#list>
    return <#list outputs as node><#if node?index gt 0> * </#if>(${node.cE.id} + ${node.cNotE.id})</#list><#list inputs as node> * (this.parameters.${node.prior.id} * ${node.cE.id} + this.parameters.${node.invertedPrior.id} * ${node.cNotE})</#list>;
  }

  public double probability(Evidence evidence) {
    double p;
    double ph = 0.0;
    double pe = 0.0;

<#list inputSignatures as sig>
    p = <#list sig as s><#assign node = inputs[s?index]>evidence.is<#if s>T<#else>F</#if>$${node.observable.id}() * </#list>this.infer(evidence<#list sig as s>, <#if s>1.0<#else>0.0</#if></#list>);
    <#if sig?index == 0>
    ph += p;
    </#if>
    pe += p;
</#list>
    return pe == 0.0 ? 0.0 : ph / pe;
  }

  public static class Evidence {
<#list orderedNodes as node>
    public Boolean ${node.evidence.id};
</#list>

<#list orderedNodes as node>
    public double isT$${node.observable.id}() {
      return this.${node.evidence.id} == null || this.${node.evidence.id} ? 1.0 : 0.0;
    }

    public double isF$${node.observable.id}() {
      return this.${node.evidence.id} == null || !this.${node.evidence.id} ? 1.0 : 0.0;
    }

</#list>
  }
}