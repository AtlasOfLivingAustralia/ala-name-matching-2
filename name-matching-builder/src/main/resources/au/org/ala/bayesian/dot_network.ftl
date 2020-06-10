strict digraph "${network.id}" {
  subgraph clusterNetwork {
<#list orderedNodes as node><#assign obs = node.observable>
    "${obs.id}" [ label = "${obs.id}" <#if node.input> shape = doublecircle </#if> ]
</#list>
<#list network.edges as edge>
    "${edge.source.id}" -> "${edge.target.id}"
</#list>
  }
}