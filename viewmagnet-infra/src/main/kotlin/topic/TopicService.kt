package topic


import com.google.common.flogger.FluentLogger
import model.Mode
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
    private val logger = FluentLogger.forEnclosingClass()
    private val subscriberCount = AtomicInteger()


    fun publish(topic: TopicType, context: TopicContext = activeCtx) {

        val realCtx = if (context.individual == activeCtx.individual)
            context.copy(individual = "ALL") else context

        val toPublish = Topic(topic, TopicHeader(serviceName))
        val serialized = serde.serialize(toPublish)
        ts.publish(topicClass.name, realCtx, serialized)
        logger.atInfo().log("Publishing: $topic")
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
        /**
         * mode vm arg has higher priority than mode inside passed [activeCtx],
         * if -Dmode=SIM/REAL passed it will overwrite passed mode.
         */
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
                activeCtx.copy(
                    individual = serviceName,
                    mode = Mode.valueOf(System.getProperty("mode", activeCtx.mode.toString()).toUpperCase())
                )
            )
        }
    }
}
