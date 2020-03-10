package metric


import influxDB
import model.*
import org.junit.*
import subscribeAdChanged
import subscribeAdPoolChanged
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
        subscribeAdPoolChanged(metricService)
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
    fun `published BillboardStatus should create BillboardStatus metric on influxdb`() {
        val topicService = TopicService.createFor(BillboardStatus::class.java, "metric-service-test", TopicContext())
        val billboardEnvironment = BillboardEnvironment(Weather.CLOUDS, Float.MIN_VALUE, Float.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Int.MIN_VALUE,"country",Float.MIN_VALUE)
        val billboardStatus = BillboardStatus(Health.UP,"12", billboardEnvironment)
        println("Published $billboardStatus")
        topicService.publish(billboardStatus)
        Thread.sleep(1000)
        val billboardStatusLastRecord = metricService.getLastBillboardStatusRecord()
        Assert.assertEquals(billboardStatus, billboardStatusLastRecord)
    }

    @Test
    fun `published AdPoolChanged should create AdPool metric on influxdb`() {
        val topicService = TopicService.createFor(AdPoolChanged::class.java, "metric-service-test", TopicContext())
        val adPoolChanged = AdPoolChanged(setOf("1", "2", "5", "6", "7", "100"))
        println("Published $adPoolChanged")
        topicService.publish(adPoolChanged)
        Thread.sleep(1000)
        val adPoolLastRecord = metricService.getLastAdPoolRecord()
        Assert.assertEquals(adPoolChanged, adPoolLastRecord)
    }

    @Test
    fun `published AdChanged should create AdDuration metric on influxdb`() {
        val topicService = TopicService.createFor(AdChanged::class.java, "metric-service-test", TopicContext())
        val person = Person(Gender.MAN, Age.ADULT)
        val adChanged = AdChanged("2",20000, listOf(person))
        println("Published $adChanged")
        topicService.publish(adChanged)
        Thread.sleep(1000)
        val adDurationLastRecord = metricService.getLastAdDurationRecord()
        Assert.assertEquals(adChanged.durationMs, adDurationLastRecord)
    }

}