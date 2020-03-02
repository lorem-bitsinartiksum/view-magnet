package lorem.bitsinartiksum.manager


import model.BillboardStatus
import model.Health
import topic.TopicContext
import topic.TopicService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class HealthChecker {

    private val lastReceived = mutableMapOf<String, Pair<Long, BillboardStatus>>()
    private val ts = TopicService.createFor(BillboardStatus::class.java, "ad-manager", TopicContext())

    init {
        ts.subscribe {
            lastReceived[it.header.source] = System.currentTimeMillis() to it.payload
        }
        startChecker()
    }

    private fun startChecker() {

        val isBillboardDown =
            { lastRecMs: Long -> lastRecMs < (System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(10)) }

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            lastReceived
                .filter { isBillboardDown(it.value.first) }
                .forEach { ts.publish(it.value.second.copy(Health.DOWN)) }
        }, 0, 5, TimeUnit.SECONDS)
    }
}