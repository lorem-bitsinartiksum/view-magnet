package lorem.bitsinartiksum.ad

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
    val defaultImg: Image,
    width: Int,
    height: Int,
    QRImage: BufferedImage
) {
    private val frame = JFrame("AdView")
    private var img = JLabel(ImageIcon(defaultImg.getScaledInstance(width, height, Image.SCALE_SMOOTH)))

    init {
        frame.layout = FlowLayout()
        frame.setSize(width, height)

        val combinedImage = BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        )
        val g: Graphics2D = combinedImage.createGraphics()
        g.drawImage(defaultImg, 0, 0, null)
        g.drawImage(QRImage, width - 100, height - 150, null)
        g.dispose()
        val combined = JLabel(ImageIcon(combinedImage))
        frame.add(combined)

        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                img.icon =
                    ImageIcon(defaultImg.getScaledInstance(e.component.width, e.component.height, Image.SCALE_SMOOTH))
            }
        })
    }

    fun show() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
    }

    fun changeAd(newImg: Image, duration: Duration) {
        EventQueue.invokeLater {
            img.icon = ImageIcon(newImg.getScaledInstance(frame.width, frame.height, Image.SCALE_SMOOTH))
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