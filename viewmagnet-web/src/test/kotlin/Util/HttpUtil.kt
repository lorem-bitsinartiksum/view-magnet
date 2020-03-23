/*
 * Javalin - https://javalin.io
 * Copyright 2017 David Åse
 * Licensed under Apache 2.0: https://github.com/tipsy/javalin/blob/master/LICENSE
 */

package io.javalin.util

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import domain.Ad.Ad
import domain.Ad.AdDTO
import model.Admin
import model.AdminDTO
import model.User
import model.UserDTO
import io.javalin.core.util.Header
import io.javalin.json.JavalinJson

class HttpUtil(port: Int) {
    private val json = "application/json"
    var headers = mutableMapOf(Header.ACCEPT to json, Header.CONTENT_TYPE to json)

    init {
        Unirest.setObjectMapper(object : ObjectMapper {
            override fun <T> readValue(value: String, valueType: Class<T>): T {
                return JavalinJson.fromJson(value, valueType)
            }

            override fun writeValue(value: Any): String {
                return JavalinJson.toJson(value)
            }
        })
    }

    @JvmField
    val origin: String = "http://localhost:$port"

    inline fun <reified T> post(path: String) =
            Unirest.post(origin + path).headers(headers).asObject(T::class.java)

    inline fun <reified T> post(path: String, body: Any) =
            Unirest.post(origin + path).headers(headers).body(body).asObject(T::class.java)

    inline fun <reified T> get(path: String, params: Map<String, Any>? = null) =
            Unirest.get(origin + path).headers(headers).queryString(params).asObject(T::class.java)

    inline fun <reified T> put(path: String, body: Any) =
            Unirest.put(origin + path).headers(headers).body(body).asObject(T::class.java)

    inline fun <reified T> deleteWithResponseBody(path: String) =
            Unirest.delete(origin + path).headers(headers).asObject(T::class.java)

    fun delete(path: String) =
            Unirest.delete(origin + path).headers(headers).asString()

    fun loginAndSetTokenHeader(email: String, password: String) {
        val userDTO = UserDTO(User(email = email, password = password))
        val response = post<UserDTO>("/api/users/login", userDTO)
        headers["Authorization"] = "Token ${response.body.user?.token}"
    }

    fun loginAndSetTokenHeaderForAdmin(email: String, password: String) {
        val adminDTO = AdminDTO(Admin(email = email, password = password))
        val response = post<AdminDTO>("/api/admins/login", adminDTO)
        headers["Authorization"] = "Token ${response.body.admin?.token}"
    }

    fun deleteToken(){
        headers = mutableMapOf(Header.ACCEPT to json, Header.CONTENT_TYPE to json)
    }

    fun registerUser(email: String, password: String, username: String, phone: String = "123456789", location: String = "Ankara"): UserDTO {
        val userDTO = UserDTO(User(email = email, password = password, username = username, phone = phone, location = location))
        val response = post<UserDTO>("/api/users", userDTO)
        return response.body
    }

    fun registerAdmin(email: String, password: String, username: String, phone: String = "777777777"): AdminDTO {
        val adminDTO = AdminDTO(Admin(email = email, password = password, username = username, phone = phone))
        val response = post<AdminDTO>("/api/admins", adminDTO)
        return response.body
    }

    fun createUser(userEmail: String = "user@valid_user_mail.com", username: String = "user_name_test"): UserDTO {
        val password = "password"
        val user = registerUser(userEmail, password, username)
        loginAndSetTokenHeader(userEmail, password)
        return user
    }

    fun createAdmin(adminEmail: String = "admin@valid_admin_mail.com", username: String = "admin_user_name_test"): AdminDTO {
        val password = "password"
        val admin = registerAdmin(adminEmail, password, username)
        loginAndSetTokenHeader(adminEmail, password)
        return admin
    }

    fun createAd(ad: Ad): HttpResponse<AdDTO> {
        createUser()
        return post<AdDTO>("/api/ads", AdDTO(ad))
    }

    fun createAd(): HttpResponse<AdDTO> {
        return createAd(Ad(title = "volkswagen",
            description = "volkswagen. das auto."))
    }

}