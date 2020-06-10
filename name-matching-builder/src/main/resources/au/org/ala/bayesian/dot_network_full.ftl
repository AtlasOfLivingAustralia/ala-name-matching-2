strict digraph "${network.id}" {
<#list orderedNodes as node><#assign obs = node.observable>
    "${obs.id}" [ label = "${obs.id}" <#if node.input> shape = doublecircle </#if> color = red ]
</#list>
<#list network.edges as edge>
    "${edge.source.id}" -> "${edge.target.id}" [ color = red ]
</#list>

<#list orderedNodes as node>
    <#if node.prior??>
       <#assign value = parameters.get(node.prior.id)!-1.0>
    "${node.prior.id}" [ label = "${node.prior.id}<#if value gte 0.0> = ${value?string["0.0#######"]}</#if>" shape = doubleoctagon color = blue ]
    </#if>
    <#list node.inference as var>
        <#assign value = parameters.get(var.id)!-1.0>
    "${var.id}" [ label = "${var.id} = ${var.formula}<#if value gte 0.0> = ${value?string["0.0#######"]}</#if>" shape = octagon color = blue ]
    </#list>
</#list>

<#list orderedNodes as node>
    "${node.evidence.id}" [ label = "${node.evidence.id}" shape = square color = green ]
</#list>

    "result" [ shape = doublecircle color = blue ]
<#list inputs as node>
    "out@${node.observable.id}" [ shape = invtriangle color = blue ]
</#list>
<#list outputs as node>
    "out@${node.observable.id}" [ shape = invtriangle color = blue ]
</#list>
<#list inputs as node>
    "${node.evidence.id}" -> "out@${node.observable.id}" [ color = blue ]
    "${node.prior.id}" -> "out@${node.observable.id}" [ color = blue ]
    "${node.invertedPrior.id}" -> "out@${node.observable.id}" [ color = blue ]
    "out@${node.observable.id}" -> "result" [ color = blue ]
</#list>
<#list outputs as node>
    "${node.cE.id}" -> "out@${node.observable.id}" [ color = blue ]
    "${node.cNotE.id}" -> "out@${node.observable.id}" [ color = blue ]
    "out@${node.observable.id}" -> "result" [ color = blue ]
</#list>

<#list orderedNodes as node>
    "${node.evidence.id}" [ label = "${node.evidence.id}" shape = square color = green ]
    "${node.cE.id}" [ label = "${node.cE.id}" color = blue ]
    "${node.cNotE.id}" [ label = "${node.cNotE.id}" color = blue ]
    <#if node.invertedPrior??>
        <#assign value = parameters.get(node.invertedPrior.id)!-1.0>
    "${node.invertedPrior.id}" [ label = "${node.invertedPrior.id}<#if value gte 0.0> = ${value?string["0.0#######"]}</#if>" color = blue ]
    </#if>
    <#list node.interior as var>
        <#assign value = parameters.get(var.id)!-1.0>
    "${var.id}" [ label = "${var.id} = ${var.formula}<#if value gte 0.0> = ${value?string["0.0#######"]}</#if>" color = blue ]
    </#list>
    <#list node.factors as inf>
    "calc@${inf.id}" [ label = "calc@${inf.id}" shape = invtriangle color = blue ]
    </#list>
</#list>
<#list orderedNodes as node>
    "${node.observable.id}" -> "${node.evidence.id}" [ color = green ]
    "${node.evidence.id}" -> "${node.cE.id}" [ label = "true" color = green ]
    "${node.evidence.id}" -> "${node.cNotE.id}" [ label = "false" color = green ]
    <#if node.invertedPrior?? && node.prior??>
        "${node.prior.id}" -> "${node.invertedPrior.id}" [ color = blue ]
    </#if>
    <#list node.inference as var>
        <#if var.derived>
            <#list var.derivedFrom as vs>
    "${vs.id}" -> "${var.id}" [ color = blue ]
            </#list>
        </#if>
    </#list>
    <#list node.interior as var>
        <#if var.derived>
            <#list var.derivedFrom as vs>
    "${vs.id}" -> "${var.id}"
            </#list>
        </#if>
    </#list>
    <#list node.inference as inf>
     "calc@${inf.id}" -> <#if inf.outcome.match>"${node.cE.id}"<#else>"${node.cNotE}"</#if> [ color = blue ]
     "${inf.id}" -> "calc@${inf.id}"
        <#list inf.contributors as cont><#assign cn = nodes[cont.observable.id]>
     "<#if cont.match>${cn.cE.id}<#else>${cn.cNotE.id}</#if>" -> "calc@${inf.id}" [ color = blue ]
        </#list>
    </#list>
</#list>

}