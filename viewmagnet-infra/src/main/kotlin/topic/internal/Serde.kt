package topic.internal

internal interface Serde<T> {

    fun serialize(instance: T): ByteArray

    fun deserialize(instance: ByteArray): T
}