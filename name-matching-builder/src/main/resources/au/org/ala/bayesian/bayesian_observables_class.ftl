<#import "derivations.ftl" as derivations>
package ${packageName};

import au.org.ala.bayesian.Normaliser;
import au.org.ala.bayesian.Observable;
import static au.org.ala.names.model.ExternalContext.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ${className} {
<#list network.normalisers as normaliser>
  <#if normaliser.description??>
    /** ${normaliser.description} */
  </#if>
  public static final Normaliser ${normaliser.javaVariable} = ${normaliser.creator};
</#list>

  <#list network.observablesById as observable>
    <#if observable.description??>
  /** ${observable.description} */
    </#if>
  public static final Observable ${observable.javaVariable} = new Observable(
      "${observable.id}",
      <#if observable.uri??>URI.create("${observable.uri}")<#else>null</#if>,
      ${observable.type.name}.class,
      Observable.Style.${observable.style},
      <#if observable.normaliser??>${observable.normaliser.javaVariable}<#else>null</#if>,
      ${observable.required?c}
    );
  </#list>

  public static List<Observable> OBSERVABLES = Collections.unmodifiableList(Arrays.asList(
  <#list network.observablesById as observable>
    ${observable.javaVariable}<#if observable?has_next>,</#if>
  </#list>
  ));

  static {
<#list network.observablesById as observable>
  <#list externalContexts as context>
    ${observable.javaVariable}.setExternal(${context.name()}, "${observable.getExternal(context)}");
  </#list>
</#list>
  }
}
