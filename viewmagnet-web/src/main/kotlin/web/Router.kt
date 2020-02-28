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
import web.controllers.AdminController

class Router(private val userController: UserController,
             private val adController: AdController,
             private val adminController: AdminController
) : KoinComponent {

    fun register(app: Javalin) {
        val rolesOptionalAuthenticated = roles(Roles.ADMIN, Roles.AUTHENTICATED)
        app.routes {
            path("users") {
                post(userController::register, roles(Roles.ANYONE))
                post("login", userController::login, roles(Roles.ANYONE))
            }
            path("user") {
                get(userController::getCurrent, roles(Roles.AUTHENTICATED))
                put(userController::update, roles(Roles.AUTHENTICATED))
                delete(userController::delete, roles(Roles.AUTHENTICATED))
                path(":email") {
                    get(userController::getCurrentByEmail, roles(Roles.ADMIN))
                    delete(userController::deleteByEmail, roles(Roles.ADMIN))
                    put(userController::updateByEmail, roles(Roles.ADMIN))
                }
            }
            path("ads") {
                post(adController::create, roles(Roles.AUTHENTICATED))
                get(adController::findBy, rolesOptionalAuthenticated)
                path(":slug") {
                    get(adController::get, rolesOptionalAuthenticated)
                    delete(adController::delete, rolesOptionalAuthenticated)
                    put(adController::update, rolesOptionalAuthenticated)
                }
            }
            path("admins") {
                post(adminController::register, roles(Roles.ANYONE))
                post("login", adminController::login, roles(Roles.ANYONE))
            }
            path("admin") {
                get(adminController::getCurrent, roles(Roles.ADMIN))
                put(adminController::update, roles(Roles.ADMIN))
                delete(adminController::delete, roles(Roles.ADMIN))
                path("manager") {
                    path(":billboard_id") {
                        get(adminController::getBillboardStatus, roles(Roles.ADMIN))
                    }
                    path("command") {
                        path("show-ad/:ad_id") {
                            post(adminController::issueShowAdCommand, roles(Roles.ADMIN))
                        }
                        path("shutdown/:billboard_id") {
                            post(adminController::issueShutDownCommand, roles(Roles.ADMIN))
                        }
                    }
                }
            }
        }
    }
}
