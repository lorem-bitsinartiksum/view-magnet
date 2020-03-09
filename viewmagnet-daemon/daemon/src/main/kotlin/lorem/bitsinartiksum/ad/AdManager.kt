package lorem.bitsinartiksum.ad

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lorem.bitsinartiksum.config.Config
import model.*
import topic.Country
import topic.TopicContext
import topic.TopicService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

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

    private inline fun <reified T> parse(field: String, output: String): T? {
        return if (output.startsWith(field)) {
            val found = output.subSequence(output.indexOf(" ") + 1, output.length).toString()
            when (T::class) {
                Long::class -> found.toLong() as T
                Float::class -> found.toFloat() as T
                Int::class -> found.toInt() as T
                String::class -> found.toString() as T
                else -> null
            }
        } else null
    }

    val jackson = jacksonObjectMapper()
    data class weatherInfo(val weather: Weather, val tempC: Float, val windSpeed: Float, val sunrise:Long, val sunset: Long, val timezone: Int, val country: String)

    private fun startWatching() {
        var  weatherInfo = weatherInfo(weather = Weather.UNKNOWN , tempC = 0F, windSpeed = 0F, sunrise = 0, sunset = 0, timezone = 0, country = "country")
        var envRef: BillboardEnvironment

        runPythonScript("weather-info\\weather.py") {
            val json = jackson.readTree(it)
            if(!json.isEmpty){
                val weather = Weather.valueOf(json.path("weather").path(0).get("main").asText("UNKNOWN").toUpperCase())
                val temp = (json.path("main").get("temp").asText("0")).toFloat()
                val wind = (json.path("wind").get("speed").asText("0")).toFloat()
                val sunrise = (json.path("sys").get("sunrise").asText("0")).toLong()
                val sunset = (json.path("sys").get("sunset").asText("0")).toLong()
                val timezone = (json.get("timezone").asText("0")).toInt()
                val country = (json.path("sys").get("country").asText("UNKNOWN")).toString()

                weatherInfo = weatherInfo(weather = weather, tempC = temp, windSpeed = wind, sunrise = sunrise, sunset = sunset, timezone = timezone, country = country)
                print(weatherInfo)
            }
        }

        runPythonScript("sound-pressure-level-meter\\spl_meter.py") {
            println("READ Desibel: $it")
            val sound = it.toFloat()

            if(sound != null && !weatherInfo.weather.equals(Weather.UNKNOWN)){
                envRef = BillboardEnvironment(weather = weatherInfo.weather, tempC = weatherInfo.tempC, windSpeed = weatherInfo.windSpeed, sunrise = weatherInfo.sunrise, sunset = weatherInfo.sunset, timezone = weatherInfo.timezone, country = weatherInfo.country, soundDb = sound)
                println(envRef.toString())
            }
        }

        runPythonScriptWithBatch("age-gender-pred") {
            if (it.startsWith("age-gender : ")) {
                println("READ : $it")
            }
            else if(it.startsWith("object : ")){
                println("READ : $it")
            }
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

    private fun runPythonScriptWithBatch(name: String, handler: (String) -> Unit) {
        val scriptPath = Path.of(System.getProperty("user.dir"), "viewmagnet-daemon", name)
        Executors.newSingleThreadExecutor().execute {
            val process =
                ProcessBuilder("cmd.exe", "/C", "$scriptPath\\build.bat $scriptPath").redirectErrorStream(true).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            process.outputStream.close();

            var line = reader.readLine()
            while (line != null) {
                handler(line)
                line = reader.readLine()
            }
            process.waitFor();
        }
    }
}