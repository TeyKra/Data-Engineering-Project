file://<WORKSPACE>/build.sbt
### scala.reflect.internal.FatalError: 
  bad constant pool index: 0 at pos: 48445
     while compiling: <no file>
        during phase: globalPhase=<no phase>, enteringPhase=<some phase>
     library version: version 2.13.8
    compiler version: version 2.13.8
  reconstructed args: -classpath <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.8/scala-library-2.13.8.jar -Ymacro-expand:discard -Ycache-plugin-class-loader:last-modified -Ypresentation-any-thread

  last tree to typer: EmptyTree
       tree position: <unknown>
            tree tpe: <notype>
              symbol: null
           call site: <none> in <none>

== Source file context for tree position ==



occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 2.13.8
Classpath:
<HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.8/scala-library-2.13.8.jar [exists ]
Options:



action parameters:
uri: file://<WORKSPACE>/build.sbt
text:
```scala
val scalaVersionNumber = "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "projet_data-engineer",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scalaVersionNumber,
    resolvers += "Maven Central" at "https://repo1.maven.org/maven2/",
    resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0",
      "org.apache.kafka" % "kafka-streams" % "2.8.0",
      "org.apache.kafka" % "kafka-clients" % "2.8.0",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-api" % "2.14.1",
      "org.apache.logging.log4j" % "log4j-core" % "2.14.1",
      "javax.activation" % "activation" % "1.1.1",
      "com.twilio.sdk" % "twilio" % "8.25.0",
      "software.amazon.awssdk" % "s3" % "2.17.182",
      "org.apache.spark" %% "spark-core" % "3.2.0",
      "org.apache.spark" %% "spark-sql" % "3.2.0",
      "com.itextpdf" % "itext7-core" % "7.1.15",
      "org.apache.pdfbox" % "pdfbox" % "2.0.24",
      "org.apache.pdfbox" % "fontbox" % "2.0.24"
    )
  )
```



#### Error stacktrace:

```
scala.reflect.internal.Reporting.abort(Reporting.scala:69)
	scala.reflect.internal.Reporting.abort$(Reporting.scala:65)
	scala.reflect.internal.SymbolTable.abort(SymbolTable.scala:28)
	scala.tools.nsc.symtab.classfile.ClassfileParser$ConstantPool.errorBadIndex(ClassfileParser.scala:407)
	scala.tools.nsc.symtab.classfile.ClassfileParser$ConstantPool.getExternalName(ClassfileParser.scala:262)
	scala.tools.nsc.symtab.classfile.ClassfileParser.readParamNames$1(ClassfileParser.scala:853)
	scala.tools.nsc.symtab.classfile.ClassfileParser.parseAttribute$1(ClassfileParser.scala:859)
	scala.tools.nsc.symtab.classfile.ClassfileParser.$anonfun$parseAttributes$6(ClassfileParser.scala:936)
	scala.tools.nsc.symtab.classfile.ClassfileParser.parseAttributes(ClassfileParser.scala:936)
	scala.tools.nsc.symtab.classfile.ClassfileParser.parseMethod(ClassfileParser.scala:635)
	scala.tools.nsc.symtab.classfile.ClassfileParser.parseClass(ClassfileParser.scala:548)
	scala.tools.nsc.symtab.classfile.ClassfileParser.$anonfun$parse$2(ClassfileParser.scala:174)
	scala.tools.nsc.symtab.classfile.ClassfileParser.$anonfun$parse$1(ClassfileParser.scala:159)
	scala.tools.nsc.symtab.classfile.ClassfileParser.parse(ClassfileParser.scala:142)
	scala.tools.nsc.symtab.SymbolLoaders$ClassfileLoader.doComplete(SymbolLoaders.scala:342)
	scala.tools.nsc.symtab.SymbolLoaders$SymbolLoader.$anonfun$complete$2(SymbolLoaders.scala:249)
	scala.tools.nsc.symtab.SymbolLoaders$SymbolLoader.complete(SymbolLoaders.scala:247)
	scala.reflect.internal.Symbols$Symbol.completeInfo(Symbols.scala:1561)
	scala.reflect.internal.Symbols$Symbol.info(Symbols.scala:1533)
	scala.reflect.internal.Definitions.scala$reflect$internal$Definitions$$enterNewMethod(Definitions.scala:47)
	scala.reflect.internal.Definitions$DefinitionsClass.String_$plus$lzycompute(Definitions.scala:1256)
	scala.reflect.internal.Definitions$DefinitionsClass.String_$plus(Definitions.scala:1256)
	scala.reflect.internal.Definitions$DefinitionsClass.syntheticCoreMethods$lzycompute(Definitions.scala:1577)
	scala.reflect.internal.Definitions$DefinitionsClass.syntheticCoreMethods(Definitions.scala:1559)
	scala.reflect.internal.Definitions$DefinitionsClass.symbolsNotPresentInBytecode$lzycompute(Definitions.scala:1590)
	scala.reflect.internal.Definitions$DefinitionsClass.symbolsNotPresentInBytecode(Definitions.scala:1590)
	scala.reflect.internal.Definitions$DefinitionsClass.init(Definitions.scala:1646)
	scala.tools.nsc.Global$Run.<init>(Global.scala:1226)
	scala.tools.nsc.interactive.Global$TyperRun.<init>(Global.scala:1331)
	scala.tools.nsc.interactive.Global.newTyperRun(Global.scala:1354)
	scala.tools.nsc.interactive.Global.<init>(Global.scala:294)
	scala.meta.internal.pc.MetalsGlobal.<init>(MetalsGlobal.scala:40)
	scala.meta.internal.pc.ScalaPresentationCompiler.newCompiler(ScalaPresentationCompiler.scala:453)
```
#### Short summary: 

scala.reflect.internal.FatalError: 
  bad constant pool index: 0 at pos: 48445
     while compiling: <no file>
        during phase: globalPhase=<no phase>, enteringPhase=<some phase>
     library version: version 2.13.8
    compiler version: version 2.13.8
  reconstructed args: -classpath <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.8/scala-library-2.13.8.jar -Ymacro-expand:discard -Ycache-plugin-class-loader:last-modified -Ypresentation-any-thread

  last tree to typer: EmptyTree
       tree position: <unknown>
            tree tpe: <notype>
              symbol: null
           call site: <none> in <none>

== Source file context for tree position ==

