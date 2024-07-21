name := "ssen"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.13.4"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies += "com.lihaoyi" %% "cask" % "0.7.3"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-api" % "2.17.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.17.0",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.17.0",
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "4.3.0",
  "com.clickhouse" % "clickhouse-jdbc" % "0.6.2",
  "ch.qos.logback" % "logback-classic" % "1.5.6"
)

libraryDependencies += "org.lz4" % "lz4-java" % "1.8.0"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-streams" % "4.3.0"
