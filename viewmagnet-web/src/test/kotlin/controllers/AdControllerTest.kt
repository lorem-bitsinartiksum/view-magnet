package controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import config.AppConfig
import domain.Ad.Ad
import domain.Ad.AdDTO
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import web.ErrorResponse

class AdControllerTest{
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
    fun `create new ad`() {
        val email = "email_valid@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title", description = "valid_description"))
        val response = http.post<AdDTO>("/api/ads", adDTO)

        assertEquals(response.status, HttpStatus.OK_200)
        assertEquals(response.body.ad?.title, adDTO.ad?.title)
        assertEquals(response.body.ad?.description, adDTO.ad?.description)

        http.delete("/api/user")
        val slug = response.body.ad?.slug;
        http.delete("/api/ads/$slug")

    }

    @Test
    fun `get ad by slug`() {
        val email = "email_valid@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title", description = "valid_description"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)

        val slug = response.body.ad?.slug;
        val response2 = http.get<AdDTO>("/api/ads/$slug")

        assertEquals(response2.status, HttpStatus.OK_200)
        assertFalse(response2.body.ad?.title.isNullOrBlank())
        assertNotNull(response2.body.ad?.description)

        http.delete("/api/user")
        http.delete("/api/ads/$slug")
    }

    @Test
    fun `invalid get ad by slug with unauth user`() {
        val email = "email_valid@valid_email.com"
        val password = "Test"
        val email2 = "email_valid2@valid_email.com"
        val password2 = "Test2"

        http.registerUser(email2, password2, "username_Test2")
        http.registerUser(email, password, "username_Test")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title", description = "valid_description"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)

        http.delete("/api/user")

        http.deleteToken()
        http.loginAndSetTokenHeader(email2, password2)

        val slug = response.body.ad?.slug;
        val response2 = http.get<ErrorResponse>("/api/ads/$slug")

        assertEquals(response2.status, HttpStatus.UNAUTHORIZED_401)
    }

    @Test
    fun `delete ad by slug`() {
        val email = "email_valid3@valid_email.com"
        val password = "Test3"
        http.registerUser(email, password, "username_Test3")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title3", description = "valid_description3"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)

        val slug = response.body.ad?.slug;
        val response2 = http.delete("/api/ads/$slug")
        assertEquals(response2.status, HttpStatus.OK_200)

        http.delete("/api/user")
    }

    @Test
    fun `invalid delete ad by slug with unauth user`() {
        val email = "email_valid4@valid_email.com"
        val password = "Test"
        val email2 = "email_valid5@valid_email.com"
        val password2 = "Test5"
        http.registerUser(email, password, "username_Test4")
        http.registerUser(email2, password2, "username_Test5")

        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title4", description = "valid_description4"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)

        http.deleteToken()
        http.loginAndSetTokenHeader(email2, password2)

        val slug = response.body.ad?.slug;
        val response2 = http.delete("/api/ads/$slug")
        assertEquals(response2.status, HttpStatus.UNAUTHORIZED_401)

        http.delete("/api/user")
    }
}