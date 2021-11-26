enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName := "dc"

netLogoClassManager := "net.cohesyslab.dc.DiscreteChoosersExtension"

netLogoZipSources := false

netLogoVersion := "6.2.1"

netLogoTarget := org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value / "dc")

version := "0.0.0-SNAPSHOT"

scalaVersion := "2.12.8"

scalaSource in Compile := baseDirectory.value / "src" / "main" / "scala"

javaSource in Compile := baseDirectory.value / "discrete-choosers" / "src" / "main" / "java"

javaSource in Test := baseDirectory.value / "discrete-choosers" / "src" / "test" / "java"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings", "-encoding", "utf8")

unmanagedBase := baseDirectory.value / "discrete-choosers" / "libs" / "rednaxela"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "23.0",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "junit" % "junit" % "4.12" % "test"
)
