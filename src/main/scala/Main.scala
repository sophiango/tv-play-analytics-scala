import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.util.Properties

object Main {
	def main(args: Array[String]): Unit = {
		val env = StreamExecutionEnvironment.getExecutionEnvironment
		val tableEnv = StreamTableEnvironment.create(env)

		// Create Iceberg catalog
		tableEnv.executeSql(
			"""
				| CREATE CATALOG iceberg WITH (
				|   'type' = 'iceberg',
				|   'catalog-type' = 'hadoop',
				|   'warehouse' = 'file:///opt/flink/iceberg_warehouse',
				|   'property-version' = '1'
				| )
				|""".stripMargin)

		tableEnv.executeSql("USE CATALOG iceberg")
		tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS tv;")
		tableEnv.executeSql("USE tv")  // assuming you've created iceberg.db

//		tableEnv.executeSql("""DROP TABLE IF EXISTS iceberg.tv.watch_events""")
		// Iceberg sink table
		tableEnv.executeSql(
			"""
				|CREATE TABLE IF NOT EXISTS watch_events (
				| user_id INT,
				| show_id INT,
				| device STRING,
				| event_ts FLOAT,
				| duration_sec INT
				|) WITH (
				| 'format-version' = '2',
				| 'write.format.default' = 'parquet'
				|)
				|""".stripMargin
		)

//		tableEnv.executeSql("""DROP TABLE IF EXISTS kafka_watch_events""")
		// Kafka source table
		tableEnv.executeSql(
			"""
  		|	CREATE TABLE IF NOT EXISTS kafka_watch_events (
			| user_id INT,
			| show_id INT,
			| device STRING,
			| event_ts FLOAT,
			| duration_sec INT
			| ) WITH (
			| 'connector' = 'kafka',
			| 'topic' = 'tv_play_events',
			| 'properties.bootstrap.servers' = 'kafka:29092',
			| 'format' = 'json',
			| 'scan.startup.mode' = 'earliest-offset'
			| )
			|""".stripMargin
		)

		tableEnv.executeSql(
			"""
			|INSERT INTO watch_events
			|SELECT * FROM kafka_watch_events
			|""".stripMargin
		)

//		val kafkaProps = new Properties()
//		kafkaProps.setProperty("bootstrap.servers", "kafka:29092")
//		kafkaProps.setProperty("group.id", "tv-play-consumer-group")
//
//		val kafkaSource = new FlinkKafkaConsumer[String](
//			"tv_play_events",
//			new SimpleStringSchema(),
//			kafkaProps
//		)
//
//		val rawStream: DataStream[String] = env.addSource(kafkaSource)
//
//		val mapper = new ObjectMapper()
//		mapper.registerModule(DefaultScalaModule)
//
//		// Parse the incoming Kafka strings into typed events
//		val parser = new TVPlayEventDeserializer()
//		val parsedEvents: DataStream[TVPlayEvent] = rawStream
//			.flatMap(json => parser.parse(json))
//			.name("Parse JSON to TVPlayEvent")
//
//		// Output parsed events
//		parsedEvents.print()
//
//		// Start the Flink job
//		env.execute("tv_play_event_stream_job")
	}
}
