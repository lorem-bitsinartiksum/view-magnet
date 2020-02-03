package topic.internal

import topic.TopicContext
import topic.UnsubscribeToken

internal interface TSBackend {

    fun publish(name: String, context: TopicContext, payload: ByteArray)

    fun subscribe(name: String, context: TopicContext, consumer: (ByteArray) -> Unit): UnsubscribeToken
}