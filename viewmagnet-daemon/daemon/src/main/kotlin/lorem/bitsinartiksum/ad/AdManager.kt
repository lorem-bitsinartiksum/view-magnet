package lorem.bitsinartiksum.ad

import lorem.bitsinartiksum.Config
import model.Ad
import model.AdChanged
import model.AdPoolChanged
import model.Similarity
import topic.TopicContext
import topic.TopicService
import java.nio.file.Path
import java.util.concurrent.Executors
import kotlin.concurrent.timer

typealias AdPool = Set<Pair<Ad, Similarity>>

class AdManager(private val updateDisplay: (Ad) -> Unit, val cfg: Config) {

    private val adChangedTs = TopicService.createFor(AdChanged::class.java, cfg.id, TopicContext())
    private var rollStartTime = System.currentTimeMillis()

    var pool: AdPool = setOf(
        Ad(
            "t1",
            "https://wallpaperaccess.com/full/173836.jpg"
        ) to 0.1f
    )
        private set

    var currentAd: Ad = pool.first().first
        private set(newAd) {
            val durationMs = System.currentTimeMillis() - rollStartTime
            adChangedTs.publish(AdChanged(field, durationMs, listOf()))
            field = newAd
            updateDisplay(newAd)
        }

    private var schedule = Schedule(pool.toList(), cfg.window)

    fun refreshPool(newPool: Set<Pair<Ad, Similarity>>) {
        synchronized(schedule) {
            pool = newPool
            schedule = Schedule(pool.toList(), cfg.window)
            println("REFRESHING POOL $newPool")
        }
    }

    inline fun <reified T> handleCommand(cmd: T) {
        when (T::class.java) {
            AdPoolChanged::class.java -> {
                refreshPool((cmd as AdPoolChanged).newPool)
            }
        }
    }

    fun start() {
        startWatching()
        timer("ad-scheduler", false, 0, cfg.period.toMillis()) {
            synchronized(schedule) {
                currentAd = schedule.next() ?: currentAd
            }
        }
    }


    private fun startWatching() {
        runPythonScript("test.py") {
//            println("READ: $it")
        }
    }

    private fun runPythonScript(name: String, handler: (String) -> Unit) {

        val scriptPath = Path.of(System.getProperty("user.dir"), "viewmagnet-daemon", name)

        val builder = ProcessBuilder("python3", scriptPath.toString()).redirectErrorStream(true)
        val proc = builder.start()
        // We wont be sending anything.
        proc.outputStream.close()

        Executors.newSingleThreadExecutor().execute {

            val out = proc.inputStream
            val reader = out.bufferedReader()
            while (proc.isAlive) {
                if (out.available() > 0)
                    handler(reader.readLine())
                else {
                    Thread.sleep(10)
                }
            }
        }
    }
}