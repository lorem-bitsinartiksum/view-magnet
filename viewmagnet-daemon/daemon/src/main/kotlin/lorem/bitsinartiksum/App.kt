package lorem.bitsinartiksum

import kotlinx.coroutines.runBlocking
import model.Ad
import model.AdPoolChanged
import model.Mode
import model.ShowAd
import topic.TopicContext
import topic.TopicService
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.logging.LogManager

class Other() {
    init {
        println("ASDDAS")
        val otherTs = TopicService.createFor(AdPoolChanged::class.java, "test-pub2", TopicContext())

        otherTs.subscribe { println("RECEIVED $it") }
    }
}
fun main() = runBlocking<Unit> {
    LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"))

//    println(ClassLoader.getSystemResourceAsStream("logging.properties")?.readAllBytes().toString())
    val d = Daemon()
    CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute {
        println("CHANGING AD")
        val ts = TopicService.createFor(ShowAd::class.java, "vikvik", TopicContext(mode = Mode.REAL))
        ts.publish(
            ShowAd(
                Ad(
                    id = "ASD",
                    content = "https://images.unsplash.com/photo-1559628129-67cf63b72248?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1234&q=80"
                )
            )
        )
    }
    d.start()
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
