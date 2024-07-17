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
