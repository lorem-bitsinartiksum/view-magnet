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

data class BillboardStatus(
    val billboard_id: Int = 0,
    val health: Health = Health.UP,
    val ad_id: Int = 0,
    val weather: Weather = Weather.UNKNOWN,
    val temperature: Int = 0,
    val sound_level: Int = 0,
    val timestamp: Long = Date().time)

data class MetricCount(val count: Int = 0)

data class FieldAverage(val average: Double = 0.0)


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

enum class Health(@JsonValue val health : String) {
    UP("up"),
    DOWN("down");
}

val influxDB: InfluxDB by lazy { InfluxDBFactory.connect("http://localhost:8086", "root", "root") }

fun main() {
    val app = Javalin.create().start(7000)
    val metricService = MetricService(influxDB)
    val controller = Controller(metricService)

    app.routes {
        get("/company-metric-count/:company_id", { ctx ->
            controller.getMetricCount(ctx, "company_id")
        })
        get("/ad-metric-count/:ad_id", { ctx ->
            controller.getMetricCount(ctx, "ad_id")
        })
        get("/billboard-metric-count/:billboard_id", { ctx ->
            controller.getMetricCount(ctx, "billboard_id")
        })
        get("/age-average/:ad_id", { ctx ->
            controller.getFieldAverage(ctx, "age")
        })
        get("/temperature-average/:ad_id", { ctx ->
            controller.getFieldAverage(ctx, "temperature")
        })
        get("/sound-level-average/:ad_id", { ctx ->
            controller.getFieldAverage(ctx, "sound_level")
        })
        post("/metric", { ctx ->
            controller.postMetric(ctx)
        })
        post("/billboard-status", { ctx ->
            controller.postBillboardStatus(ctx)
        })
    }

}

class Controller(private val metricService: MetricService) {

    fun postMetric(ctx: Context) {
        val metric = ctx.bodyAsClass(Metric::class.java)
        val result = metricService.createMetric(metric)
        ctx.status(result)
    }

    fun getMetricCount(ctx: Context, tag: String) {
        ctx.json(metricService.getMetricCount(tag, ctx.pathParam(tag)))
    }

    fun getFieldAverage(ctx: Context, field: String) {
        ctx.json(metricService.getFieldAverage(field, ctx.pathParam("ad_id")))
    }

    fun postBillboardStatus(ctx: Context) {
        val billboardStatus = ctx.bodyAsClass(BillboardStatus::class.java)
        val result = metricService.createBillboardStatus(billboardStatus)
        ctx.status(result)
    }
}
