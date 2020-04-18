package lorem.bitsinartiksum

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.Detection
import model.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean


data class WeatherInfo(
    val weather: Weather,
    val tempC: Float,
    val windSpeed: Float,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val country: String
)

class EnvironmentListener(
    private val cmdHandler: CommandHandler,
    private val isOverridden: AtomicBoolean,
    fakeEnvChan: Flow<BillboardEnvironment>,
    fakePplChan: Flow<Person>
) {

    companion object {
        var detectedPersons: MutableList<Person> = Collections.synchronizedList(mutableListOf())
    }

    private val jackson = jacksonObjectMapper()
    var envRef: BillboardEnvironment = BillboardEnvironment(Weather.UNKNOWN, 0f, 0f, 0, 0, 0, "", 0f)

    init {
        runBlocking(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
            launch {
                fakeEnvChan.collect {
                    if (isOverridden.get()) {
                        envRef = it
                        handleEnvData(it)
                    }
                }
            }
            launch {
                fakePplChan.collect {
                    if (isOverridden.get())
                        detectedPersons.add(it)
                }
            }
        }
    }

    fun start() {

        var weatherInfo = WeatherInfo(
            weather = Weather.UNKNOWN,
            tempC = 0F,
            windSpeed = 0F,
            sunrise = 0,
            sunset = 0,
            timezone = 0,
            country = "country"
        )

        runPythonScript("weather-info\\weather.py") {
            val json = jackson.readTree(it)

            if (!isOverridden.get() && !json.isEmpty) {
                val weather = Weather.valueOf(json.path("weather").path(0).get("main").asText("UNKNOWN").toUpperCase())
                val temp = (json.path("main").get("temp").asText("0")).toFloat()
                val wind = (json.path("wind").get("speed").asText("0")).toFloat()
                val sunrise = (json.path("sys").get("sunrise").asText("0")).toLong()
                val sunset = (json.path("sys").get("sunset").asText("0")).toLong()
                val timezone = (json.get("timezone").asText("0")).toInt()
                val country = (json.path("sys").get("country").asText("UNKNOWN")).toString()

                weatherInfo = WeatherInfo(
                    weather = weather,
                    tempC = temp,
                    windSpeed = wind,
                    sunrise = sunrise,
                    sunset = sunset,
                    timezone = timezone,
                    country = country
                )
            }
        }

        runPythonScript("sound-pressure-level-meter\\spl_meter.py") {

            if (isOverridden.get()) {
                return@runPythonScript
            }

            val sound = it.toFloatOrNull()
            if (sound != null && weatherInfo.weather != Weather.UNKNOWN) {
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
                handleEnvData(envRef)
            }
        }

        runPythonScriptWithBatch("age-gender-pred") {

            if (isOverridden.get()) return@runPythonScriptWithBatch

            val regex = "\\d+,\\d+".toRegex()
            if (it.startsWith("age-gender : ")) {
                println("READ : $it")
                val values = it
                    .replace("age-gender : ", "")
                    .replace("[", "")
                    .replace("]", "")
                    .replace("'", "")
                    .replace(" ", "")

                val passingPpl = regex
                    .findAll(values)
                    .map { it.value.split(",") }
                    .map { (age, gender) -> Person(Gender.valueOf(gender.toUpperCase()), Age.of(age.toInt())) }
                    .toList()


                detectedPersons.addAll(passingPpl)

                if (passingPpl.any { it.age == Age.BABY }) {
                    cmdHandler.showRelatedAd(Detection.BABY)
                }
            } else if (it.startsWith("object : ")) {
                when {
                    it.contains("dog", ignoreCase = true) -> {
                        cmdHandler.showRelatedAd(Detection.DOG)
                    }
                    it.contains("cat", ignoreCase = true) -> {
                        cmdHandler.showRelatedAd(Detection.CAT)
                    }
                    it.contains("bicycle", ignoreCase = true) -> {
                        cmdHandler.showRelatedAd(Detection.BICYCLE)
                    }
                }
            }
        }
    }

    private fun handleEnvData(envData: BillboardEnvironment? = null) {
        val env = envData ?: envRef

        when {
            env.weather == Weather.TORNADO -> cmdHandler.showRelatedAd(Detection.TORNADO)
            env.weather == Weather.RAIN -> cmdHandler.showRelatedAd(Detection.RAIN)
            env.soundDb > 80 -> cmdHandler.showRelatedAd(Detection.NOISE)
            env.tempC > 35 -> cmdHandler.showRelatedAd(Detection.HOT)
            env.tempC < -5 -> cmdHandler.showRelatedAd(Detection.COLD)
        }
    }
}


private fun runPythonScript(name: String, handler: (String) -> Unit) {
    val scriptPath = Path.of(System.getProperty("user.dir"), "viewmagnet-daemon", name)

    Executors.newSingleThreadExecutor().execute {
        val command = "python $scriptPath"
        val process = Runtime.getRuntime().exec(command)
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        var line = reader.readLine()
        while (line != null) {
            handler(line)
            line = reader.readLine()
        }
        process.waitFor()
    }
}

private fun runPythonScriptWithBatch(name: String, handler: (String) -> Unit) {
    val scriptPath = Path.of(System.getProperty("user.dir"), "viewmagnet-daemon", name)
    Executors.newSingleThreadExecutor().execute {
        val process =
            ProcessBuilder("cmd.exe", "/C", "$scriptPath\\build.bat $scriptPath").redirectErrorStream(true).start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        process.outputStream.close()

        var line = reader.readLine()
        while (line != null) {
            handler(line)
            line = reader.readLine()
        }
        process.waitFor()
    }
}