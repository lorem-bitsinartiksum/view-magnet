package topic.internal.serde

import com.google.gson.Gson

class JsonSerde<T>(private val topicClass: Class<T>) : Serde<T> {

    private val gson = Gson()

    override fun serialize(instance: T): ByteArray {

        return gson.toJson(instance).toByteArray()
    }

    override fun deserialize(instance: ByteArray): T {

        val jsonTxt = String(instance)

        return gson.fromJson(jsonTxt, topicClass)
    }
}