organization := "org.xerial.silk"
sonatypeProfileName := "org.xerial"
description := "A framework for simplifying SQL pipelines"
scalaVersion in Global := "2.11.7"

packSettings
packMain := Map("silk" -> "xerial.silk.cui.SilkMain")
packExclude := Seq("silk")

lazy val root = Project(id = "silk", base = file(".")).settings(
  publish := {}
).aggregate(core, cui)


resolvers in Global += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val commonSettings = Seq(
  scalacOptions in Compile := Seq("-language:experimental.macros")
)

lazy val core = Project(id = "silk-core", base = file("silk-core"))
  .settings(commonSettings)
  .settings(
    name := "silk-core",
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.2.0",
      "org.xerial" % "xerial-lens" % "3.3.8",
      "org.scala-lang" % "scalap" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.ow2.asm" % "asm-all" % "4.1",
      "com.esotericsoftware.kryo" % "kryo" % "2.20" exclude("org.ow2.asm", "asm"),
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.xerial" % "sqlite-jdbc" % "3.8.11.1",
      "org.xerial.msgframe" % "msgframe-core" % "0.1.0-SNAPSHOT"
    )
  )

lazy val cui = Project(id = "silk-cui", base = file("silk-cui")).settings(
  name := "silk-cui"
).dependsOn(core % "test->test;compile->compile")

pomExtra in Global := {
    <url>http://xerial.org/silk</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/xerial/silk.git</connection>
      <developerConnection>scm:git:git@github.com:xerial/silk.git</developerConnection>
      <url>github.com/xerial/silk.git</url>
    </scm>
    <properties>
      <scala.version>{scalaVersion.value}</scala.version>
      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <developers>
      <developer>
        <id>leo</id>
        <name>Taro L. Saito</name>
        <url>http://xerial.org/leo</url>
      </developer>
    </developers>
}