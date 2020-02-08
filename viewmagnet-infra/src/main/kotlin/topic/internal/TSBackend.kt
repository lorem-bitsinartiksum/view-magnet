package topic.internal

import topic.TopicContext
import topic.UnsubscribeToken

internal interface TSBackend {

    fun publish(name: String, source: String, payload: ByteArray)

    fun subscribe(name: String, source: String, consumer: (ByteArray) -> Unit): UnsubscribeToken
}