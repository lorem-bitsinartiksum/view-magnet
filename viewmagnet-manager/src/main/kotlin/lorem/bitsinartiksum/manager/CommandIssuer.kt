package lorem.bitsinartiksum.manager


import model.Ad
import model.ShowAd
import model.Shutdown
import topic.TopicContext
import topic.TopicService

class CommandIssuer {

    private val shutdownTs = TopicService.createFor(Shutdown::class.java, "billboard-manager", TopicContext())
    private val showAdTs = TopicService.createFor(ShowAd::class.java, "billboard-manager", TopicContext())

    fun showAd(ad: Ad) {

        val cmd = ShowAd(ad)
        showAdTs.publish(cmd)
    }

    fun shutDown(billboardId: String) {

        val cmd = Shutdown(billboardId)
        shutdownTs.publish(cmd)
    }
}