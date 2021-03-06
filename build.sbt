import Dependencies._

ThisBuild / scalaVersion     := "2.13.4"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.cair"
ThisBuild / organizationName := "cair"

val spcVersion = "2.0.0"
val lsp4jVersion = "0.12.0"
val http4sVersion = "0.21.22"
val circeVersion = "0.13.0"
val logbackVersion = "1.2.4"
val scalaLoggingVersion = "3.9.4"

lazy val root = (project in file("."))
  .settings(
    name := "DefReaS",

    // For lenses and other cats typeclass stuff.
    scalacOptions += "-Ymacro-annotations",

    // For interop with LSP4J.
    scalacOptions += "-Xmixin-force-forwarders:false",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),

    // Generate semanticdb outputs on compile.
    addCompilerPlugin("org.scalameta" %% "semanticdb-scalac" % "4.4.24" cross CrossVersion.full),
    scalacOptions ++= Seq("-Yrangepos", "-P:semanticdb:text:on"),

    // Libraries we use.
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logbackVersion,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % spcVersion,
    libraryDependencies += "org.eclipse.lsp4j" % "org.eclipse.lsp4j" % lsp4jVersion,
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
    ),
  )
