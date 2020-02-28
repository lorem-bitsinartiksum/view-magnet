package lorem.bitsinartiksum.listener

import lorem.bitsinartiksum.ad.AdManager
import lorem.bitsinartiksum.config.Config
import model.AdPoolChanged
import topic.TopicContext

import topic.TopicService


class CommandListener(val cfg: Config, val manager: AdManager) {

    private val ts = TopicService.createFor(AdPoolChanged::class.java, "billboard-${cfg.id}", TopicContext())


    fun start() {

        ts.subscribe {
            manager.handleCommand(it.payload)
        }

    }
}

