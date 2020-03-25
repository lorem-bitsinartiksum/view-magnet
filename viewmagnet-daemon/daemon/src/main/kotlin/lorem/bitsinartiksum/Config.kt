package lorem.bitsinartiksum

import model.Mode
import java.time.Duration


data class Config(
    val id: String = System.getProperty("id", "2343:23456"),
    val period: Duration = Duration.ofSeconds(System.getProperty("period", "2").toLong()),
    val interest: List<Float> = System.getProperty("interest", "0.0, 0.0, 0.0")
        .replace("\\s", "")
        .split(",")
        .map { it.toFloat() },
    val mode: Mode = Mode.valueOf(System.getProperty("mode", "real").toUpperCase())
)