package lorem.bitsinartiksum

import model.Ad
import model.AdPoolChanged
import model.Mode
import topic.TopicContext
import topic.TopicService
import java.time.Duration

fun main() {
    val id = "billboard-39.9035557,32.622682"
    val poolChange = TopicService.createFor(AdPoolChanged::class.java, "vikvik", TopicContext())
    poolChange.publish(AdPoolChanged(newPool = setOf(Ad("", "") to 0.5f)), TopicContext(individual = id))
}

fun initialInterest(): List<Float> {
    val given = System.getProperty("interest", "NOT_GIVEN")
    if (given == "NOT_GIVEN") {
        return generateSequence { 0.5f }.take(11).toList()
    }
    val interest = given.replace("\\s", "")
        .split(",")
        .map { it.toFloat() }
    if (interest.size != 11) {
        error("INITIAL INTEREST SIZE DOESNT MATCH")
    }
    return interest
}

data class Config(
    val id: String = System.getProperty("id", "billboard-39.9035557,32.622682"),
    val period: Duration = Duration.ofSeconds(System.getProperty("period", "2").toLong()),
    val window: Duration = Duration.ofSeconds(15),
    val mode: Mode = Mode.valueOf(System.getProperty("mode", "real").toUpperCase()),
    val interest: List<Float> = initialInterest()
)