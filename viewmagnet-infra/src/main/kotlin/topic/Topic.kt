package topic

import model.Mode

data class TopicContext(
    val country: Country = Country.ALL,
    val city: City = City.ALL,
    val district: District = District.ALL,
    val individual: String = "ALL",
    val mode: Mode = Mode.REAL
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
    val source: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class Topic<T>(val payload: T, val header: TopicHeader)