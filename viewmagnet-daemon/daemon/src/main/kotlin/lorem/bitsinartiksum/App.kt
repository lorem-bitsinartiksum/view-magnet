package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.config.Config
import lorem.bitsinartiksum.reporter.StatusReporter
import model.BillboardEnvironment
import model.BillboardStatus
import model.Weather
import topic.TopicContext
import topic.TopicService
import java.util.*


fun main() = runBlocking<Unit> {

    val sp = StatusReporter(Config())

    val ts = TopicService.createFor(BillboardStatus::class.java, "env-listener", TopicContext())
    ts.subscribe {
        println("RECEIVED $it")
    }
    val env = envUpdate()
    val ad = adUpdate()
    sp.start(env, ad)
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
