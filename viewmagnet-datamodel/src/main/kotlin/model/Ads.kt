package model

import java.util.*

typealias Similarity = Float

data class AdPoolChanged(val newPool: Set<Pair<Ad, Similarity>>)

data class AdChanged(
    val ad: Ad,
    val durationMs: Long,
    val detections: List<Person>
)

data class AdDTO(val ad: Ad?)

data class AdsDTO(val ads: List<Ad>, val adsCount: Int)

data class Ad(
    val id: String ? = null,
    val content: String? = null,
    val user: User? = null,
    val title: String,
    val description: String,
    val targetGender: List<Gender>?= null,
    val targetAge: List<Age>?= null,
    val targetWeather: List<Weather>?= null,
    val targetLowTemp: Int? = null,
    val targetHighTemp: Int? = null,
    val targetLowSoundLevel: Int? = null,
    val targetHighSoundLevel: Int? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null)

data class AdWithFeature(
    val id: String ? = null,
    val content: String,
    val user: User? = null,
    val title: String,
    val description: String,
    val targetGender: List<Gender>?= null,
    val targetAge: List<Age>?= null,
    val targetWeather: List<Weather>?= null,
    val targetLowTemp: Int? = null,
    val targetHighTemp: Int? = null,
    val targetLowSoundLevel: Int? = null,
    val targetHighSoundLevel: Int? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val feature : List<Float> = emptyList())

data class Person(val gender: Gender, val age: Age)


enum class Gender {
    MALE, FEMALE, UNDETECTED
}

enum class Age {
    BABY, CHILD, YOUNG, ADULT, ELDERLY
}


