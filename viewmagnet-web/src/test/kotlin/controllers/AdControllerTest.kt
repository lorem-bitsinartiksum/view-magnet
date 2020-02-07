package controllers

import io.javalin.Javalin
import io.javalin.util.HttpUtil
import config.AppConfig
import domain.Ad.*
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

        val adDTO = AdDTO(Ad(title = "valid_title", description = "valid_description", targetGender = listOf(Gender.FEMALE,Gender.MALE), targetAge = listOf(Age.BABY,Age.CHILD,Age.YOUNG,Age.ADULT,Age.ELDERLY),targetWeather = listOf(Weather.RAINY), targetLowTemp = 10))
        val response = http.post<AdDTO>("/api/ads", adDTO)

        assertEquals(HttpStatus.OK_200,response.status)
        assertEquals(adDTO.ad?.title,response.body.ad?.title)
        assertEquals(adDTO.ad?.description,response.body.ad?.description)
        assertEquals(adDTO.ad?.targetGender,response.body.ad?.targetGender)
        assertEquals(adDTO.ad?.targetAge,response.body.ad?.targetAge)
        assertEquals(adDTO.ad?.targetWeather,response.body.ad?.targetWeather)
        assertEquals(adDTO.ad?.targetLowTemp,response.body.ad?.targetLowTemp)


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

        assertEquals(HttpStatus.OK_200,response2.status)
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
        assertEquals(HttpStatus.OK_200,response.status)


        http.delete("/api/user")

        http.deleteToken()
        http.loginAndSetTokenHeader(email2, password2)

        val slug = response.body.ad?.slug;
        val response2 = http.get<ErrorResponse>("/api/ads/$slug")

        assertEquals(HttpStatus.UNAUTHORIZED_401,response2.status)
    }

    @Test
    fun `delete ad by slug`() {
        val email = "email_valid3@valid_email.com"
        val password = "Test3"
        http.registerUser(email, password, "username_Test3")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title3", description = "valid_description3"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200,response.status)

        val slug = response.body.ad?.slug;
        val response2 = http.delete("/api/ads/$slug")
        assertEquals(HttpStatus.OK_200,response2.status)


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
        assertEquals(HttpStatus.OK_200,response.status)


        http.deleteToken()
        http.loginAndSetTokenHeader(email2, password2)

        val slug = response.body.ad?.slug;
        val response2 = http.delete("/api/ads/$slug")
        assertEquals(HttpStatus.UNAUTHORIZED_401,response2.status)

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

        val response2 = http.get<AdsDTO>("/api/ads?email=$email")

        assertEquals(HttpStatus.OK_200,response.status)
        assertNotNull(response2.body.ads)
        assertEquals(response2.body.ads.size, response2.body.adsCount)
        response2.body.ads.forEach {
            assertEquals(email,it.user?.email)
            assertFalse(it.title.isNullOrBlank())
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

        val slug = response.body.ad?.slug;
        val updatedAdDTO = AdDTO(Ad(title = "updated_valid_title7", description = "updated_valid_description7",targetGender = listOf(Gender.FEMALE,Gender.MALE), targetAge = listOf(Age.CHILD,Age.YOUNG,Age.ADULT),targetWeather = listOf(Weather.SUNNY)))
        val response2 = http.put<AdDTO>("/api/ads/$slug",updatedAdDTO)

        assertEquals(HttpStatus.OK_200,response2.status)
        assertEquals(updatedAdDTO.ad?.title,response2.body.ad?.title)
        assertEquals(updatedAdDTO.ad?.description,response2.body.ad?.description)
        assertEquals(updatedAdDTO.ad?.targetGender,response2.body.ad?.targetGender)
        assertEquals(updatedAdDTO.ad?.targetAge,response2.body.ad?.targetAge)
        assertEquals(updatedAdDTO.ad?.targetWeather,response2.body.ad?.targetWeather)
    }

    @Test
    fun `invalid update ad unauth user`() {
        val email = "email_valid8@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test8")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title8", description = "valid_description8"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)
        val slug = response.body.ad?.slug;

        http.deleteToken()
        val email2 = "email_valid9@valid_email.com"
        val password2 = "Test"
        http.registerUser(email2, password2, "username_Test9")
        http.loginAndSetTokenHeader(email2, password2)

        val updatedAdDTO = AdDTO(Ad(title = "updated_valid_title9", description = "updated_valid_description9"))
        val response2 = http.put<ErrorResponse>("/api/ads/$slug",updatedAdDTO)

        assertEquals(HttpStatus.UNAUTHORIZED_401,response2.status)
    }

    @Test
    fun `get all ads by gender and weather`() {
        val email = "email_valid10@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test10")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdDTO(Ad(title = "valid_title10", description = "valid_description10", targetGender = listOf(Gender.FEMALE,Gender.MALE), targetAge = listOf(Age.BABY,Age.CHILD,Age.YOUNG,Age.ADULT,Age.ELDERLY),targetWeather = listOf(Weather.SNOWY)))
        val adDTO2 = AdDTO(Ad(title = "valid_title11", description = "valid_description11", targetGender = listOf(Gender.MALE), targetAge = listOf(Age.BABY,Age.CHILD,Age.YOUNG,Age.ADULT,Age.ELDERLY),targetWeather = listOf(Weather.STORMY)))
        val adDTO3 = AdDTO(Ad(title = "valid_title12", description = "valid_description12", targetGender = listOf(Gender.MALE), targetAge = listOf(Age.BABY,Age.CHILD,Age.YOUNG,Age.ADULT,Age.ELDERLY),targetWeather = listOf(Weather.SNOWY)))


        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200,response.status)
        assertEquals(adDTO.ad?.title,response.body.ad?.title)
        assertEquals(adDTO.ad?.description,response.body.ad?.description)
        assertEquals(adDTO.ad?.targetGender,response.body.ad?.targetGender)
        assertEquals(adDTO.ad?.targetAge,response.body.ad?.targetAge)
        assertEquals(adDTO.ad?.targetWeather,response.body.ad?.targetWeather)

        val response2 = http.post<AdDTO>("/api/ads", adDTO2)
        assertEquals(HttpStatus.OK_200,response2.status)
        assertEquals(adDTO2.ad?.title,response2.body.ad?.title)
        assertEquals(adDTO2.ad?.description,response2.body.ad?.description)
        assertEquals(adDTO2.ad?.targetGender,response2.body.ad?.targetGender)
        assertEquals(adDTO2.ad?.targetAge,response2.body.ad?.targetAge)
        assertEquals(adDTO2.ad?.targetWeather,response2.body.ad?.targetWeather)

        val response3 = http.post<AdDTO>("/api/ads", adDTO3)
        assertEquals(HttpStatus.OK_200,response3.status)
        assertEquals(adDTO3.ad?.title,response3.body.ad?.title)
        assertEquals(adDTO3.ad?.description,response3.body.ad?.description)
        assertEquals(adDTO3.ad?.targetGender,response3.body.ad?.targetGender)
        assertEquals(adDTO3.ad?.targetAge,response3.body.ad?.targetAge)
        assertEquals(adDTO3.ad?.targetWeather,response3.body.ad?.targetWeather)

        val response4 = http.get<AdsDTO>("/api/ads?targetWeather=SNOWY&targetGender=MALE")
        assertEquals(HttpStatus.OK_200,response4.status)
        assertEquals(adDTO.ad?.title,response4.body.ads?.first().title)
        assertEquals(adDTO.ad?.description,response4.body.ads?.first().description)
        assertEquals(adDTO.ad?.targetGender,response4.body.ads?.first().targetGender)
        assertEquals(adDTO.ad?.targetAge,response4.body.ads?.first().targetAge)
        assertEquals(adDTO.ad?.targetWeather,response4.body.ads?.first().targetWeather)

        assertEquals(adDTO3.ad?.title,response4.body.ads?.last().title)
        assertEquals(adDTO3.ad?.description,response4.body.ads?.last().description)
        assertEquals(adDTO3.ad?.targetGender,response4.body.ads?.last().targetGender)
        assertEquals(adDTO3.ad?.targetAge,response4.body.ads?.last().targetAge)
        assertEquals(adDTO3.ad?.targetWeather,response4.body.ads?.last().targetWeather)

    }

}