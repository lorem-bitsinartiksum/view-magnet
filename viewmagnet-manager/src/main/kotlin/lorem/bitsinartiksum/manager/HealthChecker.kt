package lorem.bitsinartiksum.manager


import model.BillboardStatus
import model.Health
import topic.TopicContext
import topic.TopicService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

typealias StatusHandler = (BillboardStatus) -> Unit

class HealthChecker {

    private val lastReceived = mutableMapOf<String, Pair<Long, BillboardStatus>>()
    private val ts = TopicService.createFor(BillboardStatus::class.java, "ad-manager", TopicContext())
    private val subbers = mutableListOf<StatusHandler>()

    init {
        ts.subscribe { topic ->
            val status = topic.payload
            lastReceived[topic.header.source] = System.currentTimeMillis() to status
            subbers.forEach { it(status) }
        }
        startChecker()
    }

    fun subscribe(handler: StatusHandler) {
        subbers.add(handler)
    }

    private fun startChecker() {

        val isBillboardDown =
            { lastRecMs: Long -> lastRecMs < (System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(10)) }

        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            lastReceived
                .filter { isBillboardDown(it.value.first) }
                .forEach {
                    val newStatus = it.value.second.copy(Health.DOWN)
                    ts.publish(newStatus)
                    subbers.forEach { handler -> handler(newStatus) }
                }
        }, 0, 5, TimeUnit.SECONDS)
    }
}