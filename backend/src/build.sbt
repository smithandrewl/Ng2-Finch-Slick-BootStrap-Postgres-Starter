name := "backend"

version := "1.0"

scalaVersion := "2.11.0"

resolvers += "twttr" at "https://maven.twttr.com/"

libraryDependencies ++= Seq(
  "com.twitter"                %% "finagle-http"     % "6.35.0",
  "com.typesafe.slick"         %% "slick"            % "3.1.1",
  "io.argonaut"                %% "argonaut"         % "6.1",
  "com.github.finagle"         %% "finch-core"       % "0.10.0",
  "com.github.finagle"         %% "finch-circe"      % "0.10.0",
  "org.bitbucket.b_c"           % "jose4j"           % "0.5.1",
  "com.twitter"                %% "twitter-server"   % "1.20.0"
)
