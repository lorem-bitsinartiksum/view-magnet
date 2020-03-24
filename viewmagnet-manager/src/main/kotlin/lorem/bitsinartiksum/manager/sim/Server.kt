package lorem.bitsinartiksum.manager.sim

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.flogger.FluentLogger
import io.javalin.Javalin
import io.javalin.http.sse.SseClient
import lorem.bitsinartiksum.manager.HealthChecker

fun main() {

}

class ApiServer {
    val app = Javalin.create().start(6232)
    private val logger = FluentLogger.forEnclosingClass()
    private var sessions = setOf<SseClient>()
    private val jack = jacksonObjectMapper()

    data class BillboardReq(val pos: List<Float>, val interest: List<Float>)

    init {
        registerRoutes()
        pushBillboardStatusUpdates()
    }

    fun pushBillboardStatusUpdates() {
        val hc = HealthChecker()
        hc.subscribe { status ->
            sessions.forEach { it.sendEvent(jack.writeValueAsString(status)) }
        }
    }

    fun registerRoutes() {
        app.post("newbillboard") { ctx ->
            val (pos, interest) = ctx.bodyAsClass(BillboardReq::class.java)
            logger.atInfo().log("Launching new daemon @$pos with interest: $interest")
        }
        app.sse("status") { client ->

            client.onClose {
                logger.atInfo().log("A client disconnected, total: ${sessions.size}")
                sessions = sessions - client
            }

            logger.atInfo().log("New client connected, total: ${sessions.size}")
            sessions = sessions + client
        }
    }

}