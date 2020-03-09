package lorem.bitsinartiksum.reporter

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.select
import lorem.bitsinartiksum.config.Config
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
    private var currentEnv = BillboardEnvironment(Weather.CLOUDS, Float.MIN_VALUE, Float.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Int.MIN_VALUE,"country",Float.MIN_VALUE)
    private var currentAdId = "#NULL#"

    suspend fun start(envUpdates: ReceiveChannel<BillboardEnvironment>, adUpdates: ReceiveChannel<String>) {

        timer.scheduleAtFixedRate(0, cfg.periodMs) {
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