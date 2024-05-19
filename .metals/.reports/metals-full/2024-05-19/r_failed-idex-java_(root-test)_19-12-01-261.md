error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/UnionReader.java
jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/UnionReader.java
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


import org.apache.arrow.vector.types.Types.MinorType;
import org.apache.arrow.vector.types.pojo.Field;

<@pp.dropOutputFile />
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/UnionReader.java" />


<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />
/**
 * Source code generated using FreeMarker template ${.template_name}
 */
@SuppressWarnings("unused")
public class UnionReader extends AbstractFieldReader {

  private BaseReader[] readers = new BaseReader[43];
  public UnionVector data;
  
  public UnionReader(UnionVector data) {
    this.data = data;
  }

  public MinorType getMinorType() {
    return TYPES[data.getTypeValue(idx())];
  }

  private static MinorType[] TYPES = new MinorType[43];

  static {
    for (MinorType minorType : MinorType.values()) {
      TYPES[minorType.ordinal()] = minorType;
    }
  }

  @Override
  public Field getField() {
    return data.getField();
  }

  public boolean isSet(){
    return !data.isNull(idx());
  }

  public void read(UnionHolder holder) {
    holder.reader = this;
    holder.isSet = this.isSet() ? 1 : 0;
  }

  public void read(int index, UnionHolder holder) {
    getList().read(index, holder);
  }

  private FieldReader getReaderForIndex(int index) {
    int typeValue = data.getTypeValue(index);
    FieldReader reader = (FieldReader) readers[typeValue];
    if (reader != null) {
      return reader;
    }
    switch (MinorType.values()[typeValue]) {
    case NULL:
      return NullReader.INSTANCE;
    case STRUCT:
      return (FieldReader) getStruct();
    case LIST:
      return (FieldReader) getList();
    <#list vv.types as type>
      <#list type.minor as minor>
        <#assign name = minor.class?cap_first />
        <#assign uncappedName = name?uncap_first/>
        <#if !minor.typeParams?? || minor.class == "Decimal">
    case ${name?upper_case}:
      return (FieldReader) get${name}();
        </#if>
      </#list>
    </#list>
    default:
      throw new UnsupportedOperationException("Unsupported type: " + MinorType.values()[typeValue]);
    }
  }

  private SingleStructReaderImpl structReader;

  private StructReader getStruct() {
    if (structReader == null) {
      structReader = (SingleStructReaderImpl) data.getStruct().getReader();
      structReader.setPosition(idx());
      readers[MinorType.STRUCT.ordinal()] = structReader;
    }
    return structReader;
  }

  private UnionListReader listReader;

  private FieldReader getList() {
    if (listReader == null) {
      listReader = new UnionListReader(data.getList());
      listReader.setPosition(idx());
      readers[MinorType.LIST.ordinal()] = listReader;
    }
    return listReader;
  }

  @Override
  public java.util.Iterator<String> iterator() {
    return getStruct().iterator();
  }

  @Override
  public void copyAsValue(UnionWriter writer) {
    writer.data.copyFrom(idx(), writer.idx(), data);
  }

  <#list ["Object", "BigDecimal", "Short", "Integer", "Long", "Boolean",
          "LocalDateTime", "Duration", "Period", "Double", "Float",
          "Character", "Text", "Byte", "byte[]"] as friendlyType>
  <#assign safeType=friendlyType />
  <#if safeType=="byte[]"><#assign safeType="ByteArray" /></#if>

  @Override
  public ${friendlyType} read${safeType}() {
    return getReaderForIndex(idx()).read${safeType}();
  }

  </#list>

  public int size() {
    return getReaderForIndex(idx()).size();
  }

  <#list vv.types as type>
    <#list type.minor as minor>
      <#assign name = minor.class?cap_first />
      <#assign uncappedName = name?uncap_first/>
      <#assign boxedType = (minor.boxedType!type.boxedType) />
      <#assign javaType = (minor.javaType!type.javaType) />
      <#assign friendlyType = (minor.friendlyType!minor.boxedType!type.boxedType) />
      <#assign safeType=friendlyType />
      <#if safeType=="byte[]"><#assign safeType="ByteArray" /></#if>
      <#if !minor.typeParams?? || minor.class == "Decimal" >

  private ${name}ReaderImpl ${uncappedName}Reader;

  private ${name}ReaderImpl get${name}() {
    if (${uncappedName}Reader == null) {
      ${uncappedName}Reader = new ${name}ReaderImpl(data.get${name}Vector());
      ${uncappedName}Reader.setPosition(idx());
      readers[MinorType.${name?upper_case}.ordinal()] = ${uncappedName}Reader;
    }
    return ${uncappedName}Reader;
  }

  public void read(Nullable${name}Holder holder){
    getReaderForIndex(idx()).read(holder);
  }

  public void copyAsValue(${name}Writer writer){
    getReaderForIndex(idx()).copyAsValue(writer);
  }
      </#if>
    </#list>
  </#list>

  @Override
  public void copyAsValue(ListWriter writer) {
    ComplexCopier.copy(this, (FieldWriter) writer);
  }

  @Override
  public void setPosition(int index) {
    super.setPosition(index);
    for (BaseReader reader : readers) {
      if (reader != null) {
        reader.setPosition(index);
      }
    }
  }

  public FieldReader reader(String name){
    return getStruct().reader(name);
  }

  public FieldReader reader() {
    return getList().reader();
  }

  public boolean next() {
    return getReaderForIndex(idx()).next();
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