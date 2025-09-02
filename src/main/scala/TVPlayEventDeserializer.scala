import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class TVPlayEventDeserializer extends Serializable {
	lazy val mapper = new ObjectMapper().registerModule(DefaultScalaModule)

	def parse(json: String): Option[TVPlayEvent] = {
		try {
			Some(mapper.readValue(json, classOf[TVPlayEvent]))
		} catch {
			case e: Exception => {
				println(s"JSON parse error: ${e.getMessage}")
				None
			}
		}
	}

}
