package topic.internal

import kotlinx.coroutines.channels.ReceiveChannel

internal interface TSBackend {

    fun publish(name: String, payload: ByteArray)

    fun subscribe(name: String): ReceiveChannel<ByteArray>
}