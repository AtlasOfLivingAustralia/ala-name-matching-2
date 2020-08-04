strict digraph "${network.id}" {
  compound = true;
  newrank = true;

<#list orderedNodes as node>
  subgraph "cluster:${node.observable.id}" {
    edge [ arrowsize=0.5 ];
    label = "${node.observable.id}";
    color = lightgrey;
    "${node.observable.id}" [ label = "${node.observable.label}", shape = <#if node.input>doublecircle<#else>circle</#if>, color = red ]
    { rank=same;
      "${node.CE.id}" [ label = "${node.formula}" color = green ]
      "${node.CNotE.id}" [ label = "${node.notFormula}", color = green ]
      "${node.observable.id}:collector" [ style = invis, color = lightgrey ]
    }
    "${node.observable.id}" -> "${node.CE.id}" [ color = blue, arrowtail = dot ]
    "${node.observable.id}" -> "${node.CNotE.id}" [ color = blue, arrowtail = odot ]
    <#if node.prior??>
     { "${node.prior.id}" [ label = "${node.formula}" shape = doubleoctagon, color = blue ] }
     "${node.observable.id}" -> "${node.prior.id}" [ style = invis ]
    </#if>
    <#list node.inference as inf>
     "${inf.id}" [ label = "${inf.formula}" shape = box, color = blue ]
     "${node.observable.id}" -> "${inf.id}" [ style = invis ]
        <#if inf.outcome.match>
     "${inf.id}" -> "${node.CE.id}" [ color = blue ]
        <#else>
     "${inf.id}" -> "${node.CNotE.id}"
         </#if>
        <#list inf.contributors as c> [ color = blue ]
            <#assign base = nodes[c.observable.id]>
      <#if c.match>"${base.CE}"<#else>"${base.CNotE}"</#if> -> "${inf.id}" [ color = green ]
        </#list>
     </#list>
  }
</#list>

  "result:" [ label = "p", shape = doublecircle, style = filled, color = green, fontcolor = white ]
<#list outputs as node>
  "${node.CE.id}" -> "result:" [ color = green ]
  "${node.CNotE.id}" -> "result:" [ color = green ]
</#list>
<#list inputs as node>
  "${node.prior.id}" -> "result:" [ color = green ]
  "${node.CE.id}" -> "result:" [ color = green ]
  "${node.CNotE}" -> "result:" [ color = green ]
</#list>


<#list network.edges as edge>
    "${edge.source.id}:collector" -> "${edge.target.id}" [ color = red, ltail = "cluster:${edge.source.id}", lhead = "cluster:${edge.target.id}", minlen = 2 ]
</#list>

}