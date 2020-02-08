import metric.MetricService
import model.BillboardStatus
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import topic.TopicContext
import topic.TopicService
import model.AdChanged


data class MetricCount(val count: Int = 0)

data class FieldAverage(val average: Double = 0.0)


val influxDB: InfluxDB by lazy { InfluxDBFactory.connect("http://localhost:8086", "root", "root") }

fun main() {
    val metricService = MetricService(influxDB)
    subscribeAdChanged(metricService)
    subscribeBillboardStatus(metricService)
    influxDB.close()
}

fun subscribeAdChanged(metricService: MetricService) {
    val topicService = TopicService.createFor(AdChanged::class.java, "metric-service", TopicContext())
    topicService.subscribe {
        println("Received AdChanged from Billboard(${it.header.source})")
        metricService.createPersonMetrics(it.payload.detections, it.header.source, it.header.createdAt, it.payload.id)
    }
}

fun subscribeBillboardStatus(metricService: MetricService) {
    val topicService = TopicService.createFor(BillboardStatus::class.java, "metric-service", TopicContext())
    topicService.subscribe {
        println("Received BillboardStatus from Billboard(${it.header.source})")
        metricService.createBillboardStatus(it.payload, it.header.source, it.header.createdAt)
    }
}