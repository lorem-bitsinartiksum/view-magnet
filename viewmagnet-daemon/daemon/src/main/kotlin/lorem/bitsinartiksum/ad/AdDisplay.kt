package lorem.bitsinartiksum.ad

import lorem.bitsinartiksum.QRGenerator
import model.BillboardEnvironment
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ItemEvent
import java.awt.image.BufferedImage
import java.net.URL
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.math.roundToLong

class AdDisplay(
    defaultImg: Image,
    private var width: Int,
    private var height: Int,
    overrideEnv: (BillboardEnvironment) -> Unit,
    toggleOverride: (Boolean) -> Unit,
    var changeToRelatedAd: (Detection) -> Unit
) {
    private val frame = JFrame("AdView")
    private var duration: Duration? = null
    private var img = defaultImg
    private var poster = JLabel(ImageIcon(defaultImg.getScaledInstance(width, height, Image.SCALE_SMOOTH)))

    init {
        frame.layout = BorderLayout()
        frame.setSize(width, height)

        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                poster.icon =
                    ImageIcon(img.getScaledInstance(e.component.width - 300, e.component.height, Image.SCALE_SMOOTH))
                width = e.component.width - 300
                height = e.component.height
            }
        })
        overlayQr("/home", true)

        val sidePanel = {
            val container = JPanel()
            container.preferredSize = Dimension(280, 300)

            val (envFormPanel, envSupplier) = Form.weather()
            val overrideCb = JCheckBox("Override Sensors")
            val setEnvBtn = JButton("SET ENV")
            setEnvBtn.isEnabled = false

            val cmdPanel = JPanel()
            cmdPanel.layout = FlowLayout()
            val detection = JComboBox(Detection.values())
            val showAdBtn = JButton("Show Related")
            showAdBtn.isEnabled = false

            val (probControlPanel, probSupplier) = Form.probControl()
            val setProbsBtn = JButton("SET SIM PARAMS")


            overrideCb.addItemListener {
                val isSelected = it.stateChange == ItemEvent.SELECTED
                toggleOverride(isSelected)
                setEnvBtn.isEnabled = isSelected
                showAdBtn.isEnabled = isSelected
            }

            setEnvBtn.addActionListener {
                val env = envSupplier()
                overrideEnv(env)
            }

            setProbsBtn.addActionListener {
                val (ages, others) = probSupplier()
                ages.filterValues { it != null }.forEach { SimDataGen.ageProb[it.key] = it.value!! }
                SimDataGen.period =
                    Duration.ofMillis(others["EnvDataSendPeriod"]?.roundToLong() ?: SimDataGen.period.toMillis())
                SimDataGen.genderProb = others["Gender=ManProb"] ?: SimDataGen.genderProb
                SimDataGen.personProb = others["PersonProb"] ?: SimDataGen.personProb
            }

            cmdPanel.add(detection)
            cmdPanel.add(showAdBtn)


            container.add(overrideCb, BorderLayout.NORTH)
            envFormPanel.add(setEnvBtn, BorderLayout.SOUTH)
            container.add(envFormPanel, BorderLayout.CENTER)
//            container.add(setEnvBtn, BorderLayout.SOUTH)
            container.add(cmdPanel)
            probControlPanel.add(setProbsBtn, BorderLayout.SOUTH)
            container.add(probControlPanel)

            showAdBtn.addActionListener {
                changeToRelatedAd(detection.selectedItem as Detection)
            }

            container
        }()

        frame.add(sidePanel, BorderLayout.EAST)
        frame.add(poster, BorderLayout.WEST)

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            EventQueue.invokeLater {
                frame.title = "${duration?.toSeconds() ?: "AdViewer"}s"
            }
            duration = duration?.plusSeconds(1)
        }, 0, 1, TimeUnit.SECONDS)
    }

    fun show() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
    }

    fun changeAd(newImg: Image, url: String, showingRelatedAd: Boolean) {
        EventQueue.invokeLater {
            img = newImg.getScaledInstance(width, height, Image.SCALE_SMOOTH)
            duration = Duration.ofMillis(0)
            overlayQr(url, showingRelatedAd)
        }
    }

    private fun overlayQr(url: String, showingRelatedAd: Boolean) {
        val (width, height) = Pair(frame.width, frame.height)
        val combinedImage = BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        )
        val g: Graphics2D = combinedImage.createGraphics()
        g.drawImage(img, 0, 0, null)
        if (!showingRelatedAd) {
            val qrImg = QRGenerator.generateQRCodeImage(url, 200, 200)
            g.drawImage(qrImg, width - 500, height - 250, null)
        }
        g.dispose()
        poster.icon = ImageIcon(combinedImage)

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