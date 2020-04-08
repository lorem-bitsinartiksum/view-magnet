package lorem.bitsinartiksum.ad

import com.google.common.flogger.FluentLogger
import lorem.bitsinartiksum.CommandHandler
import lorem.bitsinartiksum.Config
import lorem.bitsinartiksum.Detection
import model.Ad
import model.AdChanged
import model.Similarity
import topic.TopicContext
import topic.TopicService
import java.time.Duration
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.timer
import kotlin.concurrent.write

typealias AdPool = Set<Pair<Ad, Similarity>>


class AdManager(private val updateDisplay: (Ad, Duration) -> Unit, val cfg: Config) : CommandHandler {
    private val logger = FluentLogger.forEnclosingClass()
    private val adChangedTs = TopicService.createFor(AdChanged::class.java, cfg.id, TopicContext())
    private var rollStartTime = System.currentTimeMillis()
    private val rwLock = ReentrantReadWriteLock()
    var pool: AdPool = setOf()
        private set

    private var schedule = Schedule(pool.toList(), cfg.window)
    private val highPriorityAds: Queue<Ad> = LinkedList()

    var currentAd: Ad = Ad("0,0,0", "0,0,0")
        private set(newAd) {
            if (newAd == field) return
            val durationMs = System.currentTimeMillis() - rollStartTime
            rollStartTime = System.currentTimeMillis()
            adChangedTs.publish(AdChanged(field, durationMs, listOf()))
            field = newAd
            updateDisplay(newAd, Duration.ofMillis(durationMs))
        }

    override fun showRelatedAd(detection: Detection) {
        TODO("Not yet implemented")
    }

    override fun showAd(ad: Ad) {
        highPriorityAds.add(ad)
    }

    override fun changePool(newPool: AdPool) {
        rwLock.write {
            pool = newPool
            schedule = Schedule(pool.toList(), cfg.window)
            logger.atInfo().log("Refreshing Pool New Pool: $newPool")
        }
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    fun start() {
        timer("ad-scheduler", false, 0, cfg.period.toMillis()) {
            rwLock.read {
                currentAd = if (highPriorityAds.isNotEmpty())
                    highPriorityAds.poll()
                else
                    schedule.next() ?: currentAd
            }
        }
    }

}