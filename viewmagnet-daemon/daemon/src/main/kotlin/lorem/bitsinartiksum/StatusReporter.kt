package lorem.bitsinartiksum

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.select
import model.BillboardEnvironment
import model.BillboardStatus
import model.Health
import model.Weather
import topic.TopicContext
import topic.TopicService
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


class StatusReporter(val cfg: Config) {

    private val ts = TopicService.createFor(
        BillboardStatus::class.java, "billboard-${cfg.id}",
        TopicContext()
    )

    private val timer = Timer("status-reporter")
    private var currentEnv = BillboardEnvironment(Weather.CLOUDS, Int.MIN_VALUE, Int.MIN_VALUE)
    private var currentAdId = "#NULL#"

    suspend fun start(envUpdates: ReceiveChannel<BillboardEnvironment>, adUpdates: ReceiveChannel<String>) {

        timer.scheduleAtFixedRate(0, cfg.period.toMillis()) {
            ts.publish(BillboardStatus(Health.UP, currentAdId, currentEnv))
        }

        while (true) {
            select<Unit> {
                envUpdates.onReceive {
                    currentEnv = it
                }
                adUpdates.onReceive {
                    currentAdId = it
                }
            }
        }
    }
}