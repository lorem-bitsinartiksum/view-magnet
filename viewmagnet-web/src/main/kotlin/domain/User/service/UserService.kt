package domain.User.service

import io.javalin.BadRequestResponse
import io.javalin.HttpResponseException
import io.javalin.NotFoundResponse
import io.javalin.UnauthorizedResponse
import config.Roles
import model.User
import domain.User.repository.UserRepository
import utils.Cipher
import utils.JwtProvider
import org.eclipse.jetty.http.HttpStatus
import java.util.*

class UserService(private val jwtProvider: JwtProvider, private val userRepository: UserRepository) {
    private val base64Encoder = Base64.getEncoder()

    fun create(user: User): User {
        userRepository.findByEmail(user.email).takeIf { it != null }?.apply {
            throw HttpResponseException(HttpStatus.BAD_REQUEST_400,
                    "Email already registered!")
        }
        userRepository.create(user.copy(password = String(base64Encoder.encode(Cipher.encrypt(user.password)))))
        return user.copy(token = generateJwtToken(user))
    }

    fun delete(email: String?){
        if (email.isNullOrBlank()) throw BadRequestResponse()
        userRepository.delete(email)
    }

    fun authenticate(user: User): User {
        val userFound = userRepository.findByEmail(user.email)
        if (userFound?.password == String(base64Encoder.encode(Cipher.encrypt(user.password)))) {
            return userFound.copy(token = generateJwtToken(userFound))
        }
        throw UnauthorizedResponse("email or password invalid!")
    }

    fun getByEmail(email: String?): User {
        if (email.isNullOrBlank()) throw BadRequestResponse()
        val user = userRepository.findByEmail(email)
        user ?: throw NotFoundResponse()
        return user.copy(token = generateJwtToken(user))
    }

    fun update(email: String?, user: User): User? {
        email ?: throw HttpResponseException(HttpStatus.NOT_ACCEPTABLE_406, "User not found to update.")
        return if(user.password.isNullOrEmpty()){
            userRepository.update(email, user)
        } else{
            userRepository.update(email, user.copy(password = String(base64Encoder.encode(Cipher.encrypt(user.password)))))
        }
    }

    private fun generateJwtToken(user: User): String? {
        return jwtProvider.createJWT(user, Roles.AUTHENTICATED)
    }


}