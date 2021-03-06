package lorem.bitsinartiksum.manager

import com.google.common.flogger.FluentLogger
import model.Shutdown
import topic.TopicContext
import topic.TopicService
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class BillboardService(private val poolMgr: PoolManager) {

    private val logger = FluentLogger.forEnclosingClass()
    private val shutdownTs = TopicService.createFor(Shutdown::class.java, "SIM_MGR", TopicContext())
    private val spawnedDaemons = ConcurrentHashMap<String, Process>()

    init {
        Runtime.getRuntime().addShutdownHook(
            Thread({ spawnedDaemons.values.forEach(Process::destroy) }, "shutdown-hook")
        )
    }

    fun launchNewBillboard(id: String, pos: List<Float>, interest: List<Float>) {
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
        poolMgr.saveBillboard(id, interest)
    }

    fun updateInterest(id: String, newInterest: List<Float>) {
        logger.atInfo().log("Changing interest of $id to $newInterest")
        poolMgr.changeInterest(id, newInterest)
    }

    fun shutdownBillboard(id: String) {
        logger.atInfo().log("Shutting down billboard: $id")
        val daemon = spawnedDaemons[id]
        daemon?.destroy() ?: shutdownTs.publish(Shutdown(id), TopicContext(individual = id))
    }
}