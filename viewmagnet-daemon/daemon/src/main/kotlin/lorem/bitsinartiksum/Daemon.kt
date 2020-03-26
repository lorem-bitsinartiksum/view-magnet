package lorem.bitsinartiksum

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager
import model.BillboardEnvironment
import model.Mode
import model.Weather
import java.awt.Color
import java.util.*

data class Daemon(
    val cfg: Config = Config(),
    val adDisplay: AdDisplay = AdDisplay(
        AdDisplay.loadImg("https://wallpaperaccess.com/full/173836.jpg")!!,
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

fun CoroutineScope.envUpdate() = produce {
    while (true) {
        var t = 1.0f
        send(BillboardEnvironment(Weather.FOG, t.inc(), t.inc(), 3, 4, 5, "a", 23.4f))
        delay(2000)
    }
}

fun CoroutineScope.adUpdate() = produce {
    while (true) {
        send(UUID.randomUUID().toString())
        delay(1000)
    }
}