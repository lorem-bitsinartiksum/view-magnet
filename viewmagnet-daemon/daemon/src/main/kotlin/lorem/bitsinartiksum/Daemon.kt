package lorem.bitsinartiksum

import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager

data class Daemon(
    val cfg: Config = Config(),
    val adDisplay: AdDisplay = AdDisplay(
        AdDisplay.loadImg("https://wallpaperaccess.com/full/173836.jpg")!!,
        200,
        300
    ),
    val adManager: AdManager = AdManager({ adDisplay.changeAd(AdDisplay.loadImg(it.content)!!) }, cfg),
    val cmdListener: CommandListener = CommandListener(
        cfg,
        adManager
    ),
    val statusReporter: StatusReporter = StatusReporter(
        cfg
    )
) {

    fun start() = runBlocking {
        adDisplay.show()
        adManager.start()
        cmdListener.start()

        StatusReporter(cfg).start(envUpdate(), adUpdate())

    }
}
