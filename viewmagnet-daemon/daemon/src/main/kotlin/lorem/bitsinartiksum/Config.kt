package lorem.bitsinartiksum

import java.time.Duration


data class Config(
    val id: String = System.getProperty("id", "2343:23456"),
    val period: Duration = Duration.ofSeconds(System.getProperty("period", "2").toLong()),
    val interest: List<Float> = System.getProperty("interest", "0.0, 0.0, 0.0")
        .replace("\\s", "")
        .split(",")
        .map { it.toFloat() }
)