package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager
import lorem.bitsinartiksum.config.Config
import lorem.bitsinartiksum.reporter.StatusReporter
import model.BillboardEnvironment
import model.BillboardStatus
import model.Weather
import topic.TopicContext
import topic.TopicService
import java.util.*


fun main() = runBlocking<Unit> {

    val cfg = Config()

    val display = AdDisplay(
        AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
        200,
        300
    )
    display.show()

    val adManager = AdManager({ display.changeAd(AdDisplay.loadImg(it.content)!!) }, cfg)

    val statusReporter = StatusReporter(cfg)

    val env = envUpdate()
    val ad = adUpdate()

    adManager.start()
    statusReporter.start(env, ad)

    val ts = TopicService.createFor(BillboardStatus::class.java, "env-listener", TopicContext())
    ts.subscribe {
        println("RECEIVED $it")
    }


}

fun CoroutineScope.envUpdate() = produce {
    while (true) {
        var t = 1
        send(BillboardEnvironment(Weather.FOGGY, t++, t * 10))
        delay(2000)
    }
}

fun CoroutineScope.adUpdate() = produce {
    while (true) {
        send(UUID.randomUUID().toString())
        delay(1000)
    }
}
