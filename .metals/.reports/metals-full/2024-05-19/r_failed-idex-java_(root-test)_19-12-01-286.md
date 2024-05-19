error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/ComplexWriters.java
jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/ComplexWriters.java
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

<@pp.dropOutputFile />
<#list vv.types as type>
<#list type.minor as minor>
<#list ["Nullable"] as mode>
<#assign name = minor.class?cap_first />
<#assign eName = name />
<#assign javaType = (minor.javaType!type.javaType) />
<#assign fields = minor.fields!type.fields />
<#assign friendlyType = (minor.friendlyType!minor.boxedType!type.boxedType) />

<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/${eName}WriterImpl.java" />
<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />

/*
 * This class is generated using FreeMarker on the ${.template_name} template.
 */
@SuppressWarnings("unused")
public class ${eName}WriterImpl extends AbstractFieldWriter {

  final ${name}Vector vector;

  public ${eName}WriterImpl(${name}Vector vector) {
    this.vector = vector;
  }

  @Override
  public Field getField() {
    return vector.getField();
  }

  @Override
  public int getValueCapacity() {
    return vector.getValueCapacity();
  }

  @Override
  public void allocate() {
    vector.allocateNew();
  }

  @Override
  public void close() {
    vector.close();
  }

  @Override
  public void clear() {
    vector.clear();
  }

  @Override
  protected int idx() {
    return super.idx();
  }

  <#if mode == "Repeated">

  public void write(${minor.class?cap_first}Holder h) {
    mutator.addSafe(idx(), h);
    vector.setValueCount(idx()+1);
  }

  public void write(${minor.class?cap_first}Holder h) {
    mutator.addSafe(idx(), h);
    vector.setValueCount(idx()+1);
  }

  public void write${minor.class}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
    mutator.addSafe(idx(), <#list fields as field>${field.name}<#if field_has_next>, </#if></#list>);
    vector.setValueCount(idx()+1);
  }

  public void setPosition(int idx) {
    super.setPosition(idx);
    mutator.startNewValue(idx);
  }


  <#else>

  <#if minor.class != "Decimal">
  public void write(${minor.class}Holder h) {
    vector.setSafe(idx(), h);
    vector.setValueCount(idx()+1);
  }

  public void write(Nullable${minor.class}Holder h) {
    vector.setSafe(idx(), h);
    vector.setValueCount(idx()+1);
  }

  public void write${minor.class}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
    vector.setSafe(idx(), 1<#list fields as field><#if field.include!true >, ${field.name}</#if></#list>);
    vector.setValueCount(idx()+1);
  }
  </#if>

  <#if minor.class == "VarChar">
  public void write${minor.class}(${friendlyType} value) {
    vector.setSafe(idx(), value);
    vector.setValueCount(idx()+1);
  }
  </#if>

  <#if minor.class == "Decimal">

  public void write(DecimalHolder h){
    DecimalUtility.checkPrecisionAndScale(h.precision, h.scale, vector.getPrecision(), vector.getScale());
    vector.setSafe(idx(), h);
    vector.setValueCount(idx() + 1);
  }

  public void write(NullableDecimalHolder h){
    if (h.isSet == 1) {
      DecimalUtility.checkPrecisionAndScale(h.precision, h.scale, vector.getPrecision(), vector.getScale());
    }
    vector.setSafe(idx(), h);
    vector.setValueCount(idx() + 1);
  }

  public void writeDecimal(int start, ArrowBuf buffer){
    vector.setSafe(idx(), 1, start, buffer);
    vector.setValueCount(idx() + 1);
  }

  public void writeDecimal(int start, ArrowBuf buffer, ArrowType arrowType){
    DecimalUtility.checkPrecisionAndScale(((ArrowType.Decimal) arrowType).getPrecision(),
      ((ArrowType.Decimal) arrowType).getScale(), vector.getPrecision(), vector.getScale());
    vector.setSafe(idx(), 1, start, buffer);
    vector.setValueCount(idx() + 1);
  }

  public void writeDecimal(BigDecimal value){
    // vector.setSafe already does precision and scale checking
    vector.setSafe(idx(), value);
    vector.setValueCount(idx() + 1);
  }

  public void writeBigEndianBytesToDecimal(byte[] value, ArrowType arrowType){
    DecimalUtility.checkPrecisionAndScale(((ArrowType.Decimal) arrowType).getPrecision(),
        ((ArrowType.Decimal) arrowType).getScale(), vector.getPrecision(), vector.getScale());
    vector.setBigEndianSafe(idx(), value);
    vector.setValueCount(idx() + 1);
  }

  public void writeBigEndianBytesToDecimal(byte[] value){
    vector.setBigEndianSafe(idx(), value);
    vector.setValueCount(idx() + 1);
  }
  </#if>

  public void writeNull() {
    vector.setNull(idx());
    vector.setValueCount(idx()+1);
  }
  </#if>
}

<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/writer/${eName}Writer.java" />
<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.writer;

<#include "/@includes/vv_imports.ftl" />
/*
 * This class is generated using FreeMarker on the ${.template_name} template.
 */
@SuppressWarnings("unused")
public interface ${eName}Writer extends BaseWriter {
  public void write(${minor.class}Holder h);

  <#if minor.class == "Decimal">@Deprecated</#if>
  public void write${minor.class}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>);
<#if minor.class == "Decimal">

  public void write${minor.class}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>, ArrowType arrowType);

  public void write${minor.class}(${friendlyType} value);

  public void writeBigEndianBytesToDecimal(byte[] value, ArrowType arrowType);

  @Deprecated
  public void writeBigEndianBytesToDecimal(byte[] value);
</#if>
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