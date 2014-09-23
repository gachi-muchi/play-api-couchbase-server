name := "play-app-server"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "com.google.inject" % "guice" % "3.0",
  "com.couchbase.client" % "couchbase-client" % "1.4.4"
)     

play.Project.playJavaSettings
