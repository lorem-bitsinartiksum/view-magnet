package config

import domain.User.repository.UserRepository
import domain.User.service.UserService
import web.controllers.UserController

import domain.Ad.repository.AdRepository
import domain.Ad.service.AdService
import web.controllers.AdController

import utils.JwtProvider
import web.Router
import org.koin.dsl.module.module

object ModulesConfig {
    private val configModule = module {
        single { AppConfig() }
        single { JwtProvider() }
        single { AuthConfig(get()) }
        single { Router(get(),get()) }
    }
    private val userModule = module {
        single { UserController(get()) }
        single { UserService(get(), get()) }
        single { UserRepository() }
    }
    private val adModule = module {
        single { AdController(get()) }
        single { AdService(get(), get()) }
        single { AdRepository() }
    }

    internal val allModules = listOf(
        configModule,
        userModule,
        adModule
    )
}
