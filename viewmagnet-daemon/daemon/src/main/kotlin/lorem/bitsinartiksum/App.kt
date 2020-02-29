package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import model.BillboardEnvironment
import model.Weather
import java.util.*


fun main() = runBlocking<Unit> {

    //    val sp = StatusReporter(Config())
    val display = AdDisplay(
        AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
        200,
        300
    )
    display.show()

    delay(1000)
    display.changeAd(AdDisplay.loadImg("https://images.unsplash.com/photo-1582740735409-d0ae8d48976e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80")!!)
//    AdManager()

//    val ts = TopicService.createFor(BillboardStatus::class.java, "env-listener", TopicContext())
//    ts.subscribe {
//        println("RECEIVED $it")
//    }
//    val env = envUpdate()
//    val ad = adUpdate()
//    sp.start(env, ad)
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
