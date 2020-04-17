package controllers

import com.mongodb.MongoClient
import config.AppConfig
import io.javalin.Javalin
import io.javalin.util.AdReqDTO
import io.javalin.util.HttpUtil
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
    fun `create db`() {
        val email = "alper@gmail.com"
        val password = "Test"
        http.registerUser(email, password, "alper")
        http.loginAndSetTokenHeader(email, password)

        val adDTO1 = AdReqDTO(
            AdReq(
                title = "Columbia Bugaboot Plus IV",
                description = "https://www.columbia.com.tr/urun/bugaboot-plus-iv-omni-heat-wide-erkek-ayakkabi-33489/siyah",
                content = "https://i.etsystatic.com/6191330/r/il/dc5841/1150868640/il_570xN.1150868640_tig9.jpg",
                feature = listOf(0.85f, 0f, 0f, 0.3f, 0.80f, 0.1f, 1f, 0.05f, 1f, 0.05f, 0.5f)
            )
        )

        val adDTO2 = AdReqDTO(
            AdReq(
                title = "Audi R8",
                description = "https://www.audi.com.tr/tr/web/tr/modeller/r8/r8.html",
                content = "https://i.pinimg.com/originals/5f/5e/51/5f5e5195bb6f5a24a079bd2abdc12993.jpg",
                feature = listOf(0.98f, 0f, 0f, 0.5f, 0.8f, 0.03f, 0.5f, 0.5f, 0.5f, 0.5f, 0.25f)
            )
        )

        val adDTO3 = AdReqDTO(
            AdReq(
                title = "Audi A4",
                description = "https://www.audi.com.tr/tr/web/tr/modeller/a4/a4-saloon-2020.html",
                content = "https://payload.cargocollective.com/1/15/502001/7621361/Audi_Ali_04_2x.jpg",
                feature = listOf(0.5f, 0f, 0f, 0.2f, 0.9f, 0.3f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO4 = AdReqDTO(
            AdReq(
                title = "Volkswagen Beetle",
                description = "https://www.vw.com/models/beetle/section/overview/",
                content = "https://i.pinimg.com/originals/60/fd/74/60fd74c236a6de1684a74e2bc6e0e0e8.jpg",
                feature = listOf(0.05f, 0f, 0f, 0.05f, 0.3f, 0.4f, 0.5f, 0.5f, 0.5f, 0.5f, 0.75f)
            )
        )

        val adDTO5 = AdReqDTO(
            AdReq(
                title = "Skyder Waterproof Gloves",
                description = "https://www.amazon.com/SKYDEER-Waterproof-Windproof-Thinsulate-Insulation/dp/B07RY387FJ",
                content = "https://m.media-amazon.com/images/S/aplus-media/sc/1da74b0c-f005-4a3f-b929-241ae614663e.__CR0,0,970,600_PT0_SX970_V1___.jpg",
                feature = listOf(0.55f, 0f, 1f, 1f, 1f, 1f, 0.80f, 0f, 1f, 0f, 0.5f)
            )
        )

        val adDTO6 = AdReqDTO(
            AdReq(
                title = "Nivea Sun Cream",
                description = "https://www.hepsiburada.com/nivea-sun-koruma-nem-nemlendirici-gunes-losyonu-gkf-50-200-ml-p-SGNIVEA53248",
                content = "https://images.says.com/uploads/story_source/source_image/601867/16ae.jpg",
                feature = listOf(0.5f, 1f, 1f, 1f, 0.95f, 1f, 0f, 1f, 0f, 1f, 0.5f)
            )
        )

        val adDTO7 = AdReqDTO(
            AdReq(
                title = "Walking Stick",
                description = "https://www.aliexpress.com/item/33022839503.html",
                content = "https://ae01.alicdn.com/kf/HTB1xebXXBGE3KVjSZFhq6AkaFXau/Smart-FM-Radio-Old-Man-Woman-Walking-Stick-Lighting-Alarm-Telescopic-Musical-Ultra-Light-Music-Electronic.jpg",
                feature = listOf(0.5f, 0f, 0f, 0f, 0f, 0.9f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO8 = AdReqDTO(
            AdReq(
                title = "Baby Diaper",
                description = "https://www.hepsiburada.com/prima-bebek-bezi-aktif-bebek-6-beden-102-adet-ekstra-large-aylik-firsat-paketi-p-HBV00000F3SLL",
                content = "https://ayb.akinoncdn.com/products/2019/09/30/26982/55814b39-6ef0-4ed8-a3ed-7ebf195c23d0_size780x780_quality60_cropCenter.jpg",
                feature = listOf(0.5f, 1f, 0f, 0f, 0f, 0f, 0f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO9 = AdReqDTO(
            AdReq(
                title = "Playstation 4",
                description = "https://www.hepsiburada.com/sony-playstation-4-slim-500-gb-oyun-konsolu-turkce-menu-p-HBV000002K6XS",
                content = "https://gameranx.com/wp-content/uploads/2019/11/1_HT6HUM7LfYEDcmlwgZA4JQ.jpg",
                feature = listOf(0.65f, 0f, 0.5f, 1f, 0.4f, 0.03f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO10 = AdReqDTO(
            AdReq(
                title = "Nerf Bow",
                description = "https://toystop.com.tr/nerf-rebelle-3lu-ok-yedek-paket",
                content = "https://d3fa68hw0m2vcc.cloudfront.net/fd6/106177793.jpeg",
                feature = listOf(0.53f, 0f, 1f, 0.8f, 0.3f, 0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO11 = AdReqDTO(
            AdReq(
                title = "Iphone  11",
                description = "https://www.hepsiburada.com/iphone-11-pro-64-gb-p-HBV00000NSB61",
                content = "https://www.singtel.com/content/dam/singtel/personal/products-services/mobile/info/iphone11/products/iphone11pro/iphone11pro_18oct2019_mb_01.jpg",
                feature = listOf(0.83f, 0f, 0.2f, 0.90f, 0.95f, 0.3f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO12 = AdReqDTO(
            AdReq(
                title = "Xiaomi Redmi Note 8",
                description = "https://www.hepsiburada.com/xiaomi-redmi-note-8-64-gb-xiaomi-turkiye-garantili-pm-HB00000NBYGG",
                content = "https://netstorage-legit.akamaized.net/images/282a7982871ebd77.jpg?imwidth=900",
                feature = listOf(0.48f, 0f, 0.2f, 0.85f, 0.95f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO13 = AdReqDTO(
            AdReq(
                title = "Samsung Galaxy S3",
                description = "https://www.hepsiburada.com/yenilenmis-samsung-galaxy-s3-16-gb-12-ay-garantili-p-HBV000007HBAP",
                content = "https://4.bp.blogspot.com/-NDzMlthYzes/UUPFOaPh9jI/AAAAAAAAAX8/nSxmItRnmSM/s1600/galaxyAD.jpg",
                feature = listOf(0.1f, 0f, 0.2f, 0.70f, 0.80f, 0.55f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO14 = AdReqDTO(
            AdReq(
                title = "Bosporus Tours",
                description = "https://www.teknemania.com/portfolio/yat-9/",
                content = "https://i.pinimg.com/originals/ba/6e/89/ba6e89d8a5fe125967d3c59e9ef15670.png",
                feature = listOf(0.8f, 0f, 0f, 0.3f, 0.7f, 0.9f, 0.1f, 0.9f, 0.05f, 1f, 0.5f)
            )
        )

        val adDTO15 = AdReqDTO(
            AdReq(
                title = "Rolex",
                description = "https://www.rolex.com/tr",
                content = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/6ac6a0ea-933b-4398-a528-8124713111f7/d57fgjs-72872bd5-3fbe-4739-b223-638bce589390.jpg/v1/fill/w_751,h_1064,q_75,strp/rolex_poster_concept_by_canorousdesign-d57fgjs.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwic3ViIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl0sIm9iaiI6W1t7InBhdGgiOiIvZi82YWM2YTBlYS05MzNiLTQzOTgtYTUyOC04MTI0NzEzMTExZjcvZDU3Zmdqcy03Mjg3MmJkNS0zZmJlLTQ3MzktYjIyMy02MzhiY2U1ODkzOTAuanBnIiwid2lkdGgiOiI8PTc1MSIsImhlaWdodCI6Ijw9MTA2NCJ9XV19.FYmngdTMSkrX6etm6KcQYvU7tFUaQ7A7SmRrAAhexYI",
                feature = listOf(0.97f, 0f, 0f, 0.05f, 0.3f, 0.6f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO16 = AdReqDTO(
            AdReq(
                title = "Casio",
                description = "https://www.hepsiburada.com/casio-f-91w-1dg-digital-erkek-kol-saati-p-SASCK0700141?magaza=%C4%B0svi%C3%A7re%20Saat%C3%A7ilik",
                content = "https://i.pinimg.com/originals/38/d0/0e/38d00ecd3efb2233576f893ceda00c80.jpg",
                feature = listOf(0.1f, 0f, 0.2f, 0.5f, 0.2f, 0.05f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO17 = AdReqDTO(
            AdReq(
                title = "Macbook",
                description = "https://www.apple.com/tr/shop/buy-mac/macbook-pro/13-in%C3%A7",
                content = "https://i.pinimg.com/originals/15/78/8b/15788b8db54c133186d742fb110bd364.jpg",
                feature = listOf(0.85f, 0f, 0.25f, 0.95f, 0.95f, 0.1f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO18 = AdReqDTO(
            AdReq(
                title = "Ipad Pro",
                description = "https://www.apple.com/tr/shop/buy-ipad/ipad-pro",
                content = "https://www.graphis.com/media/LEGACY-FILES/cfe/35_posterannual2012/3285_c04864d1d241ca077a827ec20bf15c07.jpg",
                feature = listOf(0.92f, 0f, 0.3f, 0.90f, 0.4f, 0.1f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO19 = AdReqDTO(
            AdReq(
                title = "Apple Watch",
                description = "https://www.apple.com/tr/shop/buy-watch/apple-watch",
                content = "https://i.pinimg.com/originals/59/ab/66/59ab6611211b929239db2385fb295af6.jpg",
                feature = listOf(0.95f, 0f, 0.1f, 0.80f, 0.5f, 0.2f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO20 = AdReqDTO(
            AdReq(
                title = "Asus X-Series Laptop",
                description = "https://urun.n11.com/dizustu-bilgisayar/asus-x540ua-go139703-i3-7020u-8gb-256ssd-156-dos-nb-P388395565",
                content = "https://mir-s3-cdn-cf.behance.net/project_modules/disp/6ed38219266013.562d77742fd2c.jpg",
                feature = listOf(0.25f, 0f, 0.25f, 0.95f, 0.95f, 0.1f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        var response = http.post<AdReqDTO>("/api/ads", adDTO1)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO2)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO3)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO4)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO5)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO6)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO7)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO8)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO9)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO10)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO11)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO12)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO13)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO14)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO15)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO16)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO17)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO18)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO19)
        assert(response.status == 200)

        response = http.post<AdReqDTO>("/api/ads", adDTO20)
        assert(response.status == 200)
    }

    @Test
    fun `create new ad`() {
        val email = "email_valid@valid_email.com"
        val password = "Test"
        http.registerUser(email, password, "username_Test")
        http.loginAndSetTokenHeader(email, password)

        val adDTO1 = AdReqDTO(
            AdReq(
                title = "Yacht",
                description = "https://images.unsplash.com/photo-1545566239-0b2fb5c50bc6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
                content = "https://images.unsplash.com/photo-1545566239-0b2fb5c50bc6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
                targetGender = listOf(Gender.WOMAN, Gender.MAN),
                targetAge = listOf(Age.ADULT, Age.ELDERLY),
                feature = listOf(0.97f, 0f, 0f, 0.6f, 0.98f, 0.98f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO2 = AdReqDTO(
            AdReq(
                title = "Audi R8",
                description = "https://images.unsplash.com/photo-1541348263662-e068662d82af?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1234&q=80",
                content = "https://images.unsplash.com/photo-1541348263662-e068662d82af?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1234&q=80",
                targetGender = listOf(Gender.WOMAN, Gender.MAN),
                targetAge = listOf(Age.YOUNG, Age.ADULT),
                feature = listOf(0.94f, 0f, 0f, 1f, 1f, 0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )
        // rich, babby, child, teen, adult, elder, rainy, sunny, cold, hot, politics
        val adDTO3 = AdReqDTO(
            AdReq(
                title = "Fiat",
                description = "https://images.unsplash.com/photo-1555598508-f54090d38d59?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
                content = "https://images.unsplash.com/photo-1555598508-f54090d38d59?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
                targetGender = listOf(Gender.WOMAN, Gender.MAN),
                targetAge = listOf(Age.YOUNG, Age.ADULT, Age.ELDERLY),
                feature = listOf(0.3f, 0f, 0f, 0.6f, 0.4f, 0.3f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO4 = AdReqDTO(
            AdReq(
                title = "Laptop",
                description = "https://images.unsplash.com/photo-1527434171365-3d9f55f5fb78?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2550&q=80",
                content = "https://images.unsplash.com/photo-1527434171365-3d9f55f5fb78?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2550&q=80",
                targetGender = listOf(Gender.WOMAN, Gender.MAN),
                targetAge = listOf(Age.CHILD, Age.YOUNG, Age.ADULT, Age.ELDERLY),
                feature = listOf(0.18f, 0f, 0.7f, 0.8f, 0.6f, 0.1f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
            )
        )

        val adDTO5 = AdReqDTO(
            AdReq(
                title = "Beetle",
                description = "https://images.unsplash.com/photo-1566170177760-03f6f5b4a532?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
                content = "https://images.unsplash.com/photo-1566170177760-03f6f5b4a532?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60",
                targetGender = listOf(Gender.MAN),
                targetAge = listOf(Age.YOUNG, Age.ADULT, Age.ELDERLY),
                feature = listOf(0.01f, 0f, 0f, 0.4f, 0.6f, 0.3f, 0.5f, 0.2f, 0.5f, 0.5f, 0.5f)
            )
        )

        var response = http.post<AdReqDTO>("/api/ads", adDTO1)
        response = http.post<AdReqDTO>("/api/ads", adDTO2)
        assert(response.status == 200)
        response = http.post<AdReqDTO>("/api/ads", adDTO3)
        assert(response.status == 200)
        response = http.post<AdReqDTO>("/api/ads", adDTO4)
        assert(response.status == 200)
        response = http.post<AdReqDTO>("/api/ads", adDTO5)
        assert(response.status == 200)
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