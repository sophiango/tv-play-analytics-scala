name := "tv_play_analytics"

version := "0.1"

scalaVersion := "2.12.17"

ThisBuild / resolvers += Resolver.mavenCentral

libraryDependencies ++= Seq(
	"org.apache.flink" %% "flink-scala" % "1.17.2",
	"org.apache.flink" %% "flink-streaming-scala" % "1.17.2",
	"org.apache.flink" % "flink-clients" % "1.17.2",
	"org.apache.flink" % "flink-connector-kafka" % "1.17.2"
)

assembly / mainClass := Some("Main")
assembly / assemblyMergeStrategy := {
	case PathList("META-INF", "io.netty.versions.properties") =>
		MergeStrategy.discard
	case PathList("META-INF", xs @ _*) =>
		MergeStrategy.discard
	case x =>
		MergeStrategy.first
}
