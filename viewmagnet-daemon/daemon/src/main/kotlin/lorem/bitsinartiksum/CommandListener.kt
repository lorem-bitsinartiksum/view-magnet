package lorem.bitsinartiksum

import model.AdPoolChanged
import model.ShowAd
import topic.TopicContext
import topic.TopicService


class CommandListener(private val cfg: Config, private val handler: CommandHandler) {

    fun start() {
        subscribe(AdPoolChanged::class.java).subscribe {
            handler.changePool(it.payload.newPool)
        }
        subscribe(ShowAd::class.java).subscribe {
            handler.showAd(it.payload.ad)
        }
    }

    private fun <T> subscribe(clazz: Class<T>): TopicService<T> {
        return TopicService.createFor(clazz, "billboard-${cfg.id}", TopicContext(mode = cfg.mode))
    }
}

