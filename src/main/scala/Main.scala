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
				|   'catalog-type' = 'rest',
				|   'uri' = 'http://iceberg-rest:8181',
				|   'warehouse' = 'file:///opt/flink/iceberg_warehouse',
				|   'property-version' = '1'
				| )
				|""".stripMargin)

		tableEnv.executeSql("USE CATALOG iceberg")
		tableEnv.executeSql("USE db")  // assuming you've created iceberg.db

		// Kafka source table
		tableEnv.executeSql(
			"""
  		|	CREATE TABLE kafka_tv_events (
			| user_id STRING
			| show_id STRING
			| device STRING
			| event_ts BIGINT
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

		// Iceberg sink table
		tableEnv.executeSql(
			"""
			|CREATE TABLE iceberg.db.tv_play_analytics (
			| user_id STRING
			| show_id STRING
			| device STRING
			| event_ts BIGINT
			| duration_sec INT
			|) PARTITION BY (user_id)
			|""".stripMargin
		)

		tableEnv.executeSql(
			"""
			|INSERT INTO iceberg.db.tv_play_analytics
			|SELECT user_id, device, show_id, event_ts, duration_sec FROM kafka_tv_events
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
