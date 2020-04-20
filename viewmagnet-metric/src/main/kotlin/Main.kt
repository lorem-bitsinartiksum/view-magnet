import metric.MetricService
import model.*
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import topic.TopicContext
import topic.TopicService


data class MetricCount(val count: Int = 0)

data class FieldAverage(val average: Double = 0.0)


val influxDB: InfluxDB by lazy { InfluxDBFactory.connect("http://localhost:8086", "root", "root") }

private val tsAdChanged = TopicService.createFor(AdChanged::class.java, "metric-service", TopicContext())

private val tsBillboardStatus = TopicService.createFor(BillboardStatus::class.java, "metric-service", TopicContext())

private val tsAdPoolChanged = TopicService.createFor(AdPoolChangedWithBillboardId::class.java, "metric-service", TopicContext())

fun main() {
    val metricService = MetricService(Mode.valueOf(System.getProperty("mode", "sim").toUpperCase()), influxDB)
    subscribeAdChanged(metricService)
    subscribeBillboardStatus(metricService)
    subscribeAdPoolChanged(metricService)
    influxDB.close()
}

fun subscribeAdChanged(metricService: MetricService) {
    tsAdChanged.subscribe {
        println("Received AdChanged from Billboard(${it.header.source})")
        metricService.createPersonMetrics(it.payload.detections, it.header.source, it.header.createdAt, it.payload.ad.id)
        metricService.createAdDuration(it.payload.durationMs, it.header.source, it.header.createdAt, it.payload.ad.id)
    }
}

fun subscribeBillboardStatus(metricService: MetricService) {
    tsBillboardStatus.subscribe {
        println("Received BillboardStatus from Billboard(${it.header.source})")
        metricService.createBillboardStatus(it.payload, it.header.source, it.header.createdAt)
    }
}

fun subscribeAdPoolChanged(metricService: MetricService) {
    tsAdPoolChanged.subscribe {
        println("Received AdPoolChanged from ${it.header.source}")
        metricService.createAdPool(it.payload.billboardId, it.payload.newPool, it.header.createdAt)
    }
}