package utils

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import domain.Admin.Admin
import io.javalin.security.Role
import domain.User.User
import java.util.*

class JwtProvider {

    fun decodeJWT(token: String): DecodedJWT = JWT.require(Cipher.algorithm).build().verify(token)

    fun createJWT(user: User, role: Role): String? =
        JWT.create()
            .withIssuedAt(Date())
            .withSubject(user.email)
            .withClaim("role", role.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000))
            .sign(Cipher.algorithm)

    fun createJWT(admin: Admin, role: Role): String? =
        JWT.create()
            .withIssuedAt(Date())
            .withSubject(admin.email)
            .withClaim("role", role.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000))
            .sign(Cipher.algorithm)
}
