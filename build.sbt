import Versions._
import Dependencies._
import ScalacOptions._

val projectName        = "exploring-sttp"
val projectDescription = "Exploring Web Client sttp"

ThisBuild / fork                   := true
ThisBuild / turbo                  := true // default: false
ThisBuild / includePluginResolvers := true // default: false
Global / onChangedBuildSource      := ReloadOnSourceChanges

inThisBuild(
  Seq(
    version                  := projectVersion,
    scalaVersion             := scala2Version,
    publish / skip           := true,
    scalacOptions ++= defaultScalacOptions,
    semanticdbEnabled        := true,
    semanticdbVersion        := scalafixSemanticdb.revision,
    scalafixDependencies ++= Seq("com.github.liancheng" %% "organize-imports" % scalafixOrganizeImportsVersion),
    Test / parallelExecution := false,
    // run 100 tests for each property // -s = -minSuccessfulTests
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-s", "100"),
    initialCommands          :=
      s"""|
          |import scala.util.chaining._
          |import scala.concurrent.duration._
          |println()
          |""".stripMargin // initialize REPL
  )
)

lazy val root = (project in file("."))
  .settings(
    name                              := projectName,
    description                       := projectDescription,
    Compile / console / scalacOptions := consoleScalacOptions,
    libraryDependencies ++= Seq(
      sttpCore,
      sttpOkhttp,
      sttpUPickle,
      sttpSlf4j,
      sttpAkkaHttp,
      sttpZio,
      // sttpFs2,
      // sttpMonix,
      sttpJson4s,
      sttpCirce,
      json4sNative,
      akkaStream,
      circeGeneric,
      // circeCore,
      // circeParser,
      // newtype,
      slf4jApi,
      slf4jSimple,
      kindProjectorPlugin,
      betterMonadicForPlugin
    ) ++ Seq(
      munit
    ) // .map(_ % Test)
  )
