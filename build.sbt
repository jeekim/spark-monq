import AssemblyKeys._
import sbt.complete.DefaultParsers._

assemblySettings

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.specs2" %% "specs2-core" % "3.6.6" % "test"

libraryDependencies += "org.specs2" %% "specs2-gwt" % "3.6.6" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"

scalacOptions in (Compile,doc) := Seq("-groups", "-implicits")

scalacOptions in Test ++= Seq("-Yrangepos")
