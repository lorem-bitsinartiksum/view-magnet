package lorem.bitsinartiksum.ad

import lorem.bitsinartiksum.config.Config
import model.Ad
import model.AdChanged
import model.AdPoolChanged
import topic.TopicContext
import topic.TopicService
import java.nio.file.Path
import java.util.concurrent.Executors

class AdManager(private val updateDisplay: (Ad) -> Unit, val cfg: Config) {

    val adList = listOf(
        "https://images.unsplash.com/photo-1582740735409-d0ae8d48976e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80",
        "https://images.unsplash.com/photo-1582999275987-a02e090da23b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1539006749419-f9a3eb2bf3fe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=701&q=80"
    )
    private val adChangedTs = TopicService.createFor(AdChanged::class.java, cfg.id, TopicContext())
    private var rollStartTime = System.currentTimeMillis()

    private var currentAd: Ad = Ad("default", adList.first())
        set(value) {
            val durationMs = System.currentTimeMillis() - rollStartTime
            adChangedTs.publish(AdChanged(field, durationMs, listOf()))
            field = value
            updateDisplay(value)
        }

    private var pool = setOf<String>()


    fun refreshPool(newPool: Set<String>) {
        pool = newPool
        println("REFRESHING POOL $newPool")
    }

    inline fun <reified T> handleCommand(cmd: T) {
        when (T::class.java) {
            AdPoolChanged::class.java -> refreshPool((cmd as AdPoolChanged).newPool)
        }
    }

    fun start() {
        startWatching()
        startScheduler()
    }

    private fun startScheduler() {

        val thread = Thread({
            var i = 0
            while (true) {
                currentAd = Ad("ad$i", adList[i % adList.size])
                i++
                Thread.sleep(2000)
            }
        }, "ad-scheduler")
        thread.start()
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