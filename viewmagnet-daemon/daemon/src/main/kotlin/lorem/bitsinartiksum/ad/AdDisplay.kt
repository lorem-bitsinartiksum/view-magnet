package lorem.bitsinartiksum.ad

import lorem.bitsinartiksum.QRGenerator
import java.awt.EventQueue
import java.awt.FlowLayout
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.image.BufferedImage
import java.net.URL
import java.time.Duration
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class AdDisplay(
    defaultImg: Image,
    width: Int,
    height: Int
) {
    private val frame = JFrame("AdView")
    private var duration: Duration? = null
    private var img = defaultImg
    private var poster = JLabel(ImageIcon(defaultImg.getScaledInstance(width, height, Image.SCALE_SMOOTH)))

    init {
        frame.layout = FlowLayout()
        frame.setSize(width, height)

        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                poster.icon =
                    ImageIcon(img.getScaledInstance(e.component.width, e.component.height, Image.SCALE_SMOOTH))
            }
        })
        overlayQr("/home")
        frame.add(poster)
    }

    fun show() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
    }

    fun changeAd(newImg: Image, newDuration: Duration) {
        img = newImg
        duration = newDuration
        overlayQr("//")
    }

    private fun overlayQr(url: String) {
        val (width, height) = Pair(frame.width, frame.height)
        val combinedImage = BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        )
        EventQueue.invokeLater {
            val g: Graphics2D = combinedImage.createGraphics()
            val QRimg = QRGenerator.generateQRCodeImage(url, 100, 100)
            g.drawImage(img, 0, 0, null)
            g.drawImage(QRimg, width - 100, height - 150, null)
            g.dispose()
            poster.icon = ImageIcon(combinedImage)
        }
    }

    companion object {

        fun loadImg(url: String): Image? {
            return runCatching {
                val img =
                    ImageIO.read(URL(url))
                img

            }.getOrNull()
        }
    }
}