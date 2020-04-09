package domain.Ad.repository

import com.github.slugify.Slugify
import com.mongodb.client.model.Filters
import model.*
import org.litote.kmongo.*
import repository.RepositoryService
import java.util.*


class AdRepository() {
    val rs = RepositoryService.createFor(AdWithFeature::class.java)

    fun create(ad: AdWithFeature): AdWithFeature? {
        val now = Date()
        rs.save(
            AdWithFeature(
                id = ad.id,
                user = ad.user,
                title = ad.title,
                description = ad.description,
                content = ad.content,
                targetGender = ad.targetGender,
                targetAge = ad.targetAge,
                targetWeather = ad.targetWeather,
                targetLowTemp = ad.targetLowTemp,
                targetHighTemp = ad.targetHighTemp,
                targetLowSoundLevel = ad.targetLowSoundLevel,
                targetHighSoundLevel = ad.targetHighSoundLevel,
                createdAt = now.toString(),
                updatedAt = now.toString(),
                feature = ad.feature
            )
        )
        return findById(ad.id)
    }

    fun delete(id: String) {
        rs.deleteById(id)
    }

    fun update(id: String, ad: AdReq): AdWithFeature? {
        val oldAd = rs.findById(id) ?: return null
        var newAd = oldAd?.copy(
            id = ad.id ?: oldAd.id,
            title = ad.title ?: oldAd.title,
            description = ad.description ?: oldAd.description,
            content = ad.content ?: oldAd.content,
            targetGender = ad.targetGender ?: oldAd.targetGender,
            targetAge = ad.targetAge ?: oldAd.targetAge,
            targetWeather = ad.targetWeather ?: oldAd.targetWeather,
            updatedAt = Date().toString(),
            targetHighSoundLevel = ad.targetHighSoundLevel ?: oldAd.targetHighSoundLevel,
            targetLowSoundLevel = ad.targetLowSoundLevel ?: oldAd.targetLowSoundLevel,
            targetHighTemp = ad.targetHighTemp ?: oldAd.targetHighTemp,
            targetLowTemp = ad.targetLowTemp ?: oldAd.targetLowTemp,
            feature = ad.feature ?: oldAd.feature
        )
        rs.deleteById(oldAd?.id)
        rs.save(newAd)
        return findById(newAd.id)
    }

    fun findById(id: String): AdWithFeature? {
        return rs.findById(id)
    }


    fun findByFilters(
        title: String?,
        email: String?,
        targetAge: String?,
        targetGender: String?,
        targetWeather: String?
    ): List<AdWithFeature> {

        return rs.filter {
            var passes = true
            passes = title?.equals(it.title) ?: passes
            passes = email?.equals(it.user.email) ?: passes
            passes = if (targetAge != null) it.targetAge.contains(Age.valueOf(targetAge.toUpperCase())) else passes
            passes =
                if (targetGender != null) it.targetGender.contains(Gender.valueOf(targetGender.toUpperCase())) else passes
            passes =
                if (targetWeather != null) it.targetWeather.contains(Weather.valueOf(targetWeather.toUpperCase())) else passes
            passes
        }.asSequence().toList()
    }

}