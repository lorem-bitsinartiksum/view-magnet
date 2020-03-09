package model

typealias Similarity = Float

data class AdPoolChanged(val newPool: Set<Pair<Ad, Similarity>>)

data class AdChanged(
    val ad: Ad,
    val durationMs: Long,
    val detections: List<Person>
)

data class Ad(override val id: String, val content: String) : Persistable

data class AdWithFeature(override val id: String, val content: String, val feature : List<Float> = emptyList()) : Persistable

data class Person(val gender: Gender, val age: Age)


enum class Gender {
    MAN,
    WOMAN,
    UNDETECTED
}

enum class Age {
    CHILD,
    BABY,
    ELDER,
    ADULT
}


