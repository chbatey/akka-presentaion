val deps = Seq(
      "com.typesafe.akka" % "akka-actor_2.11" % "2.3.11"
   )

lazy val root = (project in file(".")).
  settings(
    name := "akka-presentation",
    version := "1.0",
    scalaVersion := "2.11.5",
    libraryDependencies ++= deps
  )


