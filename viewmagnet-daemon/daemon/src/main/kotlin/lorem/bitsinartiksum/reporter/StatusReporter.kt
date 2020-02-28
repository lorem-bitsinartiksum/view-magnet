package lorem.bitsinartiksum.reporter

import kotlinx.coroutines.channels.ReceiveChannel
import lorem.bitsinartiksum.config.Config
import model.BillboardEnvironment
import model.BillboardStatus
import model.Health
import model.Weather
import topic.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


class StatusReporter(val cfg: Config) {

    private val ts = TopicService.createFor(
        BillboardStatus::class.java, "billboard-${cfg.id}",
        TopicContext(Country.TR, City.IST, District.KECIOREN, cfg.id)
    )

    private val timer = Timer("status-reporter")
    private var currentEnv = BillboardEnvironment(Weather.CLOUDY, Int.MIN_VALUE, Int.MIN_VALUE)
    private var currentAdId = "#NULL#"

    suspend fun start(envUpdates: ReceiveChannel<BillboardEnvironment>, adUpdates: ReceiveChannel<String>) {

        timer.scheduleAtFixedRate(0, cfg.periodMs) {
            ts.publish(BillboardStatus(Health.UP, currentAdId, currentEnv))
        }

        for (env in envUpdates) {
            currentEnv = env
        }

        for (id in adUpdates) {
            currentAdId = id
        }
    }
}