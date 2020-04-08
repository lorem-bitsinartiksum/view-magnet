package lorem.bitsinartiksum.datagen

import lorem.bitsinartiksum.Config
import lorem.bitsinartiksum.ad.weatherInfo
import topic.TopicContext
import topic.TopicService
import java.awt.FlowLayout
import javax.swing.JFrame

fun main() {
    val datagen = DatagenApp(Config())
    datagen.show()
}

class DatagenApp(cfg: Config) {

    private val frame = JFrame("Data Generator - ${cfg.id}")
    private val weatherTs = TopicService.createFor(weatherInfo::class.java, "billboard-${cfg.id}", TopicContext())

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = FlowLayout()

        frame.contentPane.add(Form.weather {
            weatherTs.publish(it)
        })

        frame.pack()
    }

    fun show() {
        frame.isVisible = true
    }
}