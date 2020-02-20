package status

import model.BillboardStatus
import topic.TopicContext
import topic.TopicService

object StatusListener {

    fun subscribeBillboardStatus() {
        val topicService = TopicService.createFor(BillboardStatus::class.java, "billboard-manager", TopicContext())
        topicService.subscribe {
            println("Received BillboardStatus from Billboard(${it.header.source})")
            StatusProvider.handleStatus(it.header.source, it.payload)
        }
    }

}