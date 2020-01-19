package topic.internal

import topic.TopicContext
import topic.UnsubscribeToken

internal class TSNatsBackend : TSBackend {

    override fun publish(name: String, context: TopicContext, payload: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subscribe(name: String, context: TopicContext, consumer: (ByteArray) -> Unit): UnsubscribeToken {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}