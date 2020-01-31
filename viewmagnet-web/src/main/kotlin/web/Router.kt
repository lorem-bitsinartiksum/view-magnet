package web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.javalin.security.SecurityUtil.roles
import config.Roles
import web.controllers.UserController
import org.koin.standalone.KoinComponent
import web.controllers.AdController

class Router(private val userController: UserController,
             private val adController: AdController
) : KoinComponent {

    fun register(app: Javalin) {
        val rolesOptionalAuthenticated = roles(Roles.ANYONE, Roles.AUTHENTICATED)
        app.routes {
            path("users") {
                post(userController::register, roles(Roles.ANYONE))
                post("login", userController::login, roles(Roles.ANYONE))
            }
            path("user") {
                get(userController::getCurrent, roles(Roles.AUTHENTICATED))
                put(userController::update, roles(Roles.AUTHENTICATED))
                delete(userController::delete, roles(Roles.AUTHENTICATED))
            }
            path("ads") {
                post(adController::create, roles(Roles.AUTHENTICATED))
                get(adController::findBy, rolesOptionalAuthenticated)
                path(":slug") {
                    get(adController::get, roles(Roles.AUTHENTICATED))
                    delete(adController::delete, roles(Roles.AUTHENTICATED))
                    put(adController::update, roles(Roles.AUTHENTICATED))
                }
            }
        }
    }
}
