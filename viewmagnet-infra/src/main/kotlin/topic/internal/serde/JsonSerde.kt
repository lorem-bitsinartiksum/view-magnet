package topic.internal.serde


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import topic.Topic

internal class JsonSerde<TopicType>(private val clazz: Class<TopicType>) : Serde<Topic<TopicType>> {

    private val mapper = jacksonObjectMapper()

    override fun serialize(instance: Topic<TopicType>): ByteArray {

        return mapper.writeValueAsBytes(instance)
    }

    override fun deserialize(instance: ByteArray): Topic<TopicType> {

        val type = mapper.typeFactory.constructParametricType(Topic::class.java, clazz)

        val readValue = mapper.readValue<Topic<TopicType>>(instance, type)

        val payload = readValue.payload
        return readValue
    }
}