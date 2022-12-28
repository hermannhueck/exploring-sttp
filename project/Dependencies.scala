import sbt._

object Dependencies {

  import Versions._

  lazy val sttpCore          = "com.softwaremill.sttp.client3"         %% "core"                  % sttpVersion
  lazy val sttpOkhttp        = "com.softwaremill.sttp.client3"         %% "okhttp-backend"        % sttpVersion
  lazy val sttpUPickle       = "com.softwaremill.sttp.client3"         %% "upickle"               % sttpVersion
  lazy val sttpSlf4j         = "com.softwaremill.sttp.client3"         %% "slf4j-backend"         % sttpVersion
  lazy val sttpAkkaHttp      = "com.softwaremill.sttp.client3"         %% "akka-http-backend"     % sttpVersion
  lazy val akkaStream        = "com.typesafe.akka"                     %% "akka-stream"           % akkaVersion
  lazy val sttpZio           = "com.softwaremill.sttp.client3"         %% "zio"                   % sttpVersion
  lazy val sttpFs2           = "com.softwaremill.sttp.client3"         %% "fs2"                   % sttpVersion
  // lazy val sttpFs2        = "com.softwaremill.sttp.client3"         %% "fs2ce2"                % sttpVersion
  lazy val sttpMonix         = "com.softwaremill.sttp.client3"         %% "monix"                 % sttpVersion
  lazy val sttpCirce         = "com.softwaremill.sttp.client3"         %% "circe"                 % sttpVersion
  lazy val circeGeneric      = "io.circe"                              %% "circe-generic"         % circeVersion
  // lazy val circeCore   = "io.circe"                      %% "circe-core"     % circeVersion
  // lazy val circeParser = "io.circe"                      %% "circe-parser"   % circeVersion
  lazy val sttpJson4s        = "com.softwaremill.sttp.client3"         %% "json4s"                % sttpVersion
  lazy val json4sNative      = "org.json4s"                            %% "json4s-native"         % js4sVersion
  lazy val sttpSprayJson     = "com.softwaremill.sttp.client3"         %% "spray-json"            % sttpVersion
  lazy val sttpPlayJson      = "com.softwaremill.sttp.client3"         %% "play-json"             % sttpVersion
  lazy val sttpZioJson       = "com.softwaremill.sttp.client3"         %% "zio-json"              % sttpVersion
  lazy val sttpJsoniter      = "com.softwaremill.sttp.client3"         %% "jsoniter"              % sttpVersion
  lazy val jsoniterMacros    = "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % jsoniterVersion
  lazy val sttpHttp4s        = "com.softwaremill.sttp.client3"         %% "http4s-backend"        % sttpVersion
  // lazy val sttpHttp4s     = "com.softwaremill.sttp.client3"         %% "http4s-ce2-backend"    % sttpVersion
  lazy val http4sBlazeClient = "org.http4s"                            %% "http4s-blaze-client"   % http4sVersion
  lazy val http4sEmberClient = "org.http4s"                            %% "http4s-ember-client"   % http4sVersion
  lazy val http4sCirce       = "org.http4s"                            %% "http4s-circe"          % http4sVersion
  lazy val slf4jApi          = "org.slf4j"                              % "slf4j-api"             % slf4jVersion
  lazy val slf4jSimple       = "org.slf4j"                              % "slf4j-simple"          % slf4jVersion
  lazy val munit             = "org.scalameta"                         %% "munit"                 % munitVersion
  // lazy val newtype     = "io.estatico"                   %% "newtype"        % newTypeVersion

  // https://github.com/typelevel/kind-projector
  lazy val kindProjectorPlugin    = compilerPlugin(
    compilerPlugin("org.typelevel" % "kind-projector" % kindProjectorVersion cross CrossVersion.full)
  )
  // https://github.com/oleg-py/better-monadic-for
  lazy val betterMonadicForPlugin = compilerPlugin(
    compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion)
  )

  val compilerDependencies = Seq(
    sttpCore,
    sttpOkhttp,
    sttpUPickle,
    sttpSlf4j,
    sttpAkkaHttp,
    akkaStream,
    sttpZio,
    sttpFs2,
    // sttpMonix,
    sttpCirce,
    sttpJson4s,
    json4sNative,
    sttpSprayJson,
    sttpPlayJson,
    sttpZioJson,
    sttpJsoniter,
    jsoniterMacros,
    sttpHttp4s,
    http4sBlazeClient,
    http4sEmberClient,
    http4sCirce,
    circeGeneric,
    // circeCore,
    // circeParser,
    // newtype,
    slf4jApi,
    slf4jSimple,
    munit
  )

  val testDependencies = Seq.empty

  val pluginDependencies = Seq(kindProjectorPlugin, betterMonadicForPlugin)

  val allDependencies = compilerDependencies ++ testDependencies ++ pluginDependencies
}
