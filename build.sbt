Global / excludeLintKeys +=   test / fork
Global / excludeLintKeys += run / mainClass
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val dependencies = Seq(
  "org.apache.hadoop" % "hadoop-common" % "3.3.5",
  "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.3",
  "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.3.3",
  "org.yaml" % "snakeyaml" % "2.2",
  "com.typesafe" % "config" % "1.4.2",
  "ch.qos.logback" % "logback-classic" % "1.4.7",
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "org.slf4j" % "slf4j-log4j12" % "1.7.30",
  "log4j" % "log4j" % "1.2.17",
  "org.yaml" % "snakeyaml" % "1.29",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test"

)

lazy val root = (project in file("."))
  .settings(
    name := "Cloud1",
    libraryDependencies ++= dependencies,
  )

compileOrder := CompileOrder.JavaThenScala
test / fork := true
run / fork := true
run / javaOptions ++= Seq(
  "-Xms8G",
  "-Xmx100G",
  "-XX:+UseG1GC"
)
Compile / mainClass := Some("com.lsc.Main")
run / mainClass := Some("com.lsc.Main")

val jarName = "Cloud-jar.jar"
assembly/assemblyJarName := jarName

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}