import com.fasterxml.jackson.annotation.JsonValue
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.http.Context
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import java.util.*


data class Metric(
    val company_id: Int = 0,
    val ad_id: Int = 0,
    val billboard_id: Int = 0,
    val age: Int = 0,
    val gender: Gender = Gender.UNDETECTED,
    val weather: Weather = Weather.UNKNOWN,
    val temperature: Int = 0,
    val sound_level: Int = 0,
    val reality: Reality = Reality.REAL,
    val timestamp: Long = Date().time)

data class MetricCount(val count: Int = 0)


enum class Gender(@JsonValue val gender : String) {
    MALE("male"),
    FEMALE("female"),
    UNDETECTED("undetected");
}

enum class Weather(@JsonValue val weather : String) {
    SUNNY("sunny"),
    CLOUDY("cloudy"),
    WINDY("windy"),
    FOGGY("foggy"),
    STORMY("stormy"),
    SNOWY("snowy"),
    RAINY("rainy"),
    UNKNOWN("unknown");
}

enum class Reality(@JsonValue val reality : String) {
    REAL("real"),
    SIM("sim");
}

val influxDB: InfluxDB by lazy { InfluxDBFactory.connect("http://localhost:8086", "root", "root") }

fun main() {
    val app = Javalin.create().start(7000)
    val metricService = MetricService(influxDB)
    val controller = Controller(metricService)

    app.routes {
        get("/get_ad/:ad_id", { ctx ->
            controller.getAdMetricCount(ctx)
        })
        post("/post", { ctx ->
            controller.post(ctx)
        })
    }

}

class Controller(private val metricService: MetricService) {

    fun post(ctx: Context) {
        val metric = ctx.bodyAsClass(Metric::class.java)
        val result = metricService.create(metric)
        ctx.status(result)
    }

    fun getAdMetricCount(ctx: Context) {
        ctx.json(metricService.getAdMetricCount(ctx.pathParam("ad_id")))
    }
}
