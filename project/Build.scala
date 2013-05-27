import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Giffyt-REST-API-Phase2"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "com.restfb" % "restfb" % "1.6.12",
    "net.vz.mongodb.jackson" %% "play-mongo-jackson-mapper" % "1.1.0",
    "com.typesafe" %% "play-plugins-mailer" % "2.1.0"

  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
