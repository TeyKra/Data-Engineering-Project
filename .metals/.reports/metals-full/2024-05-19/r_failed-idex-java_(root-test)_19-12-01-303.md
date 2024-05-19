error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/DenseUnionWriter.java
jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/DenseUnionWriter.java
### java.lang.Exception: Unexpected symbol at word pos: '35'. Line: '<##include "/@includes/license.ftl" />'

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

import org.apache.arrow.vector.complex.StructVector;
import org.apache.arrow.vector.complex.impl.NullableStructWriterFactory;
import org.apache.arrow.vector.types.Types;

<@pp.dropOutputFile />
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/DenseUnionWriter.java" />


<#include "/@includes/license.ftl" />

        package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />
        import org.apache.arrow.vector.complex.writer.BaseWriter;
        import org.apache.arrow.vector.types.Types.MinorType;

/*
 * This class is generated using freemarker and the ${.template_name} template.
 */
@SuppressWarnings("unused")
public class DenseUnionWriter extends AbstractFieldWriter implements FieldWriter {

  DenseUnionVector data;

  private BaseWriter[] writers = new BaseWriter[Byte.MAX_VALUE + 1];
  private final NullableStructWriterFactory nullableStructWriterFactory;

  public DenseUnionWriter(DenseUnionVector vector) {
    this(vector, NullableStructWriterFactory.getNullableStructWriterFactoryInstance());
  }

  public DenseUnionWriter(DenseUnionVector vector, NullableStructWriterFactory nullableStructWriterFactory) {
    data = vector;
    this.nullableStructWriterFactory = nullableStructWriterFactory;
  }

  @Override
  public void setPosition(int index) {
    super.setPosition(index);
    for (BaseWriter writer : writers) {
      writer.setPosition(index);
    }
  }

  @Override
  public void start() {
    byte typeId = data.getTypeId(idx());
    getStructWriter((byte) idx()).start();
  }

  @Override
  public void end() {
    byte typeId = data.getTypeId(idx());
    getStructWriter(typeId).end();
  }

  @Override
  public void startList() {
    byte typeId = data.getTypeId(idx());
    getListWriter(typeId).startList();
  }

  @Override
  public void endList() {
    byte typeId = data.getTypeId(idx());
    getListWriter(typeId).endList();
  }

  private StructWriter getStructWriter(byte typeId) {
    StructWriter structWriter = (StructWriter) writers[typeId];
    if (structWriter == null) {
      structWriter = nullableStructWriterFactory.build((StructVector) data.getVectorByType(typeId));
      writers[typeId] = structWriter;
    }
    return structWriter;
  }

  public StructWriter asStruct(byte typeId) {
    data.setTypeId(idx(), typeId);
    return getStructWriter(typeId);
  }

  private ListWriter getListWriter(byte typeId) {
    ListWriter listWriter = (ListWriter) writers[typeId];
    if (listWriter == null) {
      listWriter = new UnionListWriter((ListVector) data.getVectorByType(typeId), nullableStructWriterFactory);
      writers[typeId] = listWriter;
    }
    return listWriter;
  }

  public ListWriter asList(byte typeId) {
    data.setTypeId(idx(), typeId);
    return getListWriter(typeId);
  }

  BaseWriter getWriter(byte typeId) {
    MinorType minorType = data.getVectorByType(typeId).getMinorType();
    switch (minorType) {
      case STRUCT:
        return getStructWriter(typeId);
      case LIST:
        return getListWriter(typeId);
    <#list vv.types as type>
      <#list type.minor as minor>
        <#assign name = minor.class?cap_first />
        <#assign fields = minor.fields!type.fields />
        <#assign uncappedName = name?uncap_first/>
        <#if !minor.typeParams?? || minor.class == "Decimal">
      case ${name?upper_case}:
      return get${name}Writer(typeId);
        </#if>
      </#list>
    </#list>
      default:
        throw new UnsupportedOperationException("Unknown type: " + minorType);
    }
  }
  <#list vv.types as type>
    <#list type.minor as minor>
      <#assign name = minor.class?cap_first />
      <#assign fields = minor.fields!type.fields />
      <#assign uncappedName = name?uncap_first/>
      <#if !minor.typeParams?? || minor.class == "Decimal">

  private ${name}Writer get${name}Writer(byte typeId) {
    ${name}Writer writer = (${name}Writer) writers[typeId];
    if (writer == null) {
      writer = new ${name}WriterImpl((${name}Vector) data.getVectorByType(typeId));
      writers[typeId] = writer;
    }
    return writer;
  }

  public ${name}Writer as${name}(byte typeId) {
    data.setTypeId(idx(), typeId);
    return get${name}Writer(typeId);
  }

  @Override
  public void write(${name}Holder holder) {
    throw new UnsupportedOperationException();
  }

  public void write${minor.class}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>, byte typeId<#if minor.class == "Decimal">, ArrowType arrowType</#if>) {
    data.setTypeId(idx(), typeId);
    get${name}Writer(typeId).setPosition(data.getOffset(idx()));
    get${name}Writer(typeId).write${name}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list><#if minor.class == "Decimal">, arrowType</#if>);
  }
      </#if>
    </#list>
  </#list>

  public void writeNull() {
  }

  @Override
  public StructWriter struct() {
    byte typeId = data.getTypeId(idx());
    data.setTypeId(idx(), typeId);
    getListWriter(typeId).setPosition(data.getOffset(idx()));
    return getListWriter(typeId).struct();
  }

  @Override
  public ListWriter list() {
    byte typeId = data.getTypeId(idx());
    data.setTypeId(idx(), typeId);
    getListWriter(typeId).setPosition(data.getOffset(idx()));
    return getListWriter(typeId).list();
  }

  @Override
  public ListWriter list(String name) {
    byte typeId = data.getTypeId(idx());
    data.setTypeId(idx(), typeId);
    getStructWriter(typeId).setPosition(data.getOffset(idx()));
    return getStructWriter(typeId).list(name);
  }

  @Override
  public StructWriter struct(String name) {
    byte typeId = data.getTypeId(idx());
    data.setTypeId(idx(), typeId);
    getStructWriter(typeId).setPosition(data.getOffset(idx()));
    return getStructWriter(typeId).struct(name);
  }

  <#list vv.types as type><#list type.minor as minor>
  <#assign lowerName = minor.class?uncap_first />
  <#if lowerName == "int" ><#assign lowerName = "integer" /></#if>
  <#assign upperName = minor.class?upper_case />
  <#assign capName = minor.class?cap_first />
  <#if !minor.typeParams?? || minor.class == "Decimal" >
  @Override
  public ${capName}Writer ${lowerName}(String name) {
    byte typeId = data.getTypeId(idx());
    data.setTypeId(idx(), typeId);
    getStructWriter(typeId).setPosition(data.getOffset(idx()));
    return getStructWriter(typeId).${lowerName}(name);
  }

  @Override
  public ${capName}Writer ${lowerName}() {
    byte typeId = data.getTypeId(idx());
    data.setTypeId(idx(), typeId);
    getListWriter(typeId).setPosition(data.getOffset(idx()));
    return getListWriter(typeId).${lowerName}();
  }
  </#if>
  <#if minor.class == "Decimal">
  public ${capName}Writer ${lowerName}(String name<#list minor.typeParams as typeParam>, ${typeParam.type} ${typeParam.name}</#list>) {
    byte typeId = data.getTypeId(idx());
    data.setTypeId(idx(), typeId);
    getStructWriter(typeId).setPosition(data.getOffset(idx()));
    return getStructWriter(typeId).${lowerName}(name<#list minor.typeParams as typeParam>, ${typeParam.name}</#list>);
  }
  </#if>
  </#list></#list>

  @Override
  public void allocate() {
    data.allocateNew();
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public void close() throws Exception {
    data.close();
  }

  @Override
  public Field getField() {
    return data.getField();
  }

  @Override
  public int getValueCapacity() {
    return data.getValueCapacity();
  }
}

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