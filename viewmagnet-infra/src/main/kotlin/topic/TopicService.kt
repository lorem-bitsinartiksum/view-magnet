package topic


import topic.internal.TSBackend
import topic.internal.TSNatsBackend
import topic.internal.serde.JsonSerde
import topic.internal.serde.Serde
import java.util.concurrent.atomic.AtomicInteger

typealias UnsubscribeToken = () -> Unit

class TopicService<TopicType> private constructor(
    private val ts: TSBackend,
    private var serde: Serde<Topic<TopicType>>,
    private val topicClass: Class<TopicType>,
    private val serviceName: String,
    val activeCtx: TopicContext
) {

    private val subscriberCount = AtomicInteger()


    fun publish(topic: TopicType, context: TopicContext = activeCtx) {

        val toPublish = Topic(topic, TopicHeader(serviceName))
        val serialized = serde.serialize(toPublish)
        ts.publish(topicClass.name, context, serialized)
    }

    fun subscribe(consumer: (Topic<TopicType>) -> Unit): UnsubscribeToken {

        subscriberCount.incrementAndGet()

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

        fun <TopicType> createFor(
            topicClass: Class<TopicType>,
            serviceName: String,
            activeCtx: TopicContext
        ): TopicService<TopicType> {

            return TopicService(
                TSNatsBackend(),
                JsonSerde(topicClass),
                topicClass,
                serviceName,
                activeCtx
            )
        }
    }
}

