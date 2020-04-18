package lorem.bitsinartiksum.manager

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.flogger.FluentLogger
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.sse.SseClient
import model.Mode
import model.ShowAd
import topic.TopicContext
import topic.TopicService


class ApiServer(
    private val billboardService: BillboardService
) {
    private val app = Javalin.create { it.enableCorsForAllOrigins() }
    private val logger = FluentLogger.forEnclosingClass()
    private var sessions = setOf<SseClient>()
    private val jack = jacksonObjectMapper()
    private val showAdTs = TopicService.createFor(ShowAd::class.java, "SIM_MGR", TopicContext(mode = Mode.REAL))

    data class BillboardReq(val pos: List<Float>, val interest: List<Float>)
    data class InterestChangeReq(val interest: List<Float>)

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
                    billboardService.launchNewBillboard(info.pos.joinToString(":"), info.pos, info.interest)
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
                    val newAd = ctx.bodyAsClass(ShowAd::class.java)
                    showAdTs.publish(newAd, TopicContext(individual = ctx.pathParam("id")))
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
        }
    }


    private fun pushBillboardStatusUpdates() {
        val hc = HealthChecker()
        hc.subscribe { bbId, status ->
            val info = jack.valueToTree<ObjectNode>(status)
            info.put("billboardId", bbId)
            sessions.forEach { it.sendEvent(info.toString()) }
        }
    }
}