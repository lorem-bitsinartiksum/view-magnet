package lorem.bitsinartiksum

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lorem.bitsinartiksum.ad.Detection
import model.BillboardEnvironment
import model.Weather
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.concurrent.Executors

val jackson = jacksonObjectMapper()


data class weatherInfo(
    val weather: Weather,
    val tempC: Float,
    val windSpeed: Float,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val country: String
)


class EnvironmentListener(private val cmdHandler: CommandHandler) {
    companion object {
        private var envRef: BillboardEnvironment = BillboardEnvironment(Weather.UNKNOWN, 0F, 0F,0,0,0,"",0f )

        fun getEnvRef(): BillboardEnvironment {
            return envRef
        }
    }

    fun start() {
        startWatching()
    }

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
            }
        }

        runPythonScript("sound-pressure-level-meter\\spl_meter.py") {
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
                println(envRef.toString())
                when {
                    envRef.weather == Weather.TORNADO -> cmdHandler.showRelatedAd(Detection.TORNADO)
                    envRef.weather == Weather.RAIN -> cmdHandler.showRelatedAd(Detection.RAIN)
                    envRef.soundDb > 80 -> cmdHandler.showRelatedAd(Detection.NOISE)
                    envRef.tempC > 35 -> cmdHandler.showRelatedAd(Detection.HOT)
                    envRef.tempC < -5 -> cmdHandler.showRelatedAd(Detection.COLD)
                }
            }
        }

        runPythonScriptWithBatch("age-gender-pred") {
            if (it.startsWith("age-gender : ")) {
                println("READ : $it")
                val values = it
                    .replace("age-gender : ", "")
                    .replace("[", "")
                    .replace("]", "")
                    .replace("'", "")
                    .replace(" ", "")

                val parts = values.split(",")

                val arrayListAge = ArrayList<Int>()
                val arrayListGender = ArrayList<String>()

                for ((index, i) in parts.withIndex()) {
                    if (index % 2 == 0) {
                        arrayListAge.add(i.toInt())
                    } else {
                        arrayListGender.add(i)
                    }
                }
                if (arrayListAge.isNotEmpty() && arrayListAge.min()!! < 11) {
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