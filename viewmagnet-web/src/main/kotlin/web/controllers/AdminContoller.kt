package web.controllers

import domain.Admin.service.AdminService
import io.javalin.Context
import model.AdminDTO
import model.BillboardStatus

data class BillboardStatusDTO(val billboardStatus: BillboardStatus)

class AdminController(private val adminService: AdminService) {
    fun login(ctx: Context) {
        ctx.validatedBody<AdminDTO>()
            .check({ it.admin?.email?.isEmailValid() ?: true })
            .check({ !it.admin?.password.isNullOrBlank() })
            .getOrThrow().admin?.also { admin ->
            adminService.authenticate(admin).apply {
                ctx.json(AdminDTO(this))
            }
        }
    }

    fun register(ctx: Context) {
        ctx.validatedBody<AdminDTO>()
            .check({ it.admin?.email?.isEmailValid() ?: true })
            .check({ !it.admin?.password.isNullOrBlank() })
            .check({ !it.admin?.username.isNullOrBlank() })
            .getOrThrow().admin?.also { admin ->
            adminService.create(admin).apply {
                ctx.json(AdminDTO(this))
            }
        }
    }

    fun delete(ctx: Context){
        adminService.delete(ctx.attribute("email"))
    }

    fun getCurrent(ctx: Context) {
        adminService.getByEmail(ctx.attribute("email")).also { admin ->
            ctx.json(AdminDTO(admin))
        }
    }

    fun update(ctx: Context) {
        val email = ctx.attribute<String>("email")
        ctx.validatedBody<AdminDTO>()
            .check({ it.admin != null })
            .check({ it.admin?.email?.isEmailValid() ?: true })
            .getOrThrow()
            .admin?.also { admin ->
            adminService.update(email, admin).apply {
                ctx.json(AdminDTO(this))
            }
        }
    }

    fun String.isEmailValid(): Boolean = !this.isNullOrBlank() && Regex(MAIL_REGEX).matches(this)
}