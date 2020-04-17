package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager

data class Daemon(
    val cfg: Config = Config(),
    val adDisplay: AdDisplay = AdDisplay(
        AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
        650, 1000
    ),
    val adManager: AdManager = AdManager({ ad ->
        val img = AdDisplay.loadImg(ad.content) ?: return@AdManager
        val qrUrl =
            "http://192.168.1.102:7000/api/qr?mod=${cfg.mode.toString().toLowerCase()}&billboard=${cfg.id}&ad=${ad.id}"
        adDisplay.changeAd(img, qrUrl)
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
        StatusReporter(cfg).start(watchEnv(envListener), watchCurrentAd(adManager))

    }
}

fun CoroutineScope.watchEnv(envListener: EnvironmentListener) = produce {
    while (true) {
        send(envListener.envRef)
        delay(1000)
    }
}

@ExperimentalCoroutinesApi
fun CoroutineScope.watchCurrentAd(adMgr: AdManager) = produce {
    while (true) {
        send(adMgr.currentAd.content)
        delay(1000)
    }
}