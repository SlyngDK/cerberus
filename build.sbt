name := "cerberus"

version := "0.0.5"

scalaVersion := "2.12.4"

organization := "xyz.jyotman"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/jyotman/cerberus"),
    "scm:git:git@github.com:jyotman/cerberus.git"
  )
)

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"