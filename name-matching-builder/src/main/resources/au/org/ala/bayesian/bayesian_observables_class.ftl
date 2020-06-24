<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.Observable;
import static au.org.ala.names.model.ExternalContext.*;

import java.net.URI;

public class ${className} {
  <#list orderedNodes as node>
    <#assign observable = node.observable >
    <#if observable.description??>
  /** ${observable.description} */
    </#if>
  public static final Observable ${node.classifier.id} = new Observable("${observable.id}"<#if observable.uri??>, URI.create("${observable.uri}")</#if>);
  </#list>

  static {
<#list orderedNodes as node>
  <#assign observable = node.observable >
  <#list externalContexts as context>
    ${node.classifier.id}.setExternal(${context.name()}, "${observable.getExternal(context)}");
  </#list>
</#list>
  }
}
