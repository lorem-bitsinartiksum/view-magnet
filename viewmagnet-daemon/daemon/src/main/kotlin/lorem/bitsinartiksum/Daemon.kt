package lorem.bitsinartiksum

import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager
import model.Mode
import java.awt.Color

data class Daemon(
    val cfg: Config = Config(),
    val adDisplay: AdDisplay = AdDisplay(
        AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
        200,
        300
    ),
    val adManager: AdManager = AdManager({
        when (cfg.mode) {
            Mode.REAL -> adDisplay.changeAd(AdDisplay.loadImg(it.content)!!)
            Mode.SIM -> {
                val (r, g, b) = it.content.split(",").map { it.toInt() }
                val color = Color(r, g, b)
                adDisplay.changeAd(color)
            }
        }
    }, cfg),
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
