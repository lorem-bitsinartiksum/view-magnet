package lorem.bitsinartiksum

import java.time.Duration


data class Config(
    val id: String = "2343:23456",
    val period: Duration = Duration.ofSeconds(2),
    val window: Duration = Duration.ofSeconds(10)
)