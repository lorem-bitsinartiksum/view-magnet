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

class AdService(private val adRepository: AdRepository,
                private val userRepository: UserRepository,
                private val adminRepository: AdminRepository) {

    inline fun <reified T : Enum<T>> enumContains(name: String?): Boolean {
        return enumValues<T>().any { it.name == name}
    }

    fun create(email: String?, ad: Ad): Ad {
        email ?: throw BadRequestResponse("invalid user to create ad") as Throwable
        return userRepository.findByEmail(email).let { user ->
            user ?: throw BadRequestResponse("invalid user to create ad")
            adRepository.create(
                ad.copy(id = Slugify().slugify(ad.title), user = user))
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

    fun update(email: String?,id: String, ad: Ad): Ad? {
        val adOld = findById(email,id) ?: throw NotFoundResponse()
        val admin = email?.let { adminRepository.findByEmail(it) }
        if (adOld.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            return findById(email,id).run {
                adRepository.update(id, ad.copy(id = id))
            }
        else
            throw UnauthorizedResponse("Unauthorized User")
    }


    fun findById(email: String?, id: String): Ad? {
        var ad =  adRepository.findById(id) ?: throw NotFoundResponse()
        val admin = email?.let { adminRepository.findByEmail(it) }
        if (ad.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            return ad
        else
            throw UnauthorizedResponse("Unauthorized User")
    }

    fun findBy(title: String?, email: String?, targetAge: String?, targetGender: String?,targetWeather: String?): List<Ad> {
        if(!targetAge.isNullOrBlank() && !enumContains<Age>(targetAge)) throw BadRequestResponse("Invalid age")
        if(!targetGender.isNullOrBlank() && !enumContains<Gender>(targetGender)) throw BadRequestResponse("Invalid gender")
        if(!targetWeather.isNullOrBlank() && !enumContains<Weather>(targetWeather)) throw BadRequestResponse("Invalid weather")
        return adRepository.findByFilters(title,email, targetAge, targetGender, targetWeather)

     }

    }
