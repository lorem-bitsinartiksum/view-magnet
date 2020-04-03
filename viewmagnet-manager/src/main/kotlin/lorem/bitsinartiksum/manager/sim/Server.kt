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
import model.Shutdown
import topic.TopicContext
import topic.TopicService
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap


fun main() {
    Objects.requireNonNull(System.getProperty("daemon")) { "Please set daemon path. -Ddaemon=<jarLocation>" }
    val server = ApiServer()
    server.start()
}

class ApiServer {
    private val app = Javalin.create { it.enableCorsForAllOrigins() }
    private val logger = FluentLogger.forEnclosingClass()
    private var sessions = setOf<SseClient>()
    private val jack = jacksonObjectMapper()
    private val spawnedDaemons = ConcurrentHashMap<String, Process>()
    private val showAdTs = TopicService.createFor(ShowAd::class.java, "SIM_MGR", TopicContext(mode = Mode.SIM))
    private val shuwdownTs = TopicService.createFor(Shutdown::class.java, "SIM_MGR", TopicContext(mode = Mode.SIM))

    data class BillboardReq(val pos: List<Float>, val interest: List<Float>)
    data class InterestChangeReq(val interest: List<Float>)
    data class ShowAdReq(val r: Float, val g: Float, val b: Float)

    init {
        registerRoutes()
        pushBillboardStatusUpdates()

        Runtime.getRuntime().addShutdownHook(
            Thread({ spawnedDaemons.values.forEach(Process::destroy) }, "shutdown-hook")
        )
    }

    fun start() {
        app.start(System.getProperty("port", "6232").toInt())
    }


    private fun registerRoutes() {

        app.routes {
            path("billboard") {
                post { ctx ->
                    val info = ctx.bodyAsClass(BillboardReq::class.java)
                    launchNewBillboard(info.pos.joinToString(":"), info.pos, info.interest)
                }
                patch(":id") { ctx ->
                    val id = ctx.pathParam("id")
                    val req = ctx.bodyAsClass(InterestChangeReq::class.java)
                    updateInterest(id, req.interest)
                }
                delete(":id") { ctx ->
                    val id = ctx.pathParam("id")
                    shutdownBillboard(id)
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
        }
    }


    private fun pushBillboardStatusUpdates() {
        val hc = HealthChecker()
        hc.subscribe { status ->
            sessions.forEach { it.sendEvent(jack.writeValueAsString(status)) }
        }
    }

    private fun launchNewBillboard(id: String, pos: List<Float>, interest: List<Float>) {
        logger.atInfo().log("Launching new daemon $id @${pos} with interest: ${interest}")

        val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"
        val daemonJarLoc = System.getProperty("daemon")
        val pBuilder = ProcessBuilder(
            javaBin,
            "-Did=$id",
            "-Dperiod=5",
            "-Dinterest=${interest.joinToString(", ")}",
            "-jar",
            daemonJarLoc
        )

        Thread({
            val process = pBuilder.start()
            spawnedDaemons[id] = process
            CompletableFuture.supplyAsync {
                BufferedReader(InputStreamReader(process.inputStream, StandardCharsets.UTF_8)).useLines {
                    it.forEach { logger.atInfo().log("BB#$id $it") }
                }
            }

            CompletableFuture.supplyAsync {
                BufferedReader(InputStreamReader(process.errorStream, StandardCharsets.UTF_8)).useLines {
                    it.forEach { logger.atSevere().log("BB#$id $it") }
                }
            }

            val status = process.waitFor()
            logger.atWarning().log("BB#$id exited with $status")
        }, "BILLBOARD#$id").start()
    }

    private fun updateInterest(id: String, newInterest: List<Float>) {
        logger.atInfo().log("Changing interest of $id to $newInterest")
    }

    private fun shutdownBillboard(id: String) {
        logger.atInfo().log("Shutting down billboard: $id")
        val daemon = spawnedDaemons[id]
        daemon?.destroy() ?: shuwdownTs.publish(Shutdown(id), TopicContext(individual = id))
    }

}