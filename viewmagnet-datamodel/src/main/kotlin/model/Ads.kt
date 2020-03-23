package model

import java.util.*

typealias Similarity = Float

data class AdPoolChanged(val newPool: Set<Pair<Ad, Similarity>>)

data class AdChanged(
    val ad: Ad,
    val durationMs: Long,
    val detections: List<Person>
)

data class AdReq(
    val id: String? = null,
    val content: String? = null,
    val user: User? = null,
    val title: String? = null,
    val description: String? = null,
    val targetGender: List<Gender>? = null,
    val targetAge: List<Age>? = null,
    val targetWeather: List<Weather>? = null,
    val targetLowTemp: Int? = null,
    val targetHighTemp: Int? = null,
    val targetLowSoundLevel: Int? = null,
    val targetHighSoundLevel: Int? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val feature : List<Float>? = null)

data class Ad(
    override val id: String,
    val content: String,
    val user: User = User(id = null, email = "", location = null, phone = null, username = null, password = null, token = null),
    val title: String = "",
    val description: String = "",
    val targetGender: List<Gender> = emptyList(),
    val targetAge: List<Age> = emptyList(),
    val targetWeather: List<Weather> = emptyList(),
    val targetLowTemp: Int = 0,
    val targetHighTemp: Int = 0,
    val targetLowSoundLevel: Int = 0,
    val targetHighSoundLevel: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()) : Persistable

data class AdWithFeature(
    override val id: String,
    val content: String,
    val feature : List<Float> = emptyList(),
    val user: User = User(id = null, email = "", location = null, phone = null, username = null, password = null, token = null),
    val title: String = "",
    val description: String = "",
    val targetGender: List<Gender> = emptyList(),
    val targetAge: List<Age> = emptyList(),
    val targetWeather: List<Weather> = emptyList(),
    val targetLowTemp: Int = 0,
    val targetHighTemp: Int = 0,
    val targetLowSoundLevel: Int = 0,
    val targetHighSoundLevel: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()) : Persistable


data class Person(val gender: Gender, val age: Age)

enum class Gender {
    MAN,
    WOMAN,
    UNDETECTED
}

enum class Age {
    BABY,
    CHILD,
    YOUNG,
    ADULT,
    ELDERLY
}