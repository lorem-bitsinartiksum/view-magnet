import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import topic.TopicContext
import topic.TopicService
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