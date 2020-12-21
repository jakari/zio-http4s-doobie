val Http4sVersion = "0.21.14"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val circeVersion = "0.12.3"
val doobieVersion = "0.9.2"
val zioVersion = "1.0.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.rin",
    name := "example",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "com.github.pureconfig" %% "pureconfig" % "0.12.1",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      // Start with this one
      "org.tpolecat" %% "doobie-core"      % doobieVersion,
      // And add any of these as needed
      "org.tpolecat" %% "doobie-hikari"    % doobieVersion,          // HikariCP transactor.
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,          // Postgres driver 42.2.5 + type mappings.ements.

      "dev.zio"       %% "zio"                  % zioVersion,
      "dev.zio"       %% "zio-macros"                  % zioVersion,
      "dev.zio"       %% "zio-interop-cats"     % "2.2.0.1"
    )
  )


scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)
