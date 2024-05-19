error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/ComplexReaders.java
jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/ComplexReaders.java
### java.lang.Exception: Unexpected symbol at word pos: '35'. Line: '<##list vv.types as type>'

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

import java.lang.Override;
import java.util.List;

import org.apache.arrow.record.TransferPair;
import org.apache.arrow.vector.complex.IndexHolder;
import org.apache.arrow.vector.complex.writer.IntervalWriter;
import org.apache.arrow.vector.complex.writer.BaseWriter.StructWriter;

<@pp.dropOutputFile />
<#list vv.types as type>
<#list type.minor as minor>
<#list [""] as mode>
<#assign lowerName = minor.class?uncap_first />
<#if lowerName == "int" ><#assign lowerName = "integer" /></#if>
<#assign name = minor.class?cap_first />
<#assign javaType = (minor.javaType!type.javaType) />
<#assign friendlyType = (minor.friendlyType!minor.boxedType!type.boxedType) />
<#assign safeType=friendlyType />
<#if safeType=="byte[]"><#assign safeType="ByteArray" /></#if>

<#assign hasFriendly = minor.friendlyType!"no" == "no" />

<#list ["Nullable"] as nullMode>
<#if mode == "" >
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/${name}ReaderImpl.java" />
<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />

/**
 * Source code generated using FreeMarker template ${.template_name}
 */
@SuppressWarnings("unused")
public class ${name}ReaderImpl extends AbstractFieldReader {
  
  private final ${name}Vector vector;
  
  public ${name}ReaderImpl(${name}Vector vector){
    super();
    this.vector = vector;
  }

  public MinorType getMinorType(){
    return vector.getMinorType();
  }

  public Field getField(){
    return vector.getField();
  }
  
  public boolean isSet(){
    return !vector.isNull(idx());
  }

  public void copyAsValue(${minor.class?cap_first}Writer writer){
    ${minor.class?cap_first}WriterImpl impl = (${minor.class?cap_first}WriterImpl) writer;
    impl.vector.copyFromSafe(idx(), impl.idx(), vector);
  }
  
  public void copyAsField(String name, StructWriter writer){
    ${minor.class?cap_first}WriterImpl impl = (${minor.class?cap_first}WriterImpl) writer.${lowerName}(name);
    impl.vector.copyFromSafe(idx(), impl.idx(), vector);
  }

  <#if nullMode != "Nullable">
  public void read(${minor.class?cap_first}Holder h){
    vector.get(idx(), h);
  }
  </#if>

  public void read(Nullable${minor.class?cap_first}Holder h){
    vector.get(idx(), h);
  }
  
  public ${friendlyType} read${safeType}(){
    return vector.getObject(idx());
  }

  <#if minor.class == "TimeStampSec" ||
       minor.class == "TimeStampMilli" ||
       minor.class == "TimeStampMicro" ||
       minor.class == "TimeStampNano">
  @Override
  public ${minor.boxedType} read${minor.boxedType}(){
    return vector.get(idx());
  }
  </#if>
  
  public void copyValue(FieldWriter w){
    
  }
  
  public Object readObject(){
    return (Object)vector.getObject(idx());
  }
}
</#if>
</#list>
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/reader/${name}Reader.java" />
<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.reader;

<#include "/@includes/vv_imports.ftl" />
/**
 * Source code generated using FreeMarker template ${.template_name}
 */
@SuppressWarnings("unused")
public interface ${name}Reader extends BaseReader{
  
  public void read(${minor.class?cap_first}Holder h);
  public void read(Nullable${minor.class?cap_first}Holder h);
  public Object readObject();
  // read friendly type
  public ${friendlyType} read${safeType}();
  public boolean isSet();
  public void copyAsValue(${minor.class}Writer writer);
  public void copyAsField(String name, ${minor.class}Writer writer);
  
}



</#list>
</#list>
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