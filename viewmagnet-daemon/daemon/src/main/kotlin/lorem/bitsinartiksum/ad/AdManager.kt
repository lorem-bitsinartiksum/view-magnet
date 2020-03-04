package lorem.bitsinartiksum.ad

import lorem.bitsinartiksum.config.Config
import model.Ad
import model.AdChanged
import model.AdPoolChanged
import topic.TopicContext
import topic.TopicService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.concurrent.Executors

class AdManager(private val updateDisplay: (Ad) -> Unit, val cfg: Config) {

    private var adList = listOf(
        Ad(
            "t1",
            "https://images.unsplash.com/photo-1582740735409-d0ae8d48976e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80"
        ),
        Ad(
            "t2",
            "https://images.unsplash.com/photo-1539006749419-f9a3eb2bf3fe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=701&q=80"
        ),
        Ad(
            "t2",
            "https://images.unsplash.com/photo-1582999275987-a02e090da23b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
        )
    )
    private val adChangedTs = TopicService.createFor(AdChanged::class.java, cfg.id, TopicContext())
    private var rollStartTime = System.currentTimeMillis()

    var currentAd: Ad = adList.first()
        private set(value) {
            val durationMs = System.currentTimeMillis() - rollStartTime
            adChangedTs.publish(AdChanged(field, durationMs, listOf()))
            field = value
            updateDisplay(value)
        }

    var pool = setOf<Ad>()
        private set


    fun refreshPool(newPool: Set<Ad>) {
        pool = newPool
        adList = newPool.toList()
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
                currentAd = adList[i % adList.size]
                i++
                Thread.sleep(1000)
            }
        }, "ad-scheduler")
        thread.start()
    }


    private fun startWatching() {
        runPythonScript("weather-info\\weather.py") {
            if(it.startsWith("weather:")){
                println("weather: " + it.subSequence(it.indexOf(" ") + 1, it.length) )
            }
            else if(it.startsWith("temp:")){
                println("temp: " + it.subSequence(it.indexOf(" ") + 1, it.length) )
            }
            else if(it.startsWith("wind_spd:")){
                println("wind_spd: " + it.subSequence(it.indexOf(" ") + 1, it.length) )
            }
            else if(it.startsWith("sunrise:")){
                println("sunrise: " + it.subSequence(it.indexOf(" ") + 1, it.length) )
            }
            else if(it.startsWith("sunset:")){
                println("sunset: " + it.subSequence(it.indexOf(" ") + 1, it.length) )
            }
            else if(it.startsWith("timezone:")){
                println("timezone: " + it.subSequence(it.indexOf(" ") + 1, it.length) )
            }
            else if(it.startsWith("country:")){
                println("country: " + it.subSequence(it.indexOf(" ") + 1, it    .length) )
            }
        }
        runPythonScript("sound-pressure-level-meter\\spl_meter.py") {
            println("READ Desibel: $it")
        }

    }

    private fun runPythonScript(name: String, handler: (String) -> Unit) {
        val scriptPath = Path.of(System.getProperty("user.dir"), "viewmagnet-daemon", name)

        Executors.newSingleThreadExecutor().execute {
            val command = "python $scriptPath";
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            var line = reader.readLine()
            while (line != null) {
                handler(line)
                line = reader.readLine()
            }
            process.waitFor();
        }
    }
}