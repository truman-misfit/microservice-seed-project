name := """misms-seed-project"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.amazonaws" % "aws-java-sdk" % "1.9.40",
  "com.amazonaws" % "amazon-sqs-java-messaging-lib" % "1.0.0",
  "org.elasticmq" %% "elasticmq-rest-sqs" % "0.8.8",
  // Automatically translate plain English stories to BDD test cases
  "org.scalatest" % "scalatest_2.11" % "3.0.0-M1",
  "info.cukes" % "cucumber-scala_2.11" % "1.2.2",
  // Integrate Swagger UI with Play! 2.x
  "com.wordnik" % "swagger-play2_2.11" % "1.3.12_play24",
  // AWS KCL and KPL
  // "com.amazonaws" % "amazon-kinesis-producer" % "0.9.0",
  "com.amazonaws" % "amazon-kinesis-client" % "1.4.0"
)

resolvers += "Sonatype-Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += Resolver.mavenLocal

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
// routesGenerator := InjectedRoutesGenerator

// Play2-cucumber integration
cucumberSettings

cucumberFeaturesLocation := "./test/features"

cucumberStepsBasePackage := "features.steps"