package model


data class AdPoolChanged(val newPool: Set<Ad>)

data class AdChanged(
    val ad: Ad,
    val durationMs: Long,
    val detections: List<Person>
)

data class Ad(override val id: String, val content: String) : Persistable

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


