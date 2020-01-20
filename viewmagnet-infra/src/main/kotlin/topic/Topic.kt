package topic


data class TopicContext(
    val country: Country = Country.ALL,
    val city: City = City.ALL,
    val district: District = District.ALL,
    val individual: String = "",
    val mode: Mode = Mode.SIM
)

enum class Country {
    TR, UK, ALL
}

enum class City {
    IST, LONDON, ALL
}

enum class District {
    KECIOREN, StJames, ALL
}

data class TopicHeader(
    val source: TopicContext,
    val createdAt: Long = System.currentTimeMillis()
)

data class Topic<T>(val payload: T, val header: TopicHeader)

enum class Mode {
    SIM, REAL
}
