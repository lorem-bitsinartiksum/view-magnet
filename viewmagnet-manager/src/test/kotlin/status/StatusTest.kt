package status

import model.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import topic.TopicContext
import topic.TopicService

internal class StatusTest {

    @Before
    fun start() {

        StatusProvider.statusTimer()

        StatusListener.subscribeBillboardStatus()
    }

    @Test
    fun `published BillboardStatus should be handled by manager`() {
        val topicService = TopicService.createFor(BillboardStatus::class.java, "billboard-test-1", TopicContext())
        val billboardEnvironment = BillboardEnvironment(Weather.SUNNY,10,10)
        val billboardStatus = BillboardStatus(Health.UP,"12", billboardEnvironment)
        println("Published $billboardStatus")
        topicService.publish(billboardStatus)
        Thread.sleep(1000)
        Assert.assertEquals(Health.UP, StatusProvider.getStatus("billboard-test-1").health)
    }

    @Test
    fun `published BillboardStatus should be removed if timed out`() {
        val topicService = TopicService.createFor(BillboardStatus::class.java, "billboard-test-2", TopicContext())
        val billboardEnvironment = BillboardEnvironment(Weather.SUNNY,10,10)
        val billboardStatus = BillboardStatus(Health.UP,"12", billboardEnvironment)
        println("Published $billboardStatus")
        topicService.publish(billboardStatus)
        for (i in 1..11) {
            Thread.sleep(1000)
        }
        Assert.assertEquals(Health.DOWN, StatusProvider.getStatus("billboard-test-2").health)
    }
}