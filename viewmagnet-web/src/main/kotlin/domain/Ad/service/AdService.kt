package domain.Ad.service

import com.github.slugify.Slugify
import domain.Ad.Ad
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

    fun create(email: String?, ad: Ad): Ad {
        email ?: throw BadRequestResponse("invalid user to create ad") as Throwable
        return userRepository.findByEmail(email).let { user ->
            user ?: throw BadRequestResponse("invalid user to create ad")
            adRepository.create(
                ad.copy(slug = Slugify().slugify(ad.title), user = user))
                ?: throw InternalServerErrorResponse("Error to create ad.")
        }
    }

    fun delete(email: String?, slug: String) {
        email ?: throw BadRequestResponse("invalid user to delete ad")
        val ad = findBySlug(email,slug) ?: throw NotFoundResponse()
        val admin = adminRepository.findByEmail(email)
        if (ad.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            adRepository.delete(slug)
        else
            throw UnauthorizedResponse("Unauthorized User")

    }

    fun update(email: String?,slug: String, ad: Ad): Ad? {
        val adOld = findBySlug(email,slug) ?: throw NotFoundResponse()
        val admin = email?.let { adminRepository.findByEmail(it) }
        if (adOld.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            return findBySlug(email,slug).run {
                adRepository.update(slug, ad.copy(slug = slug))
            }
        else
            throw UnauthorizedResponse("Unauthorized User")
    }


    fun findBySlug(email: String?, slug: String): Ad? {
        var ad =  adRepository.findBySlug(slug) ?: throw NotFoundResponse()
        val admin = email?.let { adminRepository.findByEmail(it) }
        if (ad.user?.email.equals(email) || !admin?.email.isNullOrBlank())
            return ad
        else
            throw UnauthorizedResponse("Unauthorized User")
    }

    fun findBy(title: String?, email: String?):
            List<Ad> {
        return when {
            !title.isNullOrBlank() -> adRepository.findByTitle(title)
            !email.isNullOrBlank() -> adRepository.findByEmail(email)
            else -> adRepository.findAll()
        }
    }

}
