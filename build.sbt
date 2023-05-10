ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "ClickerApi"
  )
libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"            % "2.0.13",
  "dev.zio"       %% "zio-json"       % "0.5.0",
  "io.d11"        %% "zhttp"          % "2.0.0-RC10",
  "io.getquill"   %% "quill-zio"      % "4.3.0",
  "io.getquill"   %% "quill-jdbc-zio" % "4.3.0",
  "org.postgresql" % "postgresql"             % "42.2.10"
)
