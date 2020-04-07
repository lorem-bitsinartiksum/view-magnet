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
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.timer
import kotlin.concurrent.write

typealias AdPool = Set<Pair<Ad, Similarity>>


data class weatherInfo(
    val weather: Weather,
    val tempC: Float,
    val windSpeed: Float,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val country: String
)

class AdManager(private val updateDisplay: (Ad, Duration) -> Unit, val cfg: Config) {
    private val logger = FluentLogger.forEnclosingClass()
    private val adChangedTs = TopicService.createFor(AdChanged::class.java, cfg.id, TopicContext())
    private var rollStartTime = System.currentTimeMillis()
    private val rwLock = ReentrantReadWriteLock()
    var pool: AdPool = setOf()
        private set

    private var schedule = Schedule(pool.toList(), cfg.window)
    val highPriorityAds: Queue<Ad> = LinkedList()

    var currentAd: Ad = Ad("0,0,0", "0,0,0")
        private set(newAd) {
            val durationMs = System.currentTimeMillis() - rollStartTime
            rollStartTime = System.currentTimeMillis()
            adChangedTs.publish(AdChanged(field, durationMs, listOf()))
            field = newAd
            updateDisplay(newAd, Duration.ofMillis(durationMs))
        }


    fun refreshPool(newPool: Set<Pair<Ad, Similarity>>) {
        rwLock.write {
            pool = newPool
            schedule = Schedule(pool.toList(), cfg.window)
            logger.atInfo().log("Refreshing Pool New Pool: $newPool")
        }
    }

    inline fun <reified T> handleCommand(cmd: T) {
        when (T::class.java) {
            AdPoolChanged::class.java -> {
                refreshPool((cmd as AdPoolChanged).newPool)
            }
            ShowAd::class.java -> {
                val ad = (cmd as ShowAd).ad
                highPriorityAds.add(ad)
            }
        }
    }

    fun start() {
        startWatching()
        timer("ad-scheduler", false, 0, cfg.period.toMillis()) {
            rwLock.read {
                currentAd = if (highPriorityAds.isNotEmpty())
                    highPriorityAds.poll()
                else
                    schedule.next() ?: currentAd
            }
        }
    }

    val jackson = jacksonObjectMapper()

    private fun startWatching() {
        var weatherInfo = weatherInfo(
            weather = Weather.UNKNOWN,
            tempC = 0F,
            windSpeed = 0F,
            sunrise = 0,
            sunset = 0,
            timezone = 0,
            country = "country"
        )
        var envRef: BillboardEnvironment

        runPythonScript("weather-info\\weather.py") {
            val json = jackson.readTree(it)
            if (!json.isEmpty) {
                val weather = Weather.valueOf(json.path("weather").path(0).get("main").asText("UNKNOWN").toUpperCase())
                val temp = (json.path("main").get("temp").asText("0")).toFloat()
                val wind = (json.path("wind").get("speed").asText("0")).toFloat()
                val sunrise = (json.path("sys").get("sunrise").asText("0")).toLong()
                val sunset = (json.path("sys").get("sunset").asText("0")).toLong()
                val timezone = (json.get("timezone").asText("0")).toInt()
                val country = (json.path("sys").get("country").asText("UNKNOWN")).toString()

                weatherInfo = weatherInfo(
                    weather = weather,
                    tempC = temp,
                    windSpeed = wind,
                    sunrise = sunrise,
                    sunset = sunset,
                    timezone = timezone,
                    country = country
                )
                print(weatherInfo)
            }
        }

        runPythonScript("sound-pressure-level-meter\\spl_meter.py") {
            println("READ Desibel: $it")
            val sound = it.toFloat()

            if (sound != null && !weatherInfo.weather.equals(Weather.UNKNOWN)) {
                envRef = BillboardEnvironment(
                    weather = weatherInfo.weather,
                    tempC = weatherInfo.tempC,
                    windSpeed = weatherInfo.windSpeed,
                    sunrise = weatherInfo.sunrise,
                    sunset = weatherInfo.sunset,
                    timezone = weatherInfo.timezone,
                    country = weatherInfo.country,
                    soundDb = sound
                )
                println(envRef.toString())
            }
        }

        runPythonScriptWithBatch("age-gender-pred") {
            if (it.startsWith("age-gender : ")) {
                println("READ : $it")
            } else if (it.startsWith("object : ")) {
                if (it.contains("dog", ignoreCase = true)) {
                    println("DOG DOG DOG")
                } else if (it.contains("person", ignoreCase = true)) {
                    println("PERSON PERSON PERSON")
                } else {
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