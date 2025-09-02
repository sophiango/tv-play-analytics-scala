import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import java.util.Properties

object Main {
	def main(args: Array[String]): Unit = {
		val env = StreamExecutionEnvironment.getExecutionEnvironment
//		env.enableCheckpointing(10000)
		val kafkaProps = new Properties()
		kafkaProps.setProperty("bootstrap.servers", "kafka:29092")
		kafkaProps.setProperty("group.id", "tv-play-consumer-group")

		val kafkaSource = new FlinkKafkaConsumer[String](
			"tv_play_events",
			new SimpleStringSchema(),
			kafkaProps
		)

		val stream = env.addSource(kafkaSource)

		stream.print()

		env.execute("tv_play_event_stream_job")
	}
}
