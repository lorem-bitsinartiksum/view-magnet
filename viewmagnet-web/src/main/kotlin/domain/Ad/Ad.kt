package domain.Ad

import domain.User.User
import java.util.*

enum class Age {
    BABY, CHILD, YOUNG, ADULT, ELDERLY
}

enum class Gender {
    MALE, FEMALE, UNDETECTED
}

enum class Weather {
    SUNNY, CLOUDY, WINDY, FOGGY, STORMY, SNOWY, RAINY, UNKNOWN
}

data class AdDTO(val ad: Ad?)

data class AdsDTO(val ads: List<Ad>, val adsCount: Int)

data class Ad (val slug: String? = null,
               val user: User? = null,
               val title: String,
               val description: String,
               val content: Base64? = null,
               val targetGender: List<Gender>?= null,
               val targetAge: List<Age>?= null,
               val targetWeather: List<Weather>?= null,
               val createdAt: Date? = null,
               val updatedAt: Date? = null)