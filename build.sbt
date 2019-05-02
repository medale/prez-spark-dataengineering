import Dependencies._

name := "spark-dataengineering"

//http://docs.oracle.com/javase/8/docs/technotes/tools/windows/javac.html
//-Xlint enable all recommended warnings
ThisBuild / javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint", "-encoding", "UTF-8")
//https://github.com/ThoughtWorksInc/sbt-best-practice/blob/master/scalac-options/src/main/scala/com/thoughtworks/sbtBestPractice/scalacOptions/ScalacWarnings.scala
//scalac -help
//-unchecked Enable detailed unchecked (erasure) warnings
//-deprecation Emit warning and location for usages of deprecated APIs.
//-feature Emit warning and location for usages of features that should be imported explicitly. (e.g. postfix)
ThisBuild / scalacOptions := Seq("-target:jvm-1.8", "-unchecked", "-deprecation", "-feature")

//the following three settings according to https://github.com/holdenk/spark-testing-base
ThisBuild / IntegrationTest / fork := true
ThisBuild / IntegrationTest / parallelExecution := false
//for forked JVMs
ThisBuild / IntegrationTest / javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

ThisBuild / initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8") {
    sys.error("Java 8 is required for this project.")
  }
}

// match Apache Spark Scala (see Spark pom.xml)
ThisBuild / scalaVersion := "2.11.12"

//for scaladoc to link to external libraries (see https://www.scala-sbt.org/1.x/docs/Howto-Scaladoc.html)
ThisBuild / autoAPIMappings := true

ThisBuild / resolvers += Resolver.mavenLocal

//https://stackoverflow.com/questions/18838944/how-to-add-provided-dependencies-back-to-run-test-tasks-classpath
ThisBuild / run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run)).evaluated
ThisBuild / runMain in Compile := Defaults.runMainTask(fullClasspath in Compile, runner in(Compile, run)).evaluated

//run sbt-assembly via: sbt assembly to build fat jar
lazy val assemblyPluginSettings = Seq(
  assemblyJarName in assembly := s"${baseDirectory.value.name}-${version.value}-fat.jar",
  //exclude scala from generated fat jars
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  //put templatized application.conf (see editsource) in root of fat jar
  assembledMappings in assembly += {
    sbtassembly.MappingSet(None, Vector(
      (baseDirectory.value / "target" / "application.conf") -> "application.conf"
    ))
  },
  //http://queirozf.com/entries/creating-scala-fat-jars-for-spark-on-sbt-with-sbt-assembly-plugin
  assemblyMergeStrategy in assembly := {
    case PathList("javax", "ws", _@_*) => MergeStrategy.discard
    case PathList("javax", "servlet", _@_*) => MergeStrategy.discard
    case PathList(html@_*) if html.last endsWith ".html" => MergeStrategy.discard
    case PathList(xs@_*) if ((xs.head == "META-INF") && (xs.last.endsWith(".SF"))) => MergeStrategy.discard
    case PathList(xs@_*) if ((xs.head == "META-INF") && (xs.last.endsWith(".DSA"))) => MergeStrategy.discard
    case PathList(xs@_*) if ((xs.head == "META-INF") && (xs.last.endsWith(".RSA"))) => MergeStrategy.discard
    case PathList(xs@_*) if ((xs.head == "META-INF") && (xs.last == "org.apache.hadoop.fs.FileSystem")) => MergeStrategy.concat
    case "log4j.properties" => MergeStrategy.discard
    case "log4j2.xml" => MergeStrategy.discard
    case "application.conf" => MergeStrategy.concat
    case "META-INF/io.netty.versions.properties" => MergeStrategy.concat
    case "mime.types" => MergeStrategy.first
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)

//group name for publishing artifacts
lazy val miscSettings = Seq(
  organization := "com.uebercomputing"
)

lazy val combinedSettings = miscSettings ++ assemblyPluginSettings

////////////////////////////////////////////////////////////////////////////
// Project/module definitions                                             //
////////////////////////////////////////////////////////////////////////////

lazy val analyticsBaseDir = "analytics"

lazy val datasetAnalytics = (project in file(s"${analyticsBaseDir}/dataset"))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(combinedSettings: _*)
  .settings(
    libraryDependencies :=
      commonDependencies ++
        sparkDependencies ++
        awsDependencies ++
        testDependencies ++
        sparkTestDependencies
  )

lazy val rddAnalytics = (project in file(s"${analyticsBaseDir}/rdd"))
.configs(IntegrationTest)
.settings(Defaults.itSettings: _*)
.settings(combinedSettings: _*)
.settings(
  libraryDependencies :=
    commonDependencies ++
      sparkDependencies ++
      awsDependencies ++
      testDependencies ++
      sparkTestDependencies
)

//root just needs to aggregate all projects
//when we use any sbt command it gets run for all subprojects also
lazy val root = (project in file("."))
  .aggregate(datasetAnalytics, rddAnalytics)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(publish := {})
  .settings(assemblyPluginSettings: _*)
