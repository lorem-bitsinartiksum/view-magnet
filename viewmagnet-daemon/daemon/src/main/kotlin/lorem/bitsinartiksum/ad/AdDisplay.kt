package lorem.bitsinartiksum.ad

import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

class AdDisplay(val defaultImg: Image, width: Int, height: Int) : JPanel() {

    private val frame = JFrame()
    private var img = JLabel(ImageIcon(defaultImg.getScaledInstance(width, height, Image.SCALE_SMOOTH)))
    private var color = Color.BLACK
    private val isSim = true || System.getProperty("mode", "real").equals("sim", true)

    init {

        if (!isSim) {
            frame.layout = FlowLayout()
            frame.add(img)
        }

        frame.setSize(width, height)

        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                frame.repaint()
                repaint()
                img.icon =
                    ImageIcon(defaultImg.getScaledInstance(e.component.width, e.component.height, Image.SCALE_SMOOTH))
            }
        })
        frame.add(this)
    }

    override fun show(b: Boolean) {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
    }

    override fun show() {
        this.show(true)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (isSim) {
            val g2 = g as Graphics2D
            g2.color = color
            g2.fill(Rectangle(0, 0, frame.width, frame.height))
        }
    }

    fun changeAd(newImg: Image) {
        EventQueue.invokeLater {
            img.icon = ImageIcon(newImg.getScaledInstance(frame.width, frame.height, Image.SCALE_SMOOTH))
        }
    }

    fun changeAd(newColor: Color) {
        color = newColor
        frame.repaint()
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