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
    fun `published AdChanged should create Person metrics on influxdb`() {
        val topicService = TopicService.createFor(AdChanged::class.java, "metric-service-test", TopicContext())
        val person = Person(Gender.MAN, Age.ADULT)
        val adChanged = AdChanged("2",10000, listOf(person))
        println("Published $adChanged")
        topicService.publish(adChanged)
        Thread.sleep(1000)
        val personLastRecord = metricService.getLastPersonRecord()
        Assert.assertEquals(person, personLastRecord)
    }

    @Test
    fun `published BillboardStatus should create BillboardStatus metrics on influxdb`() {
        val topicService = TopicService.createFor(BillboardStatus::class.java, "metric-service-test", TopicContext())
        val billboardEnvironment = BillboardEnvironment(Weather.SUNNY,10,10)
        val billboardStatus = BillboardStatus(Health.UP,"12", billboardEnvironment)
        println("Published $billboardStatus")
        topicService.publish(billboardStatus)
        Thread.sleep(1000)
        val billboardStatusLastRecord = metricService.getLastBillboardStatusRecord()
        Assert.assertEquals(billboardStatus, billboardStatusLastRecord)
    }

}