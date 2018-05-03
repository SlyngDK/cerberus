name := "cerberus"

version := "0.1.0"

scalaVersion := "2.12.5"

organization := "dk.slyng"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/SlyngDK/cerberus"),
    "scm:git:git@github.com:SlyngDK/cerberus.git"
  )
)

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"