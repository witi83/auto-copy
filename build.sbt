import AssemblyKeys._

assemblySettings

assemblyOption in assembly ~= { _.copy(includeScala = false) }

name := "AutoCopy"

version := "1.1"

jarName in assembly := s"${name.value.replace(" ", "-").toLowerCase}-${version.value}.jar"

scalaVersion := "2.11.2"

mainClass := Some("Main")

incOptions := incOptions.value.withNameHashing(true)

fork in run := true

javacOptions ++= Seq("-Xlint:unchecked")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "org.apache.commons" % "commons-vfs2" % "2.0" exclude("org.apache.maven.scm", "maven-scm-provider-svnexe") exclude("org.apache.maven.scm", "maven-scm-api")

libraryDependencies += "commons-httpclient" % "commons-httpclient" % "3.1"

libraryDependencies += "commons-net" % "commons-net" % "3.3"
