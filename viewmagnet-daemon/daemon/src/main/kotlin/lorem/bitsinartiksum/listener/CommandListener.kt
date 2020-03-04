package lorem.bitsinartiksum.listener

import lorem.bitsinartiksum.ad.AdManager
import lorem.bitsinartiksum.config.Config
import model.AdPoolChanged
import model.ShowAd
import topic.TopicContext

import topic.TopicService


class CommandListener(val cfg: Config, val manager: AdManager) {

    fun start() {
        subscribe(AdPoolChanged::class.java)
        subscribe(ShowAd::class.java)
    }

    private inline fun <reified T> subscribe(clazz: Class<T>) {
        val ts = TopicService.createFor(AdPoolChanged::class.java, "billboard-${cfg.id}", TopicContext())
        ts.subscribe { manager.handleCommand(it.payload) }
    }
}
