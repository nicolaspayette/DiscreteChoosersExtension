enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName := "dc"

netLogoClassManager := "net.cohesyslab.dc.DiscreteChoosersExtension"

netLogoZipSources := false

netLogoVersion := "6.0.4"

netLogoTarget := org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value / "dc")

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.8"

scalaSource in Compile := baseDirectory.value / "src" / "main" / "scala"

javaSource in Compile := baseDirectory.value / "discrete-choosers" / "src" / "main" / "java"

javaSource in Test := baseDirectory.value / "discrete-choosers" / "src" / "test" / "java"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "utf8")

unmanagedBase := baseDirectory.value / "discrete-choosers" / "libs" / "rednaxela"

libraryDependencies ++= Seq(
  "com.intellij" % "annotations" % "12.0",
  "com.google.guava" % "guava" % "23.0"
)


