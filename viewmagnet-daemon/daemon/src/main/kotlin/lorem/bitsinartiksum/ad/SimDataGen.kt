package lorem.bitsinartiksum.ad

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import model.Age
import model.BillboardEnvironment
import model.Gender
import model.Person
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicReference

object SimDataGen {

    var period = Duration.ofSeconds(1)
    var genderProb = 0.5
    var personProb = 1.0
    var ageProb = {
        val m = mutableMapOf<Age, Double>()
        val ages = Age.values()
        m.putAll(ages.map { it to 1.0 / ages.size })
        m
    }()
    val envRef = AtomicReference<BillboardEnvironment>()

    fun environment() = flow {
        while (true) {
            val env = envRef.get()
            if (env != null)
                emit(envRef.get())
            delay(period.toMillis())
        }
    }

    fun people() = flow {
        while (true) {
            if (flipCoin(personProb))
                emit(randPerson())
            delay(1000)
        }
    }

    private fun randPerson(): Person {
        val gender = if (flipCoin(genderProb)) Gender.MAN else Gender.WOMAN
        val age = (randAge@{
            var accWeight = 0.0
            val totalWeight = ageProb.values.sum()
            val randWeight = ThreadLocalRandom.current().nextDouble(0.0, totalWeight)
            for ((age, weight) in ageProb.entries) {
                accWeight += weight
                if (accWeight >= randWeight)
                    return@randAge age
            }
            Age.ADULT
        })()
        return Person(gender, age)
    }

    private fun flipCoin(prob: Double) = ThreadLocalRandom.current().nextDouble(0.0, 1.0) < prob
}
