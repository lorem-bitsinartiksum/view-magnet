package lorem.bitsinartiksum.manager.sim

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.flogger.FluentLogger
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.sse.SseClient
import lorem.bitsinartiksum.manager.HealthChecker
import model.Ad
import model.Mode
import model.ShowAd
import topic.TopicContext
import topic.TopicService
import java.util.*


fun main() {
    Objects.requireNonNull(System.getProperty("daemon")) { "Please set daemon path. -Ddaemon=<jarLocation>" }
    val server = ApiServer()
    server.start()
}

class ApiServer(
    private val billboardService: BillboardService = BillboardService(),
    private val adService: AdService = AdService()
) {
    private val app = Javalin.create { it.enableCorsForAllOrigins() }
    private val logger = FluentLogger.forEnclosingClass()
    private var sessions = setOf<SseClient>()
    private val jack = jacksonObjectMapper()
    private val showAdTs = TopicService.createFor(ShowAd::class.java, "SIM_MGR", TopicContext(mode = Mode.SIM))


    data class BillboardReq(val pos: List<Float>, val interest: List<Float>)
    data class NewAdReq(val color: List<Float> /*[0.2,0.4,0.1]*/)
    data class InterestChangeReq(val interest: List<Float>)
    data class ShowAdReq(val r: Float, val g: Float, val b: Float)

    init {
        registerRoutes()
        pushBillboardStatusUpdates()
    }

    fun start() {
        app.start(System.getProperty("port", "6232").toInt())
    }

    private fun registerRoutes() {

        app.routes {
            path("billboard") {
                post { ctx ->
                    val info = ctx.bodyAsClass(BillboardReq::class.java)
                    billboardService.launchNewBillboard(info.pos.joinToString(","), info.pos, info.interest)
                }
                patch(":id") { ctx ->
                    val id = ctx.pathParam("id")
                    val req = ctx.bodyAsClass(InterestChangeReq::class.java)
                    billboardService.updateInterest(id, req.interest)
                }
                delete(":id") { ctx ->
                    val id = ctx.pathParam("id")
                    billboardService.shutdownBillboard(id)
                }
                post(":id/show-ad") { ctx ->
                    val color = ctx.bodyAsClass(ShowAdReq::class.java)
                    val newAd = Ad(UUID.randomUUID().toString(), content = "${color.r},${color.g},${color.b}")
                    showAdTs.publish(ShowAd(newAd))
                }
                sse("status") { client ->
                    client.onClose {
                        sessions = sessions - client
                        logger.atInfo().log("A client disconnected, total: ${sessions.size}")
                    }
                    sessions = sessions + client
                    logger.atInfo().log("New client connected, total: ${sessions.size}")
                }
            }
            post("ad") { ctx ->
                val newAd = ctx.bodyAsClass(NewAdReq::class.java)
                adService.addNewAd(newAd.color)
            }
        }
    }


    private fun pushBillboardStatusUpdates() {
        val hc = HealthChecker()
        hc.subscribe { status ->
            sessions.forEach { it.sendEvent(jack.writeValueAsString(status)) }
        }
    }
}