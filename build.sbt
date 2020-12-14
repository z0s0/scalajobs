name := "scalajobs"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= ProjectConfig.deps
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
