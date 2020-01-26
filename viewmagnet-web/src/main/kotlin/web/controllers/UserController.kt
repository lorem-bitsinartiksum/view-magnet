package web.controllers

import io.javalin.Context
import domain.User.UserDTO
import domain.User.service.UserService

const val MAIL_REGEX = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")

class UserController(private val userService: UserService) {
    fun login(ctx: Context) {
        ctx.validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .getOrThrow().user?.also { user ->
            userService.authenticate(user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }

    fun register(ctx: Context) {
        ctx.validatedBody<UserDTO>()
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ !it.user?.password.isNullOrBlank() })
                .check({ !it.user?.username.isNullOrBlank() })
                .getOrThrow().user?.also { user ->
            userService.create(user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }

    fun delete(ctx: Context){
        userService.delete(ctx.attribute("email"))

    }

    fun getCurrent(ctx: Context) {
        userService.getByEmail(ctx.attribute("email")).also { user ->
            ctx.json(UserDTO(user))
        }
    }

    fun update(ctx: Context) {
        val email = ctx.attribute<String>("email")
        ctx.validatedBody<UserDTO>()
                .check({ it.user != null })
                .check({ it.user?.email?.isEmailValid() ?: true })
                .check({ it.user?.username?.isNotBlank() ?: true })
                .check({ it.user?.password?.isNotBlank() ?: true })
                .getOrThrow()
                .user?.also { user ->
            userService.update(email, user).apply {
                ctx.json(UserDTO(this))
            }
        }
    }

    fun String.isEmailValid(): Boolean = !this.isNullOrBlank() && Regex(MAIL_REGEX).matches(this)
}