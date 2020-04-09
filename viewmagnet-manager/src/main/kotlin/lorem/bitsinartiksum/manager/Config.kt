package lorem.bitsinartiksum.manager

import model.Mode

data class Config(
    val mode: Mode = Mode.valueOf(System.getProperty("mode", "real").toUpperCase()),
    val similarityThreshold: Float = System.getProperty("threshold", "0.2").toFloat(),
    val maxPoolSize: Int = System.getProperty("max_pool", "20").toInt(),
    val poolUpdatePeriodSec: Long = System.getProperty("periodSec", "15").toLong()
)