package config

import domain.User.repository.UserRepository
import domain.User.service.UserService
import web.controllers.UserController

import domain.Ad.repository.AdRepository
import domain.Ad.service.AdService
import domain.Admin.repository.AdminRepository
import domain.Admin.service.AdminService
import web.controllers.AdController

import utils.JwtProvider
import web.Router
import org.koin.dsl.module.module
import web.controllers.AdminController

object ModulesConfig {
    private val configModule = module {
        single { AppConfig() }
        single { JwtProvider() }
        single { AuthConfig(get()) }
        single { Router(get(),get(),get()) }
    }
    private val userModule = module {
        single { UserController(get()) }
        single { UserService(get(), get()) }
        single { UserRepository() }
    }
    private val adModule = module {
        single { AdController(get()) }
        single { AdService(get(), get(),get()) }
        single { AdRepository() }
    }
    private val adminModule = module {
        single { AdminController(get()) }
        single { AdminService(get(), get()) }
        single { AdminRepository() }
    }

    internal val allModules = listOf(
        configModule,
        userModule,
        adModule,
        adminModule
    )
}
