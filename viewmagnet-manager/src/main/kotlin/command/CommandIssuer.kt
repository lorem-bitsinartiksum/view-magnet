package command

import model.*
import topic.TopicContext
import topic.TopicService

class CommandIssuer {

    fun showAd(adId : String) {
        val topicService = TopicService.createFor(ShowAd::class.java, "billboard-manager", TopicContext())
        val showAdObj = ShowAd(adId)
        topicService.publish(showAdObj)
        println("Published $showAdObj")
    }

    fun shutDown(billboardId: String) {
        val topicService = TopicService.createFor(ShutDown::class.java, "billboard-manager", TopicContext())
        val shutDownObj = ShutDown(billboardId)
        topicService.publish(shutDownObj)
        println("Published $shutDownObj")
    }

}