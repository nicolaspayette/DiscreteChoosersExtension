enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName := "dc"

netLogoClassManager := "net.cohesyslab.dc.DiscreteChoosersExtension"

netLogoZipSources := false

netLogoVersion := "6.1.1"

netLogoTarget := org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value / "dc")

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.8"

javaSource in Compile := baseDirectory.value / "discrete-choosers" / "src" / "main" / "java"

javaSource in Test := baseDirectory.value / "discrete-choosers" / "src" / "test" / "java"

unmanagedResourceDirectories in Test += baseDirectory.value/ "discrete-choosers" / "src" / "test" / "resources"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings", "-encoding", "utf8")

unmanagedBase := baseDirectory.value / "discrete-choosers" / "libs" / "rednaxela"

libraryDependencies ++= Seq(
  "com.intellij" % "annotations" % "12.0",
  "com.google.guava" % "guava" % "23.0",
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.nlogo" % "netlogo" % "6.1.1" % "test"
)

test in Test := (test in Test).dependsOn(Keys.`package` in Compile).value