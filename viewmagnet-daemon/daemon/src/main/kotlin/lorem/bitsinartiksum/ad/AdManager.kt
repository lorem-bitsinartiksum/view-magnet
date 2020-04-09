package lorem.bitsinartiksum.ad

import com.google.common.flogger.FluentLogger
import lorem.bitsinartiksum.CommandHandler
import lorem.bitsinartiksum.Config
import model.Ad
import model.AdChanged
import model.Similarity
import topic.TopicContext
import topic.TopicService
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.timer
import kotlin.concurrent.write
import kotlin.system.exitProcess

typealias AdPool = Set<Pair<Ad, Similarity>>


class AdManager(private val updateDisplay: (Ad) -> Unit, val cfg: Config) : CommandHandler {
    private val logger = FluentLogger.forEnclosingClass()
    private val adChangedTs = TopicService.createFor(AdChanged::class.java, cfg.id, TopicContext(mode = cfg.mode))
    private var rollStartTime = System.currentTimeMillis()
    private val rwLock = ReentrantReadWriteLock()
    private val specialAds = Detection.extractAdsFromFolder()
    private var showingRelatedAd = false

    var pool: AdPool = setOf()
        private set

    private var schedule = Schedule(pool.toList(), cfg.window)
    private val highPriorityAds: Queue<Ad> = LinkedList()

    var currentAd: Ad = Ad("default-ad", "")
        private set(newAd) {
            if (newAd == field) return
            val durationMs = System.currentTimeMillis() - rollStartTime
            rollStartTime = System.currentTimeMillis()
            adChangedTs.publish(AdChanged(field, durationMs, listOf()))
            field = newAd
            updateDisplay(newAd)
        }

    override fun showRelatedAd(detection: Detection) {
        rwLock.write {
            // Billboard is already showing a related ad. So we should skip this one.
            if (showingRelatedAd) return@write
            currentAd = specialAds[detection]?.random() ?: currentAd
            showingRelatedAd = true
            // Stop blocking regular scheduler after showing it for half of the regular period
            CompletableFuture.delayedExecutor(cfg.period.toMillis() / 2, TimeUnit.MILLISECONDS).execute {
                showingRelatedAd = false
            }
        }

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
        logger.atFine().log("Exiting...")
        exitProcess(0)
    }

    fun start() {
        timer("ad-scheduler", false, 0, cfg.period.toMillis()) {
            if (showingRelatedAd) return@timer
            rwLock.read {
                currentAd = if (highPriorityAds.isNotEmpty())
                    highPriorityAds.poll()
                else
                    schedule.next() ?: currentAd
            }
        }
    }

}