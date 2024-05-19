error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/StructWriters.java
jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/StructWriters.java
### java.lang.Exception: Unexpected symbol at word pos: '35'. Line: '<##list ["Nullable", "Single"] as mode>'

Java indexer failed with and exception.
```Java
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

<@pp.dropOutputFile />
<#list ["Nullable", "Single"] as mode>
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/${mode}StructWriter.java" />
<#assign index = "idx()">
<#if mode == "Single">
<#assign containerClass = "NonNullableStructVector" />
<#else>
<#assign containerClass = "StructVector" />
</#if>

<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />
import java.util.Map;
import java.util.HashMap;

import org.apache.arrow.vector.holders.RepeatedStructHolder;
import org.apache.arrow.vector.AllocationHelper;
import org.apache.arrow.vector.complex.reader.FieldReader;
import org.apache.arrow.vector.complex.writer.FieldWriter;

/*
 * This class is generated using FreeMarker and the ${.template_name} template.
 */
@SuppressWarnings("unused")
public class ${mode}StructWriter extends AbstractFieldWriter {

  protected final ${containerClass} container;
  private int initialCapacity;
  private final Map<String, FieldWriter> fields = new HashMap<>();
  public ${mode}StructWriter(${containerClass} container) {
    <#if mode == "Single">
    if (container instanceof StructVector) {
      throw new IllegalArgumentException("Invalid container: " + container);
    }
    </#if>
    this.container = container;
    this.initialCapacity = 0;
    for (Field child : container.getField().getChildren()) {
      MinorType minorType = Types.getMinorTypeForArrowType(child.getType());
      switch (minorType) {
      case STRUCT:
        struct(child.getName());
        break;
      case LIST:
        list(child.getName());
        break;
      case UNION:
        FieldType fieldType = new FieldType(addVectorAsNullable, MinorType.UNION.getType(), null, null);
        UnionWriter writer = new UnionWriter(container.addOrGet(child.getName(), fieldType, UnionVector.class), getNullableStructWriterFactory());
        fields.put(handleCase(child.getName()), writer);
        break;
<#list vv.types as type><#list type.minor as minor>
<#assign lowerName = minor.class?uncap_first />
<#if lowerName == "int" ><#assign lowerName = "integer" /></#if>
<#assign upperName = minor.class?upper_case />
      case ${upperName}: {
        <#if minor.typeParams?? >
        ${minor.arrowType} arrowType = (${minor.arrowType})child.getType();
        ${lowerName}(child.getName()<#list minor.typeParams as typeParam>, arrowType.get${typeParam.name?cap_first}()</#list>);
        <#else>
        ${lowerName}(child.getName());
        </#if>
        break;
      }
</#list></#list>
        default:
          throw new UnsupportedOperationException("Unknown type: " + minorType);
      }
    }
  }

  protected String handleCase(final String input) {
    return input.toLowerCase();
  }

  protected NullableStructWriterFactory getNullableStructWriterFactory() {
    return NullableStructWriterFactory.getNullableStructWriterFactoryInstance();
  }

  @Override
  public int getValueCapacity() {
    return container.getValueCapacity();
  }

  public void setInitialCapacity(int initialCapacity) {
    this.initialCapacity = initialCapacity;
    container.setInitialCapacity(initialCapacity);
  }

  @Override
  public boolean isEmptyStruct() {
    return 0 == container.size();
  }

  @Override
  public Field getField() {
      return container.getField();
  }

  @Override
  public StructWriter struct(String name) {
    String finalName = handleCase(name);
    FieldWriter writer = fields.get(finalName);
    if(writer == null){
      int vectorCount=container.size();
      FieldType fieldType = new FieldType(addVectorAsNullable, MinorType.STRUCT.getType(), null, null);
      StructVector vector = container.addOrGet(name, fieldType, StructVector.class);
      writer = new PromotableWriter(vector, container, getNullableStructWriterFactory());
      if(vectorCount != container.size()) {
        writer.allocate();
      }
      writer.setPosition(idx());
      fields.put(finalName, writer);
    } else {
      if (writer instanceof PromotableWriter) {
        // ensure writers are initialized
        ((PromotableWriter)writer).getWriter(MinorType.STRUCT);
      }
    }
    return writer;
  }

  @Override
  public void close() throws Exception {
    clear();
    container.close();
  }

  @Override
  public void allocate() {
    container.allocateNew();
    for(final FieldWriter w : fields.values()) {
      w.allocate();
    }
  }

  @Override
  public void clear() {
    container.clear();
    for(final FieldWriter w : fields.values()) {
      w.clear();
    }
  }

  @Override
  public ListWriter list(String name) {
    String finalName = handleCase(name);
    FieldWriter writer = fields.get(finalName);
    int vectorCount = container.size();
    if(writer == null) {
      FieldType fieldType = new FieldType(addVectorAsNullable, MinorType.LIST.getType(), null, null);
      writer = new PromotableWriter(container.addOrGet(name, fieldType, ListVector.class), container, getNullableStructWriterFactory());
      if (container.size() > vectorCount) {
        writer.allocate();
      }
      writer.setPosition(idx());
      fields.put(finalName, writer);
    } else {
      if (writer instanceof PromotableWriter) {
        // ensure writers are initialized
        ((PromotableWriter)writer).getWriter(MinorType.LIST);
      }
    }
    return writer;
  }

  public void setValueCount(int count) {
    container.setValueCount(count);
  }

  @Override
  public void setPosition(int index) {
    super.setPosition(index);
    for(final FieldWriter w: fields.values()) {
      w.setPosition(index);
    }
  }

  <#if mode="Nullable">
  @Override
  public void writeNull() {
    container.setNull(idx());
    setValueCount(idx()+1);
    super.setPosition(idx()+1);
  }
  </#if>

  @Override
  public void start() {
    <#if mode == "Single">
    <#else>
    container.setIndexDefined(idx());
    </#if>
  }

  @Override
  public void end() {
    setPosition(idx()+1);
  }

  <#list vv.types as type><#list type.minor as minor>
  <#assign lowerName = minor.class?uncap_first />
  <#if lowerName == "int" ><#assign lowerName = "integer" /></#if>
  <#assign upperName = minor.class?upper_case />
  <#assign capName = minor.class?cap_first />
  <#assign vectName = capName />

  <#if minor.typeParams?? >
  @Override
  public ${minor.class}Writer ${lowerName}(String name) {
    // returns existing writer
    final FieldWriter writer = fields.get(handleCase(name));
    Preconditions.checkNotNull(writer);
    return writer;
  }

  @Override
  public ${minor.class}Writer ${lowerName}(String name<#list minor.typeParams as typeParam>, ${typeParam.type} ${typeParam.name}</#list>) {
  <#else>
  @Override
  public ${minor.class}Writer ${lowerName}(String name) {
  </#if>
    FieldWriter writer = fields.get(handleCase(name));
    if(writer == null) {
      ValueVector vector;
      ValueVector currentVector = container.getChild(name);
      ${vectName}Vector v = container.addOrGet(name,
          new FieldType(addVectorAsNullable,
          <#if minor.typeParams??>
            <#if minor.arrowTypeConstructorParams??>
              <#assign constructorParams = minor.arrowTypeConstructorParams />
            <#else>
              <#assign constructorParams = [] />
              <#list minor.typeParams?reverse as typeParam>
                <#assign constructorParams = constructorParams + [ typeParam.name ] />
              </#list>
            </#if>    
            new ${minor.arrowType}(${constructorParams?join(", ")})
          <#else>
            MinorType.${upperName}.getType()
          </#if>
          ,null, null),
          ${vectName}Vector.class);
      writer = new PromotableWriter(v, container, getNullableStructWriterFactory());
      vector = v;
      if (currentVector == null || currentVector != vector) {
        if(this.initialCapacity > 0) {
          vector.setInitialCapacity(this.initialCapacity);
        }
        vector.allocateNewSafe();
      } 
      writer.setPosition(idx());
      fields.put(handleCase(name), writer);
    } else {
      if (writer instanceof PromotableWriter) {
        // ensure writers are initialized
        ((PromotableWriter)writer).getWriter(MinorType.${upperName}<#if minor.class == "Decimal">, new ${minor.arrowType}(precision, scale)</#if>);
      }
    }
    return writer;
  }

  </#list></#list>

}
</#list>

```


#### Error stacktrace:

```
scala.meta.internal.mtags.JavaToplevelMtags.kwOrIdent$1(JavaToplevelMtags.scala:188)
	scala.meta.internal.mtags.JavaToplevelMtags.parseToken$1(JavaToplevelMtags.scala:247)
	scala.meta.internal.mtags.JavaToplevelMtags.fetchToken(JavaToplevelMtags.scala:253)
	scala.meta.internal.mtags.JavaToplevelMtags.loop(JavaToplevelMtags.scala:72)
	scala.meta.internal.mtags.JavaToplevelMtags.indexRoot(JavaToplevelMtags.scala:41)
	scala.meta.internal.mtags.MtagsIndexer.index(MtagsIndexer.scala:21)
	scala.meta.internal.mtags.MtagsIndexer.index$(MtagsIndexer.scala:20)
	scala.meta.internal.mtags.JavaToplevelMtags.index(JavaToplevelMtags.scala:15)
	scala.meta.internal.mtags.Mtags.indexWithOverrides(Mtags.scala:74)
	scala.meta.internal.mtags.SymbolIndexBucket.indexSource(SymbolIndexBucket.scala:130)
	scala.meta.internal.mtags.SymbolIndexBucket.addSourceFile(SymbolIndexBucket.scala:113)
	scala.meta.internal.mtags.SymbolIndexBucket.$anonfun$addSourceJar$2(SymbolIndexBucket.scala:80)
	scala.collection.immutable.List.flatMap(List.scala:294)
	scala.meta.internal.mtags.SymbolIndexBucket.$anonfun$addSourceJar$1(SymbolIndexBucket.scala:76)
	scala.meta.internal.io.PlatformFileIO$.withJarFileSystem(PlatformFileIO.scala:87)
	scala.meta.internal.io.FileIO$.withJarFileSystem(FileIO.scala:44)
	scala.meta.internal.mtags.SymbolIndexBucket.addSourceJar(SymbolIndexBucket.scala:74)
	scala.meta.internal.mtags.OnDemandSymbolIndex.$anonfun$addSourceJar$2(OnDemandSymbolIndex.scala:85)
	scala.meta.internal.mtags.OnDemandSymbolIndex.tryRun(OnDemandSymbolIndex.scala:148)
	scala.meta.internal.mtags.OnDemandSymbolIndex.addSourceJar(OnDemandSymbolIndex.scala:84)
	scala.meta.internal.metals.Indexer.indexJar$1(Indexer.scala:630)
	scala.meta.internal.metals.Indexer.addSourceJarSymbols(Indexer.scala:656)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$5(Indexer.scala:487)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.collection.IterableOps$WithFilter.foreach(Iterable.scala:905)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1(Indexer.scala:478)
	scala.meta.internal.metals.Indexer.$anonfun$indexDependencySources$1$adapted(Indexer.scala:477)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.meta.internal.metals.Indexer.indexDependencySources(Indexer.scala:477)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$24(Indexer.scala:396)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$23(Indexer.scala:389)
	scala.meta.internal.metals.Indexer.$anonfun$indexWorkspace$23$adapted(Indexer.scala:385)
	scala.collection.immutable.List.foreach(List.scala:334)
	scala.meta.internal.metals.Indexer.indexWorkspace(Indexer.scala:385)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$2(Indexer.scala:173)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.meta.internal.metals.TimerProvider.timedThunk(TimerProvider.scala:25)
	scala.meta.internal.metals.Indexer.$anonfun$profiledIndexWorkspace$1(Indexer.scala:173)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:687)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:467)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1583)
```
#### Short summary: 

Java indexer failed with and exception.