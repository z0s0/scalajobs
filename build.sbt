name := "scalajobs"

version := "0.1"

scalaVersion := "2.13.4"

scalacOptions += "-Ymacro-annotations"
libraryDependencies ++= ProjectConfig.deps
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full
)
Test / fork := true
