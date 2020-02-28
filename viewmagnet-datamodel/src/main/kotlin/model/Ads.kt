package model

import java.util.*

typealias AdId = String

data class AdPoolChanged(val newPool: Set<AdId>)

data class AdChanged(
    val id: String,
    val durationMs: Long,
    val detections: List<Person>
)

data class Ad(val id: String, val post: Base64)

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


