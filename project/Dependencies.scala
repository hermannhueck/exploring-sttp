import sbt._

object Dependencies {

  import Versions._

  lazy val sttpCore     = "com.softwaremill.sttp.client3" %% "core"              % sttpVersion
  lazy val sttpOkhttp   = "com.softwaremill.sttp.client3" %% "okhttp-backend"    % sttpVersion
  lazy val sttpUPickle  = "com.softwaremill.sttp.client3" %% "upickle"           % sttpVersion
  lazy val sttpSlf4j    = "com.softwaremill.sttp.client3" %% "slf4j-backend"     % sttpVersion
  lazy val sttpAkkaHttp = "com.softwaremill.sttp.client3" %% "akka-http-backend" % sttpVersion
  lazy val sttpZio      = "com.softwaremill.sttp.client3" %% "zio"               % sttpVersion
  lazy val sttpFs2      = "com.softwaremill.sttp.client3" %% "fs2"               % sttpVersion
  lazy val sttpMonix    = "com.softwaremill.sttp.client3" %% "monix"             % sttpVersion
  lazy val sttpJson4s   = "com.softwaremill.sttp.client3" %% "json4s"            % sttpVersion
  lazy val sttpCirce    = "com.softwaremill.sttp.client3" %% "circe"             % sttpVersion
  lazy val json4sNative = "org.json4s"                    %% "json4s-native"     % js4sVersion
  lazy val akkaStream   = "com.typesafe.akka"             %% "akka-stream"       % akkaVersion
  lazy val circeGeneric = "io.circe"                      %% "circe-generic"     % circeVersion
  // lazy val circeCore   = "io.circe"                      %% "circe-core"     % circeVersion
  // lazy val circeParser = "io.circe"                      %% "circe-parser"   % circeVersion
  // lazy val newtype     = "io.estatico"                   %% "newtype"        % newTypeVersion
  lazy val slf4jApi     = "org.slf4j"                      % "slf4j-api"         % slf4jVersion
  lazy val slf4jSimple  = "org.slf4j"                      % "slf4j-simple"      % slf4jVersion
  lazy val munit        = "org.scalameta"                 %% "munit"             % munitVersion

  // https://github.com/typelevel/kind-projector
  lazy val kindProjectorPlugin    = compilerPlugin(
    compilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  )
  // https://github.com/oleg-py/better-monadic-for
  lazy val betterMonadicForPlugin = compilerPlugin(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
  )
}
