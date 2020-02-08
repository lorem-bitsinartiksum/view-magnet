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
    val activeSource: String
) {

    private val subscriberCount = AtomicInteger()


    fun publish(topic: TopicType, source: String = activeSource) {

        val toPublish = Topic(topic, TopicHeader(activeSource))
        val serialized = serde.serialize(toPublish)
        ts.publish(topicClass.name, source, serialized)
    }

    fun subscribe(consumer: (Topic<TopicType>) -> Unit): UnsubscribeToken {

        subscriberCount.incrementAndGet()

        val unsubTok = ts.subscribe(topicClass.name, activeSource) {
            val topic = serde.deserialize(it)
            consumer(topic)
        }

        return {
            if (subscriberCount.decrementAndGet() == 0)
                unsubTok()
        }
    }

    companion object {

        fun <TopicType> createFor(topicClass: Class<TopicType>, activeSource: String): TopicService<TopicType> {

            return TopicService(
                TSNatsBackend(),
                JsonSerde(topicClass),
                topicClass,
                activeSource
            )
        }
    }
}

