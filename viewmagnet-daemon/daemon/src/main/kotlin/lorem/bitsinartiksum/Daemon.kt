package lorem.bitsinartiksum

import kotlinx.coroutines.runBlocking
import lorem.bitsinartiksum.ad.AdDisplay
import lorem.bitsinartiksum.ad.AdManager
import java.awt.image.BufferedImage

fun generateQr(text :String): BufferedImage {
    val QR = QRGenerator()
    return QRGenerator().generateQRCodeImage(text, 100, 100)
}

data class Daemon(
    val cfg: Config = Config(),
    val adDisplay: AdDisplay = AdDisplay(
        AdDisplay.loadImg("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")!!,
        650,
        1000,
        generateQr("https://images.unsplash.com/photo-1582996269871-dad1e4adbbc7?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=633&q=80")
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
