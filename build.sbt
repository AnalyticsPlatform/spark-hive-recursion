
lazy val commonSettings = Seq(

  organization := "org.kaveh-hariri",
  version := "1.0",
  scalaVersion := "2.11.8",
  mainClass in Compile := Some("org.kaveh_hariri.utility.spark.hive_recursion.MainRun")
)

lazy val main = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name:= "spark-hive-recursion",
    libraryDependencies += "org.apache.commons" % "commons-compress" % "1.12",
    libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0",
    libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.0",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.0",
    libraryDependencies += "org.apache.spark" % "spark-hive_2.11" % "2.1.0",
    libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11",
    libraryDependencies += "com.typesafe" % "config" % "1.3.1",
    libraryDependencies += "commons-io" % "commons-io" % "2.4",
    libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "2.5.0",
    libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.5",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.5.3",
    libraryDependencies += "joda-time" % "joda-time" % "2.8.2"
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
