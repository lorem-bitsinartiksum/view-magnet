package domain.Admin.service

import config.Roles
import domain.Admin.Admin
import domain.Admin.repository.AdminRepository
import io.javalin.BadRequestResponse
import io.javalin.HttpResponseException
import io.javalin.NotFoundResponse
import io.javalin.UnauthorizedResponse
import lorem.bitsinartiksum.manager.CommandIssuer
import model.Ad
import org.eclipse.jetty.http.HttpStatus
import utils.Cipher
import utils.JwtProvider
import java.util.*


class AdminService(private val jwtProvider: JwtProvider, private val adminRepository: AdminRepository) {
    private val base64Encoder = Base64.getEncoder()

    fun create(admin: Admin): Admin {
        adminRepository.findByEmail(admin.email).takeIf { it != null }?.apply {
            throw HttpResponseException(
                HttpStatus.BAD_REQUEST_400,
                "Email already registered!")
        }
        adminRepository.create(admin.copy(password = String(base64Encoder.encode(Cipher.encrypt(admin.password)))))
        return admin.copy(token = generateJwtToken(admin))
    }

    fun delete(email: String?){
        if (email.isNullOrBlank()) throw BadRequestResponse()
        adminRepository.delete(email)
    }

    fun authenticate(admin: Admin): Admin {
        val adminFound = adminRepository.findByEmail(admin.email)
        if (adminFound?.password == String(base64Encoder.encode(Cipher.encrypt(admin.password)))) {
            return adminFound.copy(token = generateJwtToken(adminFound))
        }
        throw UnauthorizedResponse("email or password invalid!")
    }

    fun getByEmail(email: String?): Admin {
        if (email.isNullOrBlank()) throw BadRequestResponse()
        val admin = adminRepository.findByEmail(email)
        admin ?: throw NotFoundResponse()
        return admin.copy(token = generateJwtToken(admin))
    }

    fun update(email: String?, admin: Admin): Admin? {
        email ?: throw HttpResponseException(HttpStatus.NOT_ACCEPTABLE_406, "admin not found to update.")
        return adminRepository.update(email, admin.copy(password = String(base64Encoder.encode(Cipher.encrypt(admin.password)))))
    }

    private fun generateJwtToken(admin: Admin): String? {
        return jwtProvider.createJWT(admin, Roles.ADMIN)
    }

    fun issueShowAdCommand(ad: Ad) {
        val commandIssuer = CommandIssuer()
        commandIssuer.showAd(ad)
    }

    fun issueShutDownCommand(billboardId: String) {
        val commandIssuer = CommandIssuer()
        commandIssuer.shutDown(billboardId)
    }
}
