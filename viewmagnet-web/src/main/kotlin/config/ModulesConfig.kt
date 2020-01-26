package config

import domain.User.repository.UserRepository
import domain.User.service.UserService
import utils.JwtProvider
import web.Router
import web.controllers.UserController
import org.koin.dsl.module.module

object ModulesConfig {
    private val configModule = module {
        single { AppConfig() }
        single { JwtProvider() }
        single { AuthConfig(get()) }
        single { Router(get()) }
    }
    private val userModule = module {
        single { UserController(get()) }
        single { UserService(get(), get()) }
        single { UserRepository() }
    }

    internal val allModules = listOf(
        configModule,
        userModule
    )
}
