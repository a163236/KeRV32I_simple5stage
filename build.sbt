name := "KeRV32I_simple5stage"

version := "0.1"

scalaVersion := "2.13.8"

scalacOptions ++= Seq(
  "-language:reflectiveCalls",
  "-deprecation",
  "-feature",
  "-Xcheckinit",
  "-P:chiselplugin:genBundleElements"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
)

addCompilerPlugin("edu.berkeley.cs" %% "chisel3-plugin" % "latest.release" cross CrossVersion.full)

libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "latest.release"
libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "latest.release"
//libraryDependencies += "edu.berkeley.cs" %% "rocketchip" % "latest.release"
