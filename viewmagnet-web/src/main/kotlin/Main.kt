import config.AppConfig
import model.BillboardEnvironment
import model.BillboardStatus
import model.Health
import model.Weather
import status.StatusListener
import status.StatusProvider
import topic.TopicContext
import topic.TopicService

fun main(args: Array<String>) {

    StatusProvider.statusTimer()
    StatusListener.subscribeBillboardStatus()

    val topicService = TopicService.createFor(BillboardStatus::class.java, "1", TopicContext())
    val billboardEnvironment = BillboardEnvironment(Weather.SUNNY,10,10)
    val billboardStatus = BillboardStatus(Health.UP,"12", billboardEnvironment)
    println("Published $billboardStatus")
    topicService.publish(billboardStatus)

    AppConfig().setup().start()
    }


