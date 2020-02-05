package topic.internal.serde

internal interface Serde<T> {

    fun serialize(instance: T): ByteArray

    fun deserialize(instance: ByteArray): T

}