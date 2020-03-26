package lorem.bitsinartiksum

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import model.Ad
import model.AdPoolChanged
import model.ShowAd
import topic.TopicContext
import topic.TopicService
import java.util.concurrent.Executors
import java.util.logging.LogManager

fun main() = runBlocking<Unit> {
    LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"))

    Executors.newSingleThreadExecutor().execute {
        val d = Daemon()
        d.start()
    }


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
    val tss = TopicService.createFor(AdPoolChanged::class.java, "test-pub", TopicContext())
    val ts = TopicService.createFor(ShowAd::class.java, "test-pub", TopicContext())
    val testAd = Ad(
        "TESTAD",
        "123,234,122"
    )
//    Other()
    delay(1000)
    tss.publish(AdPoolChanged(setOf(testAd to 0.4f, testAd.copy(content = "2,2,220") to 0.4f)))
    delay(1000)
    ts.publish(ShowAd(Ad("KASKESI", "255,0,0")))
}

