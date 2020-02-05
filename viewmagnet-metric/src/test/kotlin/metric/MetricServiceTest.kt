package metric


import Metric
import influxDB
import org.junit.*
import subscribeBillboardStatus
import subscribeMetric
import topic.TopicContext
import topic.TopicService
import java.util.*

internal class MetricServiceTest {

    private val metricService = MetricService(influxDB)

    @Before
    fun start() {
        subscribeMetric(metricService)
        subscribeBillboardStatus(metricService)
    }

    @After
    fun finish() {
        influxDB.close()
    }

    @Test
    fun `pubslished Metric should be created on influxdb`() {
        val topicService = TopicService.createFor(Metric::class.java, TopicContext())
        val metric = Metric(Date().time, 2, 35, 30, 2, Gender.FEMALE, Reality.SIM, 10, 10, Weather.SUNNY)
        println("Published $metric")
        topicService.publish(metric)
        Thread.sleep(1000)
        val metricLastRecord = metricService.getLastMetricRecord()
        Assert.assertEquals(metric, metricLastRecord)
    }

}