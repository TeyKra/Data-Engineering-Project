error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/NullReader.java
jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/NullReader.java
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

import org.apache.arrow.vector.types.pojo.ArrowType.Null;
import org.apache.arrow.vector.types.pojo.Field;

<@pp.dropOutputFile />
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/NullReader.java" />


<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />

/**
 * Source code generated using FreeMarker template ${.template_name}
 */
@SuppressWarnings("unused")
public class NullReader extends AbstractBaseReader implements FieldReader{
  
  public static final NullReader INSTANCE = new NullReader();
  public static final NullReader EMPTY_LIST_INSTANCE = new NullReader(MinorType.NULL);
  public static final NullReader EMPTY_STRUCT_INSTANCE = new NullReader(MinorType.STRUCT);
  private MinorType type;
  
  private NullReader(){
    super();
    type = MinorType.NULL;
  }

  private NullReader(MinorType type){
    super();
    this.type = type;
  }

  @Override
  public MinorType getMinorType() {
    return type;
  }

  @Override
  public Field getField() {
    return new Field("", FieldType.nullable(new Null()), null);
  }

  public void copyAsValue(StructWriter writer) {}

  public void copyAsValue(ListWriter writer) {}

  public void copyAsValue(UnionWriter writer) {}

  <#list vv.types as type><#list type.minor as minor><#assign name = minor.class?cap_first />
  public void read(${name}Holder holder){
    throw new UnsupportedOperationException("NullReader cannot write into non-nullable holder");
  }

  public void read(Nullable${name}Holder holder){
    holder.isSet = 0;
  }

  public void read(int arrayIndex, ${name}Holder holder){
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public void copyAsValue(${minor.class}Writer writer){}
  public void copyAsField(String name, ${minor.class}Writer writer){}

  public void read(int arrayIndex, Nullable${name}Holder holder){
    throw new ArrayIndexOutOfBoundsException();
  }
  </#list></#list>
  
  public int size(){
    return 0;
  }
  
  public boolean isSet(){
    return false;
  }
  
  public boolean next(){
    return false;
  }
  
  public RepeatedStructReader struct(){
    return this;
  }
  
  public RepeatedListReader list(){
    return this;
  }
  
  public StructReader struct(String name){
    return this;
  }
  
  public ListReader list(String name){
    return this;
  }
  
  public FieldReader reader(String name){
    return this;
  }
  
  public FieldReader reader(){
    return this;
  }
  
  private void fail(String name){
    throw new IllegalArgumentException(String.format("You tried to read a %s type when you are using a ValueReader of type %s.", name, this.getClass().getSimpleName()));
  }
  
  <#list ["Object", "BigDecimal", "Short", "Integer", "Long", "Boolean",
          "LocalDateTime", "Duration", "Period", "Double", "Float",
          "Character", "Text", "String", "Byte", "byte[]"] as friendlyType>
  <#assign safeType=friendlyType />
  <#if safeType=="byte[]"><#assign safeType="ByteArray" /></#if>
  
  public ${friendlyType} read${safeType}(int arrayIndex){
    return null;
  }
  
  public ${friendlyType} read${safeType}(){
    return null;
  }
  </#list>
  
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