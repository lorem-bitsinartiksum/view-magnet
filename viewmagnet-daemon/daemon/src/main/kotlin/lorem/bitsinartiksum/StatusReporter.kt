package lorem.bitsinartiksum

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.select
import model.*
import topic.TopicContext
import topic.TopicService
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


private val String.asLocation: Location
    get() {
        val parts = this.split(",")
        if (parts.size != 2)
            return Location(0.0, 0.0)
        val (lat, lon) = parts
        return Location(lat.toDouble(), lon.toDouble())
    }

class StatusReporter(val cfg: Config) {

    private val ts = TopicService.createFor(
        BillboardStatus::class.java, "billboard-${cfg.id}",
        TopicContext()
    )

    private val timer = Timer("status-reporter")
    private var currentEnv = BillboardEnvironment(Weather.CLOUDS, Float.MIN_VALUE, Float.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Int.MIN_VALUE,"country",Float.MIN_VALUE)
    private var currentAdId = "#NULL#"

    suspend fun start(envUpdates: ReceiveChannel<BillboardEnvironment>, adUpdates: ReceiveChannel<String>) {

        timer.scheduleAtFixedRate(0, cfg.period.toMillis()) {
            ts.publish(BillboardStatus(cfg.id, cfg.id.asLocation, Health.UP, currentAdId, currentEnv))
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