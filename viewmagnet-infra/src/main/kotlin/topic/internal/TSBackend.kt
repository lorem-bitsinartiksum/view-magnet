package topic.internal

import kotlinx.coroutines.channels.ReceiveChannel
import topic.TopicContext

internal interface TSBackend {

    fun publish(name: String, context: TopicContext, payload: ByteArray)

    fun subscribe(name: String, context: TopicContext): ReceiveChannel<ByteArray>
}