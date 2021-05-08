// import AssemblyKeys._

// assemblySettings


lazy val commonSettings = Seq(
  scalaVersion := "2.12.12",
  resolvers += Resolver.mavenLocal
)

lazy val root = (project in file(".")).
  aggregate(util, core)

lazy val util = (project in file("util")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    )
  )

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.apache.spark" %% "spark-core" % "1.2.0"
    )
  )
