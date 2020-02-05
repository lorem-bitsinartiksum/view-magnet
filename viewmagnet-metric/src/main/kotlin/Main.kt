import metric.MetricService
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import topic.TopicContext
import topic.TopicService
import java.util.*


data class Metric(
    val timestamp: Long = Date().time,
    val ad_id: Long = 0,
    val age: Long = 0,
    val billboard_id: Long = 0,
    val company_id: Long = 0,
    val gender: Gender = Gender.UNDETECTED,
    val reality: Reality = Reality.REAL,
    val sound_level: Long = 0,
    val temperature: Long = 0,
    val weather: Weather = Weather.UNKNOWN)

data class BillboardStatus(
    val billboard_id: Long = 0,
    val health: Health = Health.UP,
    val ad_id: Long = 0,
    val weather: Weather = Weather.UNKNOWN,
    val temperature: Long = 0,
    val sound_level: Long = 0,
    val timestamp: Long = Date().time)

data class MetricCount(val count: Int = 0)

data class FieldAverage(val average: Double = 0.0)


enum class Gender {
    MALE,
    FEMALE,
    UNDETECTED;
}

enum class Weather {
    SUNNY,
    CLOUDY,
    WINDY,
    FOGGY,
    STORMY,
    SNOWY,
    RAINY,
    UNKNOWN;
}

enum class Reality {
    REAL,
    SIM;
}

enum class Health {
    UP,
    DOWN;
}

val influxDB: InfluxDB by lazy { InfluxDBFactory.connect("http://localhost:8086", "root", "root") }

fun main() {
    val metricService = MetricService(influxDB)
    subscribeMetric(metricService)
    subscribeBillboardStatus(metricService)
    influxDB.close()
}

fun subscribeMetric(metricService: MetricService) {
    val topicService = TopicService.createFor(Metric::class.java, TopicContext())
    topicService.subscribe {
        println("RECEIVED METRIC FROM BILLBOARD${it.payload.billboard_id}")
        metricService.createMetric(it.payload)
    }
}

fun subscribeBillboardStatus(metricService: MetricService) {
    val topicService = TopicService.createFor(BillboardStatus::class.java, TopicContext())
    topicService.subscribe {
        println("RECEIVED METRIC FROM BILLBOARD${it.payload.billboard_id}")
        metricService.createBillboardStatus(it.payload)
    }
}