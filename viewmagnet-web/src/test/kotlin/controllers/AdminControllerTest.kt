package controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import config.AppConfig
import model.*
import web.ErrorResponse
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdminControllerTest {
    private lateinit var app: Javalin
    private lateinit var http: HttpUtil

    @Before
    fun start() {
        app = AppConfig().setup().start()
        http = HttpUtil(app.port())
    }

    @After
    fun stop() {
        app.stop()
    }

    @Test
    fun `invalid login without pass valid body`() {
        val response = http.post<ErrorResponse>("/api/admins/login",
            AdminDTO()
        )

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422,response.status)
        assertTrue(response.body.errors["body"]!!.contains("can't be empty or invalid"))
    }

    @Test
    fun `success login with email and password`() {
        val email = "success_login@valid_email.com"
        val password = "Test"
        http.registerAdmin(email, password, "admin_name_test")
        val adminDTO = AdminDTO(Admin(email = email, password = password))
        val response = http.post<AdminDTO>("/api/admins/login",  adminDTO)

        assertEquals(HttpStatus.OK_200,response.status)
        assertEquals(adminDTO.admin?.email,response.body.admin?.email)
        assertNotNull(response.body.admin?.token)
    }

    @Test
    fun `success register admin`() {
        val adminDTO = AdminDTO(Admin(email = "success_register@valid_email.com", password = "Test", username =
        "admin_test"))
        val response = http.post<AdminDTO>("/api/admins", adminDTO)

        assertEquals(HttpStatus.OK_200,response.status)
        assertEquals(adminDTO.admin?.username,response.body.admin?.username)
        assertEquals( adminDTO.admin?.password,response.body.admin?.password)
    }

    @Test
    fun `invalid get current admin without token`() {
        val response = http.get<ErrorResponse>("/api/admin")

        assertEquals(HttpStatus.FORBIDDEN_403,response.status)
    }

    @Test
    fun `get current admin by token`() {
        val email = "get_current@valid_email.com"
        val password = "Test"
        http.registerAdmin(email, password, "username_Test")
        http.loginAndSetTokenHeaderForAdmin(email, password)
        val response = http.get<AdminDTO>("/api/admin")

        assertEquals(HttpStatus.OK_200,response.status)
        assertNotNull(response.body.admin?.username)
        assertNotNull(response.body.admin?.password)
        assertNotNull(response.body.admin?.token)
    }

    @Test
    fun `update admin data`() {
        val email = "email_valid@valid_email.com"
        val password = "Test"
        http.registerAdmin(email, password, "username_Test")

        http.loginAndSetTokenHeaderForAdmin("email_valid@valid_email.com", "Test")
        val adminDTO = AdminDTO(Admin(email = "update_admin@update_test.com", password = "Test"))
        val response = http.put<AdminDTO>("/api/admin", adminDTO)

        assertEquals(HttpStatus.OK_200,response.status)
        assertEquals(adminDTO.admin?.email,response.body.admin?.email)
    }

    @Test
    fun `delete admin`() {
        val email = "deleted@valid_email.com"
        val password = "Test"
        http.registerAdmin(email, password, "username_Test")
        http.loginAndSetTokenHeaderForAdmin(email, password)
        val response = http.delete("/api/admin")
        assertEquals(HttpStatus.OK_200,response.status)
    }

    @Test
    fun `update user data`() {
        val user_email = "user@valid_email.com"
        val user_password = "Test"
        http.registerUser(user_email, user_password, "username_Test")

        val admin_email = "admin_email_valid@valid_email.com"
        val admin_password = "Test"
        http.registerAdmin(admin_email, admin_password, "admin_username_Test")

        http.loginAndSetTokenHeaderForAdmin(admin_email, admin_password)

        val userDTO = UserDTO(User(email = "update_user@update_test.com", password = "Test"))
        val response = http.put<UserDTO>("/api/user/$user_email", userDTO)

        assertEquals(HttpStatus.OK_200,response.status)
        assertEquals(userDTO.user?.email,response.body.user?.email)
    }

    @Test
    fun `delete user`() {
        val user_email = "deleted@valid_email.com"
        val user_password = "Test"
        http.registerUser(user_email, user_password, "username_Test")

        val admin_email = "admin_email_valid2@valid_email.com"
        val admin_password = "Test"
        http.registerAdmin(admin_email, admin_password, "admin_username_Test2")
        http.loginAndSetTokenHeaderForAdmin(admin_email, admin_password)

        val response = http.delete("/api/user/$user_email")
        assertEquals(HttpStatus.OK_200,response.status)
    }

    @Test
    fun `delete ad by id`() {
        val email = "email_valid3@valid_email.com"
        val password = "Test3"
        http.registerUser(email, password, "username_Test3")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title3", description = "valid_description3"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200,response.status)
        val id = response.body.ad?.id;

        http.deleteToken()

        val admin_email = "admin_email_valid3@valid_email.com"
        val admin_password = "Test"
        http.registerAdmin(admin_email, admin_password, "admin_username_Test3")
        http.loginAndSetTokenHeaderForAdmin(admin_email, admin_password)

        val response2 = http.delete("/api/ads/$id")
        assertEquals(HttpStatus.OK_200,response2.status)

        http.delete("/api/user")
    }

    @Test
    fun `get all ads by email`() {
        val email = "email_valid6@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test5")
        http.loginAndSetTokenHeader(email, password)

        var adDTO = AdDTO(Ad(title = "valid_title6.1", description = "valid_description6.1"))
        var response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200,response.status)

        adDTO = AdDTO(Ad(title = "valid_title6.2", description = "valid_description6.2"))
        response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200,response.status)

        adDTO = AdDTO(Ad(title = "valid_title6.3", description = "valid_description6.3"))
        response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200,response.status)

        http.deleteToken()
        val admin_email = "admin_email_valid9@valid_email.com"
        val admin_password = "Test"
        http.registerAdmin(admin_email, admin_password, "admin_username_Test9")
        http.loginAndSetTokenHeaderForAdmin(admin_email, admin_password)

        val response2 = http.get<AdsDTO>("/api/ads?email=$email")

        assertEquals(HttpStatus.OK_200,response2.status)
        assertNotNull(response2.body.ads)
        assertEquals(response2.body.ads.size, response2.body.adsCount)
        response2.body.ads.forEach {
            assertEquals(it.user?.email, email)
            Assert.assertFalse(it.title.isNullOrBlank())
        }
    }

    @Test
    fun `update ad`() {
        val email = "email_valid7@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test7")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title7", description = "valid_description7"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)
        val id = response.body.ad?.id;

        http.deleteToken()
        val admin_email = "admin_email_valid4@valid_email.com"
        val admin_password = "Test"
        http.registerAdmin(admin_email, admin_password, "admin_username_Test3")
        http.loginAndSetTokenHeaderForAdmin(admin_email, admin_password)

        val updatedAdDTO = AdDTO(Ad(title = "updated_valid_title7", description = "updated_valid_description7"))
        val response2 = http.put<AdDTO>("/api/ads/$id",updatedAdDTO)

        assertEquals(HttpStatus.OK_200,response2.status)
        assertEquals("updated_valid_title7",response2.body.ad?.title)
        assertEquals( "updated_valid_description7",response2.body.ad?.description)
    }

}