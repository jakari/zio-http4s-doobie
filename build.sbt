val Http4sVersion = "0.20.8"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val circeVersion = "0.11.1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.rin",
    name := "example",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "com.github.pureconfig" %% "pureconfig" % "0.12.1",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,

      // Start with this one
      "org.tpolecat" %% "doobie-core"      % "0.8.4",

      // And add any of these as needed
      "org.tpolecat" %% "doobie-h2"        % "0.8.4",          // H2 driver 1.4.199 + type mappings.
      "org.tpolecat" %% "doobie-hikari"    % "0.8.4",          // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres"  % "0.8.4",          // Postgres driver 42.2.5 + type mappings.ements.

      "dev.zio"       %% "zio"                  % "1.0.0-RC13",
      "dev.zio"       %% "zio-interop-cats"     % "2.0.0.0-RC4"
    )
  )


scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)
