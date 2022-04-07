ThisBuild / scalaVersion := "3.1.1"

val Versions =
  new {
    val refined = "0.9.28"
    val monocle = "3.1.0"
    val catsEffect = "3.3.10"
    val circe = "0.14.1"
  }

lazy val commonSettings = Seq(
  scalacOptions -= "-Xfatal-warnings",
  libraryDependencies ++= Seq(
    "eu.timepit" %% "refined" % Versions.refined,
    "eu.timepit" %% "refined-cats" % Versions.refined,
    "dev.optics" %% "monocle-core" % Versions.monocle,
    "dev.optics" %% "monocle-macro" % Versions.monocle,
    "org.typelevel" %% "cats-effect" % Versions.catsEffect,
    "io.circe" %% "circe-generic" % Versions.circe,
    "io.circe" %% "circe-refined" % Versions.circe,
  ),
)

lazy val core = (project in file("modules/core"))
  .settings(
    commonSettings
  )

lazy val ordertaking = (project in file("modules/order-taking"))
  .settings(
    commonSettings
  )
  .dependsOn(core)
