package topic

import topic.internal.TSBackend
import topic.internal.TSNatsBackend
import topic.internal.serde.JsonSerde
import topic.internal.serde.Serde
import java.util.concurrent.atomic.AtomicInteger


class TopicService<TopicType> private constructor(
    private val ts: TSBackend,
    private var serde: Serde<Topic<TopicType>>,
    private val topicClass: Class<TopicType>,
    var activeCtx: TopicContext = TopicContext()
) {

    private val subscriberCount = AtomicInteger()


    fun publish(topic: TopicType, context: TopicContext = activeCtx) {

        ts.publish(topicClass.name, context, serde.serialize(Topic(topic, TopicHeader(context))))
    }

    fun subscribe(consumer: (Topic<TopicType>) -> Unit): UnsubscribeToken {

        val unsubTok = ts.subscribe(topicClass.name, activeCtx) {
            val topic = serde.deserialize(it)
            consumer(topic)
        }

        return {
            if (subscriberCount.decrementAndGet() == 0)
                unsubTok()
        }
    }

    companion object {

        fun <TopicType> createFor(topicClass: Class<TopicType>, activeCtx: TopicContext): TopicService<TopicType> {
            return TopicService(TSNatsBackend(), JsonSerde(Topic::class.java) as Serde<Topic<TopicType>>, topicClass)
        }
    }
}


data class TopicContext(
    val country: String = "",
    val city: String = "",
    val district: String = "",
    val invidual: String = ""
)

data class TopicHeader(
    val source: TopicContext,
    val createdAt: Long = System.currentTimeMillis()
)

data class Topic<T>(val payload: T, val header: TopicHeader)
typealias UnsubscribeToken = () -> Unit