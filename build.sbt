resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"

val akkaVersion = "2.4-SNAPSHOT"

resolvers += Resolver.mavenLocal

val deps = Seq(
//      "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
//      "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
      "com.github.krasserm" %% "akka-persistence-cassandra" % "0.4-SNAPSHOT"
   )

lazy val root = (project in file(".")).
  settings(
    name := "akka-presentation",
    version := "1.0",
    scalaVersion := "2.11.5",
    libraryDependencies ++= deps
  )


