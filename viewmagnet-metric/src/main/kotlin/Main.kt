import metric.MetricService
import model.BillboardStatus
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import topic.TopicContext
import topic.TopicService
import model.AdChanged
import model.AdPoolChanged
import model.Mode


data class MetricCount(val count: Int = 0)

data class FieldAverage(val average: Double = 0.0)


val influxDB: InfluxDB by lazy { InfluxDBFactory.connect("http://localhost:8086", "root", "root") }

private val tsAdChanged = TopicService.createFor(AdChanged::class.java, "metric-service", TopicContext())

private val tsBillboardStatus = TopicService.createFor(BillboardStatus::class.java, "metric-service", TopicContext())

fun main() {
    val metricService = MetricService(Mode.SIM, influxDB)
    subscribeAdChanged(metricService)
    subscribeBillboardStatus(metricService)
    subscribeAdPoolChanged(metricService)
    influxDB.close()
}

fun subscribeAdChanged(metricService: MetricService) {
    tsAdChanged.subscribe {
        println("Received AdChanged from Billboard(${it.header.source})")
        metricService.createPersonMetrics(it.payload.detections, it.header.source, it.header.createdAt, it.header.source)
        metricService.createAdDuration(it.payload.durationMs, it.header.source, it.header.createdAt, it.header.source)
    }
}

fun subscribeBillboardStatus(metricService: MetricService) {
    tsBillboardStatus.subscribe {
        println("Received BillboardStatus from Billboard(${it.header.source})")
        metricService.createBillboardStatus(it.payload, it.header.source, it.header.createdAt)
    }
}

fun subscribeAdPoolChanged(metricService: MetricService) {
    val topicService = TopicService.createFor(AdPoolChanged::class.java, "metric-service", TopicContext())
    topicService.subscribe {
        println("Received AdPoolChanged from ${it.header.source}")
        metricService.createAdPool(it.header.source, it.payload.newPool, it.header.createdAt)
    }
}