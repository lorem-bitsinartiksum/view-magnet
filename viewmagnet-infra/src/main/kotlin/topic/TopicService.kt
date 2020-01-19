package topic

import topic.internal.Serde
import topic.internal.TSBackend


class TopicService<TopicType> private constructor(
    private val ts: TSBackend,
    private var serde: Serde<TopicType>,
    var context: TopicContext = TopicContext()
) {


    fun publish(topic: TopicType) {

    }

    fun subscribe(consumer: TopicConsumer<TopicType>) {

    }

}


data class TopicContext(
    val country: String = "",
    val city: String = "",
    val district: String = "",
    val invidual: String = ""
)

data class TopicHeader(
    val createdAt: Long = System.currentTimeMillis()
)

typealias TopicConsumer<TopicType> = (TopicType, TopicHeader) -> Unit