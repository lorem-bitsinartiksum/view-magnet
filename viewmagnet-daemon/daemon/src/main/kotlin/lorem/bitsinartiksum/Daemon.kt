package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager
import model.BillboardEnvironment
import java.util.concurrent.atomic.AtomicBoolean

class Daemon {

    private var envRef: BillboardEnvironment? = null
    private var isPaused = AtomicBoolean(false)

    fun start() = runBlocking {

        val cfg = Config()

        val adDisplay = AdDisplay(
            AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
            650, 1000
            , { envRef = it }, { isPaused.set(it) }, { })

        val adManager = AdManager({ ad ->
            val img = AdDisplay.loadImg(ad.content) ?: return@AdManager
            val qrUrl =
                "http://192.168.1.102:7000/api/qr?mod=${cfg.mode.toString()
                    .toLowerCase()}&billboard=${cfg.id}&ad=${ad.id}"
            adDisplay.changeAd(img, qrUrl)
        }, cfg)

        adDisplay.changeToRelatedAd = {
            adManager.showRelatedAd(it)
        }

        val cmdListener = CommandListener(cfg, adManager)

        val statusReporter = StatusReporter(cfg)

        val envListener = EnvironmentListener(adManager, isPaused)

        adDisplay.show()
        adManager.start()
        cmdListener.start()
        envListener.start()
        statusReporter.start(watchEnv(envListener), watchCurrentAd(adManager))
    }

    @ExperimentalCoroutinesApi
    fun CoroutineScope.watchEnv(envListener: EnvironmentListener) = produce<BillboardEnvironment> {
        while (true) {
            val env = if (isPaused.get()) envRef else envListener.envRef
            if (env == null) {
                send(envListener.envRef)
            } else {
                send(env)
            }
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
}

