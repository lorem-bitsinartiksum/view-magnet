package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager
import model.BillboardEnvironment
import model.Weather

data class Daemon(
    val cfg: Config = Config(),
    val adDisplay: AdDisplay = AdDisplay(
        AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
        650, 1000
    ),
    val adManager: AdManager = AdManager({ ad, duration ->
        val img = AdDisplay.loadImg(ad.content) ?: return@AdManager
        adDisplay.changeAd(img, duration)
    }, cfg),
    val cmdListener: CommandListener = CommandListener(
        cfg,
        adManager
    ),
    val statusReporter: StatusReporter = StatusReporter(
        cfg
    ),
    val envListener: EnvironmentListener = EnvironmentListener(adManager)
) {

    fun start() = runBlocking {
        adDisplay.show()
        adManager.start()
        cmdListener.start()
        envListener.start()

        StatusReporter(cfg).start(envUpdate(), adUpdate(adManager))

    }
}


fun CoroutineScope.envUpdate() = produce {
    while (true) {
        var t = 1
        send(
            BillboardEnvironment(
                Weather.FOG,
                t++.toFloat(),
                (t * 10).toFloat(),
                t.toLong(),
                (t + 20).toLong(),
                t,
                "country",
                t.toFloat()
            )
        )
        delay(2000)
    }
}

fun CoroutineScope.adUpdate(adMgr: AdManager) = produce {
    while (true) {
        send(adMgr.currentAd.content)
        delay(1000)
    }
}