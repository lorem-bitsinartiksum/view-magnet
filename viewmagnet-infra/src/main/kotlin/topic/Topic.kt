package topic


data class TopicContext(
    val country: String = "",
    val city: String = "",
    val district: String = "",
    val individual: String = "",
    val mode: Mode = Mode.SIM
)

data class TopicHeader(
    val source: TopicContext,
    val createdAt: Long = System.currentTimeMillis()
)

data class Topic<T>(val payload: T, val header: TopicHeader)

enum class Mode {
    SIM, REAL
}
