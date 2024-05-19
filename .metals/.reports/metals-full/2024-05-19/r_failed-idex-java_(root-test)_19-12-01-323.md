error id: jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/ComplexCopier.java
jar:file://<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/apache/arrow/arrow-vector/2.0.0/arrow-vector-2.0.0-sources.jar!/codegen/templates/ComplexCopier.java
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

import org.apache.arrow.vector.complex.MapVector;
import org.apache.arrow.vector.complex.impl.UnionMapReader;
import org.apache.arrow.vector.complex.impl.UnionMapWriter;
import org.apache.arrow.vector.complex.reader.FieldReader;
import org.apache.arrow.vector.complex.writer.FieldWriter;
import org.apache.arrow.vector.types.Types;

<@pp.dropOutputFile />
<@pp.changeOutputFile name="/org/apache/arrow/vector/complex/impl/ComplexCopier.java" />


<#include "/@includes/license.ftl" />

package org.apache.arrow.vector.complex.impl;

<#include "/@includes/vv_imports.ftl" />

/*
 * This class is generated using freemarker and the ${.template_name} template.
 */
@SuppressWarnings("unused")
public class ComplexCopier {

  /**
   * Do a deep copy of the value in input into output
   * @param input field to read from
   * @param output field to write to
   */
  public static void copy(FieldReader input, FieldWriter output) {
    writeValue(input, output);
  }

  private static void writeValue(FieldReader reader, FieldWriter writer) {
    final MinorType mt = reader.getMinorType();

      switch (mt) {

      case LIST:
      case LARGELIST:
      case FIXED_SIZE_LIST:
        if (reader.isSet()) {
          writer.startList();
          while (reader.next()) {
            FieldReader childReader = reader.reader();
            FieldWriter childWriter = getListWriterForReader(childReader, writer);
            if (childReader.isSet()) {
              writeValue(childReader, childWriter);
            } else {
              childWriter.writeNull();
            }
          }
          writer.endList();
        } else {
          writer.writeNull();
        }
        break;
      case MAP:
        if (reader.isSet()) {
          UnionMapWriter mapWriter = (UnionMapWriter) writer;
          UnionMapReader mapReader = (UnionMapReader) reader;

          mapWriter.startMap();
          while (mapReader.next()) {
            FieldReader structReader = reader.reader();
            UnionMapWriter structWriter = (UnionMapWriter) writer.struct();
            if (structReader.isSet()) {
              mapWriter.startEntry();
              writeValue(mapReader.key(), getStructWriterForReader(mapReader.key(), structWriter.key(), MapVector.KEY_NAME));
              writeValue(mapReader.value(), getStructWriterForReader(mapReader.value(), structWriter.value(), MapVector.VALUE_NAME));
              mapWriter.endEntry();
            } else {
              structWriter.writeNull();
            }
          }
          mapWriter.endMap();
        } else {
          writer.writeNull();
        }
        break;
      case STRUCT:
        if (reader.isSet()) {
          writer.start();
          for(String name : reader){
            FieldReader childReader = reader.reader(name);
            if (childReader.getMinorType() != Types.MinorType.NULL) {
              FieldWriter childWriter = getStructWriterForReader(childReader, writer, name);
              if (childReader.isSet()) {
                writeValue(childReader, childWriter);
              } else {
                childWriter.writeNull();
              }
            }
          }
          writer.end();
        } else {
          writer.writeNull();
        }
        break;
  <#list vv.types as type><#list type.minor as minor><#assign name = minor.class?cap_first />
  <#assign fields = minor.fields!type.fields />
  <#assign uncappedName = name?uncap_first/>

  <#if !minor.typeParams?? || minor.class?starts_with("Decimal") >

      case ${name?upper_case}:
        if (reader.isSet()) {
          Nullable${name}Holder ${uncappedName}Holder = new Nullable${name}Holder();
          reader.read(${uncappedName}Holder);
          if (${uncappedName}Holder.isSet == 1) {
            writer.write${name}(<#list fields as field>${uncappedName}Holder.${field.name}<#if field_has_next>, </#if></#list><#if minor.class == "Decimal">, new ArrowType.Decimal(decimalHolder.precision, decimalHolder.scale)</#if>);
          }
        } else {
          writer.writeNull();
        }
        break;

  </#if>
  </#list></#list>
      }
 }

  private static FieldWriter getStructWriterForReader(FieldReader reader, StructWriter writer, String name) {
    switch (reader.getMinorType()) {
    <#list vv.types as type><#list type.minor as minor><#assign name = minor.class?cap_first />
    <#assign fields = minor.fields!type.fields />
    <#assign uncappedName = name?uncap_first/>
    <#if !minor.typeParams??>
    case ${name?upper_case}:
      return (FieldWriter) writer.<#if name == "Int">integer<#else>${uncappedName}</#if>(name);
    </#if>
    <#if minor.class == "Decimal">
    case ${name?upper_case}:
      if (reader.getField().getType() instanceof ArrowType.Decimal) {
        ArrowType.Decimal type = (ArrowType.Decimal) reader.getField().getType();
        return (FieldWriter) writer.${uncappedName}(name, type.getScale(), type.getPrecision());
      } else {
        return (FieldWriter) writer.${uncappedName}(name);
      }
    </#if>
    </#list></#list>
    case STRUCT:
      return (FieldWriter) writer.struct(name);
    case FIXED_SIZE_LIST:
    case LIST:
    case MAP:
      return (FieldWriter) writer.list(name);
    default:
      throw new UnsupportedOperationException(reader.getMinorType().toString());
    }
  }

  private static FieldWriter getListWriterForReader(FieldReader reader, ListWriter writer) {
    switch (reader.getMinorType()) {
    <#list vv.types as type><#list type.minor as minor><#assign name = minor.class?cap_first />
    <#assign fields = minor.fields!type.fields />
    <#assign uncappedName = name?uncap_first/>
    <#if !minor.typeParams?? || minor.class?starts_with("Decimal") >
    case ${name?upper_case}:
    return (FieldWriter) writer.<#if name == "Int">integer<#else>${uncappedName}</#if>();
    </#if>
    </#list></#list>
    case STRUCT:
      return (FieldWriter) writer.struct();
    case FIXED_SIZE_LIST:
    case LIST:
    case MAP:
    case NULL:
      return (FieldWriter) writer.list();
    default:
      throw new UnsupportedOperationException(reader.getMinorType().toString());
    }
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