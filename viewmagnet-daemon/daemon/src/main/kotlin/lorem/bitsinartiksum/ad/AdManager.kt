package lorem.bitsinartiksum.ad

import lorem.bitsinartiksum.Config
import model.Ad
import model.AdChanged
import model.AdPoolChanged
import model.Similarity
import topic.TopicContext
import topic.TopicService
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.Executors
import kotlin.concurrent.timer

typealias AdPool = Set<Pair<Ad, Similarity>>
data class AdTrack(val ad: Ad, val similarity: Similarity, var remaining: Duration)

class AdManager(private val updateDisplay: (Ad) -> Unit, val cfg: Config) {

    private val adChangedTs = TopicService.createFor(AdChanged::class.java, cfg.id, TopicContext())
    private var rollStartTime = System.currentTimeMillis()

    var pool: AdPool = setOf(
        Ad(
            "t1",
            "https://images.unsplash.com/photo-1582740735409-d0ae8d48976e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80"
        ) to 0.5f,
        Ad(
            "t2",
            "https://images.unsplash.com/photo-1539006749419-f9a3eb2bf3fe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=701&q=80"
        ) to 0.2f,
        Ad(
            "t2",
            "https://images.unsplash.com/photo-1582999275987-a02e090da23b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
        ) to 0.64f
    )
        private set

    var currentAd: Ad = pool.first().first
        private set(newAd) {
            val durationMs = System.currentTimeMillis() - rollStartTime
            adChangedTs.publish(AdChanged(field, durationMs, listOf()))
            field = newAd
            updateDisplay(newAd)
        }

    private var schedule: List<AdTrack> = listOf(AdTrack(currentAd, 1.0f, Duration.ZERO))
    private var nextAdIdx = 0

    fun refreshPool(newPool: Set<Pair<Ad, Similarity>>) {
        synchronized(schedule) {
            pool = newPool
            nextAdIdx = 0
            val totalSim = pool.fold(0f) { total, (_, sim) -> total + sim }
            schedule = pool.map { (ad, sim) ->
                AdTrack(ad, sim, Duration.ofMillis((cfg.period.toMillis() * sim / totalSim).toLong()))
            }
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
                if (nextAdIdx == 0 && schedule.isEmpty())
                    return@synchronized

                val prevAd = schedule.getOrNull(nextAdIdx - 1)
                if (prevAd != null)
                    prevAd.remaining = prevAd.remaining.minus(cfg.period)
                val nextAd = schedule[nextAdIdx++ % schedule.size]
                currentAd = nextAd.ad
            }
        }
    }

    private fun startWatching() {
        runPythonScript("test.py") {
            println("READ: $it")
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