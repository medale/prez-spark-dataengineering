import sbt._

object Dependencies {

  val sparkVersion = "2.4.0"

  //match Spark's pom for these dependencies!
  val scalaVersionStr = "2.11.12"
  val hadoopVersion = "2.7.3"
  // version must be compatible with hadoop-aws API!
  val awsSdkVersion = "1.7.4"
  //end of Spark version match

  lazy val commonDependencies = Seq(
     ("org.scala-lang" % "scala-library" % scalaVersionStr),
     ("commons-io" % "commons-io" % "2.6")
  )

  //Avro, CSV - https://spark-packages.org/
  lazy val sparkDependenciesBase = Seq(
    ("org.apache.spark" %% "spark-core" % sparkVersion)
      .exclude("org.scalatest", "scalatest_2.11"),
    ("org.apache.spark" %% "spark-sql" % sparkVersion)
      .exclude("org.scalatest", "scalatest_2.11")
  )

  lazy val sparkDependencies = sparkDependenciesBase.map(_ % "provided")

  //for reading/writing from/to AWS s3a
  val awsDependencies = Seq(
    ("com.amazonaws" % "aws-java-sdk" % awsSdkVersion)
      .exclude("com.fasterxml.jackson.core", "jackson-core")
      .exclude("com.fasterxml.jackson.core", "jackson-annotations")
      .exclude("com.fasterxml.jackson.core", "jackson-databind"),
    ("org.apache.hadoop" % "hadoop-common" % hadoopVersion % "provided")
      .exclude("commons-beanutils", "commons-beanutils")
      .exclude("commons-beanutils", "commons-beanutils-core")
      .exclude("com.fasterxml.jackson.core", "jackson-core")
      .exclude("com.fasterxml.jackson.core", "jackson-annotations")
      .exclude("com.fasterxml.jackson.core", "jackson-databind")
      .exclude("org.slf4j", "slf4j-api"),
    ("org.apache.hadoop" % "hadoop-aws" % hadoopVersion)
      .exclude("org.apache.hadoop", "hadoop-common")
      .exclude("com.amazonaws", "aws-java-sdk")
      .exclude("commons-beanutils", "commons-beanutils")
      .exclude("commons-beanutils", "commons-beanutils-core")
      .exclude("com.fasterxml.jackson.core", "jackson-core")
      .exclude("com.fasterxml.jackson.core", "jackson-annotations")
      .exclude("com.fasterxml.jackson.core", "jackson-databind")
      .exclude("org.slf4j", "slf4j-api")
  )

  //test and integration test dependencies/scope
  lazy val testDependencies = Seq(
    ("org.scalatest" %% "scalatest" % "3.0.7" % "it,test")
  )

  lazy val sparkTestDependencies = Seq(
    ("com.holdenkarau" %% "spark-testing-base" % "2.4.0_0.11.0" % "it,test"),
    ("org.apache.spark" %% "spark-hive" % sparkVersion % "it,test")
  )
}
