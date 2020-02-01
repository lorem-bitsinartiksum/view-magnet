package config

import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.Context
import io.javalin.ForbiddenResponse
import io.javalin.Javalin
import io.javalin.security.Role
import utils.JwtProvider

internal enum class Roles : Role {
    ANYONE, AUTHENTICATED, ADMIN
}

private const val headerTokenName = "Authorization"

class AuthConfig(private val jwtProvider: JwtProvider) {
    fun configure(app: Javalin) {
        app.accessManager { handler, ctx, permittedRoles ->
            val jwtToken = getJwtTokenHeader(ctx)
            val userRole = getUserRole(jwtToken) ?: Roles.ANYONE
            permittedRoles.takeIf { !it.contains(userRole) }?.apply { throw ForbiddenResponse() }
            ctx.attribute("email", getEmail(jwtToken))
            handler.handle(ctx)
        }
    }

    private fun getJwtTokenHeader(ctx: Context): DecodedJWT? {
        val tokenHeader = ctx.header(headerTokenName)?.substringAfter("Token")?.trim()
                ?: return null

        return jwtProvider.decodeJWT(tokenHeader)
    }

    private fun getEmail(jwtToken: DecodedJWT?): String? {
        return jwtToken?.subject
    }

    private fun getUserRole(jwtToken: DecodedJWT?): Role? {
        val userRole = jwtToken?.getClaim("role")?.asString() ?: return null
        return Roles.valueOf(userRole)
    }
}