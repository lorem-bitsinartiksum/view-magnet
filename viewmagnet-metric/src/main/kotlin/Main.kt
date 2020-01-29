import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.http.Context
import io.javalin.Javalin
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import java.util.*

data class Metric(val ad_id: Int = 0, val billboard_id: Int = 0, val human_count: Int = 0, val timestamp: Long = Date().time)

data class Total(val count: Double, val sum: Double, val min: Double, val max: Double)

val influxDB: InfluxDB by lazy { InfluxDBFactory.connect("http://localhost:8086", "root", "root") }

fun main() {
    val app = Javalin.create().start(7000)
    val metricService = MetricService(influxDB)
    val controller = Controller(metricService)

    app.routes {
        get("/get", { ctx ->
            controller.get(ctx)
        })
        post("/post", { ctx ->
            controller.post(ctx)
        })
    }

}

class Controller(private val metricService: MetricService) {

    fun post(ctx: Context) {
        val statistic = ctx.bodyAsClass(Metric::class.java)
        val result = metricService.create(statistic)
        ctx.status(result)
    }

    fun get(ctx: Context) {
        ctx.json(metricService.aggregated())
    }
}
