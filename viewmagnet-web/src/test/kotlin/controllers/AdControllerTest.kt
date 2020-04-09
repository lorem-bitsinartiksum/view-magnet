package controllers

import com.mongodb.MongoClient
import io.javalin.Javalin
import io.javalin.util.HttpUtil
import config.AppConfig
import io.javalin.util.AdReqDTO
import model.*
import org.eclipse.jetty.http.HttpStatus
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import web.ErrorResponse

data class AdReqDTO(val ad: AdReq?)
data class AdDTO(val ad: AdWithFeature?)
data class AdsDTO(val ads: List<AdWithFeature>, val adsCount: Int)

class AdControllerTest {
    private lateinit var app: Javalin
    private lateinit var http: HttpUtil

    @Before
    fun start() {
        val mongo = MongoClient()
        mongo.dropDatabase("viewmagnet-REAL")
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

        val adDTO = AdReqDTO(
            AdReq(
                title = "valid_title",
                content = "valid_content",
                targetGender = listOf(Gender.WOMAN, Gender.MAN),
                targetAge = listOf(Age.BABY, Age.CHILD, Age.YOUNG, Age.ADULT, Age.ELDERLY),
                targetWeather = listOf(Weather.RAIN),
                targetLowTemp = 10,
                feature = listOf(0.5f, 0.6f, 0.4f)
            )
        )
        val response = http.post<AdReqDTO>("/api/ads", adDTO)

        assertEquals(HttpStatus.OK_200, response.status)
        assertEquals(adDTO.ad?.title, response.body.ad?.title)
        assertEquals(adDTO.ad?.content, response.body.ad?.content)
        assertEquals(adDTO.ad?.targetGender, response.body.ad?.targetGender)
        assertEquals(adDTO.ad?.targetAge, response.body.ad?.targetAge)
        assertEquals(adDTO.ad?.targetWeather, response.body.ad?.targetWeather)
        assertEquals(adDTO.ad?.targetLowTemp, response.body.ad?.targetLowTemp)

    }

    @Test
    fun `get ad by id`() {
        val email = "email_valid99@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdReqDTO(AdReq(title = "valid_title55", content = "valid_content55"))
        val response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)

        val id = response.body.ad?.id;
        val response2 = http.get<AdReqDTO>("/api/ads/$id")

        assertEquals(HttpStatus.OK_200, response2.status)
        assertFalse(response2.body.ad?.title.isNullOrBlank())
        assertNotNull(response2.body.ad?.content)

        http.delete("/api/user")
        http.delete("/api/ads/$id")
    }

    @Test
    fun `invalid get ad by id with unauth user`() {
        val email = "email_valid999@valid_email.com"
        val password = "Test"
        val email2 = "email_valid9999@valid_email.com"
        val password2 = "Test2"

        http.registerUser(email2, password2, "username_Test2")
        http.registerUser(email, password, "username_Test")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdReqDTO(AdReq(title = "valid_title111", content = "valid_content111"))
        val response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200, response.status)


        http.delete("/api/user")

        http.deleteToken()
        http.loginAndSetTokenHeader(email2, password2)

        val id = response.body.ad?.id;
        val response2 = http.get<ErrorResponse>("/api/ads/$id")

        assertEquals(HttpStatus.UNAUTHORIZED_401, response2.status)
    }

    @Test
    fun `delete ad by id`() {
        val email = "email_valid3@valid_email.com"
        val password = "Test3"
        http.registerUser(email, password, "username_Test3")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdReqDTO(AdReq(title = "valid_title3", content = "valid_content3"))
        val response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200, response.status)

        val id = response.body.ad?.id;
        val response2 = http.delete("/api/ads/$id")
        assertEquals(HttpStatus.OK_200, response2.status)


        http.delete("/api/user")
    }

    @Test
    fun `invalid delete ad by id with unauth user`() {
        val email = "email_valid4@valid_email.com"
        val password = "Test"
        val email2 = "email_valid5@valid_email.com"
        val password2 = "Test5"
        http.registerUser(email, password, "username_Test4")
        http.registerUser(email2, password2, "username_Test5")

        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdReqDTO(AdReq(title = "valid_title4", content = "valid_content4"))
        val response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200, response.status)


        http.deleteToken()
        http.loginAndSetTokenHeader(email2, password2)

        val id = response.body.ad?.id;
        val response2 = http.delete("/api/ads/$id")
        assertEquals(HttpStatus.UNAUTHORIZED_401, response2.status)

        http.delete("/api/user")
    }

    @Test
    fun `get all ads by email`() {
        val email = "email_valid123@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test5")
        http.loginAndSetTokenHeader(email, password)

        var adDTO = AdReqDTO(AdReq(title = "valid_title6.1", content = "valid_content6.1"))
        var response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200, response.status)

        adDTO = AdReqDTO(AdReq(title = "valid_title6.2", content = "valid_content6.2"))
        response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200, response.status)

        adDTO = AdReqDTO(AdReq(title = "valid_title6.3", content = "valid_content6.3"))
        response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200, response.status)

        val response2 = http.get<AdsDTO>("/api/ads?email=$email")

        assertEquals(HttpStatus.OK_200, response.status)
        assertNotNull(response2.body.ads)
        assertEquals(response2.body.ads.size, response2.body.adsCount)
        response2.body.ads.forEach {
            assertEquals(email, it.user?.email)
            assertFalse(it.title.isNullOrBlank())
        }
    }

    @Test
    fun `update ad`() {
        val email = "email_valid7@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test7")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdReqDTO(AdReq(title = "valid_title7", content = "valid_content7"))
        val response = http.post<AdDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)

        val id = response.body.ad?.id;
        val updatedAdDTO = AdReqDTO(
            AdReq(
                title = "updated_valid_title7",
                content = "updated_valid_content7",
                targetGender = listOf(Gender.WOMAN, Gender.MAN),
                targetAge = listOf(Age.CHILD, Age.YOUNG, Age.ADULT),
                targetWeather = listOf(Weather.CLEAR)
            )
        )
        val response2 = http.put<AdReqDTO>("/api/ads/$id", updatedAdDTO)

        assertEquals(HttpStatus.OK_200, response2.status)
        assertEquals(updatedAdDTO.ad?.title, response2.body.ad?.title)
        assertEquals(updatedAdDTO.ad?.content, response2.body.ad?.content)
        assertEquals(updatedAdDTO.ad?.targetGender, response2.body.ad?.targetGender)
        assertEquals(updatedAdDTO.ad?.targetAge, response2.body.ad?.targetAge)
        assertEquals(updatedAdDTO.ad?.targetWeather, response2.body.ad?.targetWeather)
    }

    @Test
    fun `invalid update ad unauth user`() {
        val email = "email_valid899@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test88")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdReqDTO(AdReq(title = "valid_title88", content = "valid_content88"))
        val response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(response.status, HttpStatus.OK_200)
        val id = response.body.ad?.id;

        http.deleteToken()
        val email2 = "email_valid77@valid_email.com"
        val password2 = "Test"
        http.registerUser(email2, password2, "username_Test77")
        http.loginAndSetTokenHeader(email2, password2)

        val updatedAdDTO = AdReqDTO(AdReq(title = "updated_valid_title77", content = "updated_valid_content77"))
        val response2 = http.put<ErrorResponse>("/api/ads/$id", updatedAdDTO)

        assertEquals(HttpStatus.UNAUTHORIZED_401, response2.status)
    }

    @Test
    fun `get all ads by gender and weather`() {
        val email = "email_valid100@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test100")
        http.loginAndSetTokenHeader(email, password)

        val adDTO = AdReqDTO(
            AdReq(
                title = "valid_title1000",
                content = "valid_content1000",
                targetGender = listOf(Gender.WOMAN, Gender.MAN),
                targetAge = listOf(Age.BABY, Age.CHILD, Age.YOUNG, Age.ADULT, Age.ELDERLY),
                targetWeather = listOf(Weather.SNOW)
            )
        )
        val adDTO2 = AdReqDTO(
            AdReq(
                title = "valid_title1100",
                content = "valid_content1100",
                targetGender = listOf(Gender.MAN),
                targetAge = listOf(Age.BABY, Age.CHILD, Age.YOUNG, Age.ADULT, Age.ELDERLY),
                targetWeather = listOf(Weather.THUNDERSTORM)
            )
        )
        val adDTO3 = AdReqDTO(
            AdReq(
                title = "valid_title1200",
                content = "valid_content1200",
                targetGender = listOf(Gender.MAN),
                targetAge = listOf(Age.BABY, Age.CHILD, Age.YOUNG, Age.ADULT, Age.ELDERLY),
                targetWeather = listOf(Weather.SNOW)
            )
        )


        val response = http.post<AdReqDTO>("/api/ads", adDTO)
        assertEquals(HttpStatus.OK_200, response.status)
        assertEquals(adDTO.ad?.title, response.body.ad?.title)
        assertEquals(adDTO.ad?.content, response.body.ad?.content)
        assertEquals(adDTO.ad?.targetGender, response.body.ad?.targetGender)
        assertEquals(adDTO.ad?.targetAge, response.body.ad?.targetAge)
        assertEquals(adDTO.ad?.targetWeather, response.body.ad?.targetWeather)

        val response2 = http.post<AdReqDTO>("/api/ads", adDTO2)
        assertEquals(HttpStatus.OK_200, response2.status)
        assertEquals(adDTO2.ad?.title, response2.body.ad?.title)
        assertEquals(adDTO2.ad?.content, response2.body.ad?.content)
        assertEquals(adDTO2.ad?.targetGender, response2.body.ad?.targetGender)
        assertEquals(adDTO2.ad?.targetAge, response2.body.ad?.targetAge)
        assertEquals(adDTO2.ad?.targetWeather, response2.body.ad?.targetWeather)

        val response3 = http.post<AdReqDTO>("/api/ads", adDTO3)
        assertEquals(HttpStatus.OK_200, response3.status)
        assertEquals(adDTO3.ad?.title, response3.body.ad?.title)
        assertEquals(adDTO3.ad?.content, response3.body.ad?.content)
        assertEquals(adDTO3.ad?.targetGender, response3.body.ad?.targetGender)
        assertEquals(adDTO3.ad?.targetAge, response3.body.ad?.targetAge)
        assertEquals(adDTO3.ad?.targetWeather, response3.body.ad?.targetWeather)

        val response4 = http.get<AdsDTO>("/api/ads?targetWeather=SNOW&targetGender=MAN")
        assertEquals(HttpStatus.OK_200, response4.status)
        assertEquals(adDTO.ad?.title, response4.body.ads?.first().title)
        assertEquals(adDTO.ad?.content, response4.body.ads?.first().content)
        assertEquals(adDTO.ad?.targetGender, response4.body.ads?.first().targetGender)
        assertEquals(adDTO.ad?.targetAge, response4.body.ads?.first().targetAge)
        assertEquals(adDTO.ad?.targetWeather, response4.body.ads?.first().targetWeather)

        assertEquals(adDTO3.ad?.title, response4.body.ads?.last().title)
        assertEquals(adDTO3.ad?.content, response4.body.ads?.last().content)
        assertEquals(adDTO3.ad?.targetGender, response4.body.ads?.last().targetGender)
        assertEquals(adDTO3.ad?.targetAge, response4.body.ads?.last().targetAge)
        assertEquals(adDTO3.ad?.targetWeather, response4.body.ads?.last().targetWeather)

    }

}