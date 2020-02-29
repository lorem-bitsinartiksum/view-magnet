package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdManager
import model.BillboardEnvironment
import model.Weather
import java.util.*


fun main() = runBlocking<Unit> {

    //    val sp = StatusReporter(Config())
    AdManager()

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
