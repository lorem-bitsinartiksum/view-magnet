package status

import model.BillboardStatus
import topic.TopicContext
import topic.TopicService

class StatusListener {

    fun subscribeBillboardStatus() {
        val topicService = TopicService.createFor(BillboardStatus::class.java, "billboard-manager", TopicContext())
        topicService.subscribe {
            println("Received BillboardStatus from Billboard(${it.header.source})")
            val statusProvider = StatusProvider()
            statusProvider.handleStatus(it.header.source, it.payload)
        }
    }

}