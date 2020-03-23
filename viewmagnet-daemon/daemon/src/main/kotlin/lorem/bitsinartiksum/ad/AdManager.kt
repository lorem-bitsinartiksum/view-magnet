package lorem.bitsinartiksum.ad

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.flogger.FluentLogger
import lorem.bitsinartiksum.Config
import model.*
import topic.TopicContext
import topic.TopicService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.Executors
import kotlin.concurrent.timer

typealias AdPool = Set<Pair<Ad, Similarity>>

data class AdTrack(val ad: Ad, val similarity: Similarity, var remaining: Duration)
data class weatherInfo(val weather: Weather, val tempC: Float, val windSpeed: Float, val sunrise:Long, val sunset: Long, val timezone: Int, val country: String)

class AdManager(private val updateDisplay: (Ad) -> Unit, val cfg: Config) {
    private val logger = FluentLogger.forEnclosingClass()
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

    private var schedule: MutableList<AdTrack> = mutableListOf(AdTrack(currentAd, 1.0f, Duration.ZERO))
    private var nextAdIdx = 0

    fun refreshPool(newPool: Set<Pair<Ad, Similarity>>) {
        synchronized(schedule) {
            pool = newPool
            nextAdIdx = 0
            val totalSim = pool.fold(0f) { total, (_, sim) -> total + sim }
            schedule = pool.map { (ad, sim) ->
                AdTrack(ad, sim, Duration.ofMillis((cfg.period.toMillis() * sim / totalSim).toLong()))
            }.toMutableList()
            logger.atInfo().log("Refreshing Pool $newPool")
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

                if (prevAd != null) {
                    prevAd.remaining = prevAd.remaining.minus(cfg.period)
                    if (prevAd.remaining.isZero || prevAd.remaining.isNegative) {
                        schedule.removeAt(nextAdIdx - 1)
                        nextAdIdx--
                    }
                }

                if (schedule.isNotEmpty()) {
                    val nextAd = schedule[nextAdIdx++ % schedule.size]
                    currentAd = nextAd.ad
                }
            }
        }
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
                if(it.contains("dog", ignoreCase=true)){
                    println("DOG DOG DOG")
                }
                else if(it.contains("person", ignoreCase=true)){
                    println("PERSON PERSON PERSON")
                }
                else{
                    println("READ : $it")
                }
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