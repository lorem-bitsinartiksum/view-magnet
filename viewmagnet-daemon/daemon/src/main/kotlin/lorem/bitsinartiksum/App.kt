package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import model.Ad
import model.AdPoolChanged
import model.BillboardEnvironment
import model.Weather
import topic.TopicContext
import topic.TopicService
import java.util.*
import java.util.concurrent.Executors

fun main() = runBlocking<Unit> {

    Executors.newSingleThreadExecutor().execute {
        val d = Daemon()
        d.start()

    }

    val newPool = setOf(
        Ad(
            "t1",
            "https://images.unsplash.com/photo-1582740735409-d0ae8d48976e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80"
        ) to 0.6f,
        Ad(
            "t2",
            "https://images.unsplash.com/photo-1539006749419-f9a3eb2bf3fe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=701&q=80"
        ) to 0.4f
    )
    delay(1500)
    val ts = TopicService.createFor(AdPoolChanged::class.java, "pool-changer", TopicContext())
    ts.publish(AdPoolChanged(newPool))

//    val cfg = Config()
//
//    val display = AdDisplay(
//        AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
//        200,
//        300
//    )
//    display.show()
//
//    val adManager = AdManager({ display.changeAd(AdDisplay.loadImg(it.content)!!) }, cfg)
//
//    val statusReporter = StatusReporter(cfg)
//
//    val env = envUpdate()
//    val ad = adUpdate()
//
//    adManager.start()
//
//    CommandListener(cfg, adManager).start()
//    async {
//        statusReporter.start(env, ad)
//    }
//
//
//    val ts = TopicService.createFor(BillboardStatus::class.java, "env-lorem.bitsinartiksum.listener", TopicContext())
//
//    ts.subscribe {
//        println("RECEIVED STATUS $it")
//    }
//
//    val tss = TopicService.createFor(AdPoolChanged::class.java, "test-pub", TopicContext())
//    val testAd = Ad(
//        "TESTAD",
//        "https://images.unsplash.com/photo-1583073600538-f219abfb20bc?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
//    )
//    Other()
//    tss.publish(AdPoolChanged(setOf(testAd)))


}

fun CoroutineScope.envUpdate() = produce {
    while (true) {
        var t = 1
        send(BillboardEnvironment(Weather.FOG, t++, t * 10))
        delay(2000)
    }
}

fun CoroutineScope.adUpdate() = produce {
    while (true) {
        send(UUID.randomUUID().toString())
        delay(1000)
    }
}
