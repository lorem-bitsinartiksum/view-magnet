package domain.Ad.service

import com.github.slugify.Slugify
import domain.Ad.Ad
import domain.Ad.repository.AdRepository
import io.javalin.InternalServerErrorResponse
import io.javalin.BadRequestResponse

import domain.User.repository.UserRepository
import io.javalin.NotFoundResponse
import io.javalin.UnauthorizedResponse

class AdService(private val adRepository: AdRepository,
                private val userRepository: UserRepository) {

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
        if (ad.user?.email.equals(email))
            adRepository.delete(slug)
        else
            throw UnauthorizedResponse("Unauthorized User")

    }

    fun update(email: String?,slug: String, ad: Ad): Ad? {
        return findBySlug(email,slug).run {
            adRepository.update(slug, ad.copy(slug = slug))
        }
    }

    fun findBySlug(email: String?, slug: String): Ad? {
        var ad =  adRepository.findBySlug(slug) ?: throw NotFoundResponse()
        if (ad.user?.email.equals(email))
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
