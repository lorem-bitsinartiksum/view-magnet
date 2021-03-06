package config

import domain.Ad.repository.AdRepository
import domain.Ad.service.AdService
import domain.Admin.repository.AdminRepository
import domain.Admin.service.AdminService
import domain.Qr.service.QrService
import domain.User.repository.UserRepository
import domain.User.service.UserService
import org.koin.dsl.module.module
import utils.JwtProvider
import web.Router
import web.controllers.AdController
import web.controllers.AdminController
import web.controllers.QrController
import web.controllers.UserController

object ModulesConfig {
    private val configModule = module {
        single { AppConfig() }
        single { JwtProvider() }
        single { AuthConfig(get()) }
        single { Router(get(), get(), get(), get()) }
    }
    private val userModule = module {
        single { UserController(get()) }
        single { UserService(get(), get()) }
        single { UserRepository() }
    }
    private val adModule = module {
        single { AdController(get()) }
        single { AdService(get(), get(), get()) }
        single { AdRepository() }
    }
    private val adminModule = module {
        single { AdminController(get()) }
        single { AdminService(get(), get()) }
        single { AdminRepository() }
    }


    private val QrModule = module {
        single { QrController(get()) }
        single { QrService(get()) }
    }

    internal val allModules = listOf(
        configModule,
        userModule,
        adModule,
        adminModule,
        QrModule
    )
}
