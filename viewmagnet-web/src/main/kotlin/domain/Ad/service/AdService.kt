package domain.Ad.service

import com.github.slugify.Slugify
import model.*
import domain.Ad.repository.AdRepository
import domain.Admin.repository.AdminRepository
import io.javalin.InternalServerErrorResponse
import io.javalin.BadRequestResponse

import domain.User.repository.UserRepository
import io.javalin.NotFoundResponse
import io.javalin.UnauthorizedResponse
import java.util.*

class AdService(private val adRepository: AdRepository,
                private val userRepository: UserRepository,
                private val adminRepository: AdminRepository) {

    inline fun <reified T : Enum<T>> enumContains(name: String?): Boolean {
        return enumValues<T>().any { it.name == name}
    }

    fun create(email: String?, ad: AdReq): AdWithFeature {
        var description: String = ""
        var targetGender: List<Gender> = emptyList()
        var targetAge: List<Age> = emptyList()
        var targetWeather: List<Weather> = emptyList()
        var targetLowTemp: Int = 0
        var targetHighTemp: Int = 0
        var targetLowSoundLevel: Int = 0
        var targetHighSoundLevel: Int = 0
        var createdAt: Date = Date()
        var updatedAt: Date = Date()
        var feature: List<Float> = emptyList()

        if (ad.description != null){
            description = ad.description!!
        }
        if (ad.targetGender != null){
            targetGender = ad.targetGender!!
        }
        if (ad.targetAge != null){
            targetAge = ad.targetAge!!
        }
        if (ad.targetWeather != null){
            targetWeather = ad.targetWeather!!
        }
        if (ad.targetLowTemp != null){
            targetLowTemp = ad.targetLowTemp!!
        }
        if (ad.targetHighTemp != null){
            targetHighTemp = ad.targetHighTemp!!
        }
        if (ad.targetLowSoundLevel != null){
            targetLowSoundLevel = ad.targetLowSoundLevel!!
        }
        if (ad.targetHighSoundLevel != null){
            targetHighSoundLevel = ad.targetHighSoundLevel!!
        }
        if (ad.createdAt != null){
            createdAt = ad.createdAt!!
        }
        if (ad.updatedAt != null){
            updatedAt = ad.updatedAt!!
        }
        if (ad.feature != null){
            feature = ad.feature!!
        }

        email ?: throw BadRequestResponse("invalid user to create ad") as Throwable
        return userRepository.findByEmail(email).let { user ->
            user ?: throw BadRequestResponse("invalid user to create ad")
            adRepository.create(
                AdWithFeature(
                    id = Slugify().slugify(ad.title),
                    content = ad.content!!,
                    user = user,
                    title = ad.title!!,
                    description = description,
                    targetGender=targetGender,
                    targetAge = targetAge,
                    targetWeather = targetWeather,
                    targetLowTemp = targetLowTemp,
                    targetHighTemp = targetHighTemp,
                    targetLowSoundLevel = targetLowSoundLevel,
                    targetHighSoundLevel = targetHighSoundLevel,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    feature = feature
                ))
                ?: throw InternalServerErrorResponse("Error to create ad.")
        }
    }

    fun delete(email: String?, id: String) {
        email ?: throw BadRequestResponse("invalid user to delete ad")
        val ad = findById(email,id) ?: throw NotFoundResponse()
        val admin = adminRepository.findByEmail(email)
        if (ad.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            adRepository.delete(id)
        else
            throw UnauthorizedResponse("Unauthorized User")
    }

    fun update(email: String?,id: String, ad: AdReq): AdWithFeature? {
        val adOld = findById(email,id) ?: throw NotFoundResponse()
        val admin = email?.let { adminRepository.findByEmail(it) }
        if (adOld.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            return findById(email,id).run {
                adRepository.update(id, ad.copy(id = id))
            }
        else
            throw UnauthorizedResponse("Unauthorized User")
    }

    fun findById(email: String?, id: String): AdWithFeature? {
        var ad =  adRepository.findById(id) ?: throw NotFoundResponse()
        val admin = email?.let { adminRepository.findByEmail(it) }
        if (ad.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            return ad
        else
            throw UnauthorizedResponse("Unauthorized User")
    }

    fun findBy(title: String?, email: String?, targetAge: String?, targetGender: String?,targetWeather: String?): List<AdWithFeature> {
        if(!targetAge.isNullOrBlank() && !enumContains<Age>(targetAge)) throw BadRequestResponse("Invalid age")
        if(!targetGender.isNullOrBlank() && !enumContains<Gender>(targetGender)) throw BadRequestResponse("Invalid gender")
        if(!targetWeather.isNullOrBlank() && !enumContains<Weather>(targetWeather)) throw BadRequestResponse("Invalid weather")
        return adRepository.findByFilters(title,email, targetAge, targetGender, targetWeather)

     }

    }
