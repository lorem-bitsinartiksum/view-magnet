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

    AppConfig().setup().start()
    }


