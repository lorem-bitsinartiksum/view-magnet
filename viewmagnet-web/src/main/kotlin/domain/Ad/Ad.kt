package domain.Ad

import domain.User.User
import java.util.*

data class AdDTO(val ad: Ad?)

data class AdsDTO(val ads: List<Ad>, val adsCount: Int)

data class Ad (val slug: String? = null,
               val user: User? = null,
               val title: String,
               val description: String,
               val content: Base64? = null,
               val targetGender: String?= null,
               val targetAgeRange: IntRange?= null,
               val targetWeather: String?= null,
               val createdAt: Date? = null,
               val updatedAt: Date? = null)