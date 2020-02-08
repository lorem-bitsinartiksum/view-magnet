package metric


import influxDB
import model.*
import org.junit.*
import subscribeAdChanged
import subscribeBillboardStatus
import topic.TopicContext
import topic.TopicService
import java.util.*

internal class MetricServiceTest {

    private val metricService = MetricService(influxDB)

    @Before
    fun start() {
        subscribeAdChanged(metricService)
        subscribeBillboardStatus(metricService)
    }

    @After
    fun finish() {
        influxDB.close()
    }

    @Test
    fun `published AdChanged should created Person metrics on influxdb`() {
        val topicService = TopicService.createFor(AdChanged::class.java, "metric-service-test", TopicContext())
        val person = Person(Gender.MAN, Age.ADULT)
        val adChanged = AdChanged("1",10000, listOf(person))
        println("Published $adChanged")
        topicService.publish(adChanged)
        Thread.sleep(1000)
        val metricLastRecord = metricService.getLastPersonRecord()
        Assert.assertEquals(person, metricLastRecord)
    }

}