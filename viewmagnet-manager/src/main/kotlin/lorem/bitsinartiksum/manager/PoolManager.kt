package lorem.bitsinartiksum.manager

import com.google.common.flogger.FluentLogger
import model.*
import repository.RepositoryService
import topic.TopicContext
import topic.TopicService
import java.lang.Math.pow
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private val Double.isZero: Boolean
    get() {
        return this in -0.001..0.001
    }

data class Billboard(
    var pool: Set<Pair<Ad, Similarity>> = emptySet(),
    var interest: List<Float> = listOf(1f, 0f, 0f, 0f, 1f, 1f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f),
    var counter: Int = 0
)

class PoolManager(private val cfg: Config, hc: HealthChecker) {

    private val billboards = mutableMapOf<String, Billboard>()

    private val adChangedTs =
        TopicService.createFor(AdChanged::class.java, "pool-manager", TopicContext(mode = cfg.mode))

    private val adPoolChangedTs = TopicService.createFor(AdPoolChanged::class.java, "pool-manager", TopicContext())

    private val logger = FluentLogger.forEnclosingClass()

    private val repositoryServiceAd = RepositoryService.createFor(AdWithFeature::class.java, cfg.mode)

    private val repositoryServiceQR = RepositoryService.createFor(QR::class.java, cfg.mode)

    init {
        hc.subscribe { billboardId, newStatus ->
            if (billboards.contains(billboardId) && newStatus.health == Health.DOWN) {
                logger.atFine().log("Billboard $billboardId is down.")
                billboards.remove(billboardId)
            } else if (!billboards.contains(billboardId) && newStatus.health == Health.UP) {
                logger.atFine().log("New Billboard [$billboardId] detected")
                billboards[billboardId] = Billboard()
            }
        }
        adChangedTs.subscribe { (payload, header) ->
            val billboardId = header.source
            val billboard = billboards[billboardId] ?: return@subscribe
            val adId = payload.ad.id
            val qr = repositoryServiceQR.find { qr ->
                qr.billboardId == billboardId &&
                        qr.adId == adId
            }
            val adStartTime = header.createdAt - payload.durationMs
            val adEndTime = header.createdAt
            val countQR = qr?.interactionTimes?.map { it.toInt() }?.filter { it in adStartTime..adEndTime }?.sum() ?: 0

            val interest =
                if (billboard.counter > 1 && payload.detections.isNotEmpty())
                    calcNewInterest(adId, billboard, payload.detections.size, countQR)
                else
                    billboard.interest

            billboard.counter++
            billboard.interest = interest
        }
        startPoolUpdater()
    }

    fun changeInterest(billboardId: String, newInterest: List<Float>) {
        billboards[billboardId]?.interest = newInterest
    }

    fun saveBillboard(billboardId: String, interest: List<Float>): Billboard? {
        if (billboards.containsKey(billboardId))
            return null
        val bb = Billboard(interest = interest)
        billboards[billboardId] = bb
        return bb
    }

    private fun startPoolUpdater() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            billboards.forEach {
                updateBillboardPool(it.value)
                adPoolChangedTs.publish(AdPoolChanged(it.value.pool), TopicContext(individual = it.key))
            }
        }, 0, cfg.poolUpdatePeriodSec, TimeUnit.SECONDS)
    }

    fun calcNewInterest(adId: String, billboard: Billboard, detectionsLength: Int, countQR: Int): List<Float> {
        val ad = repositoryServiceAd.find { adWithFeature -> adWithFeature.id == adId }

        if (ad == null) {
            logger.atWarning().log("AdWithFeature not found")
            return billboard.interest
        }
        val adFeature = ad.feature
        val interest = billboard.interest
        val counter = billboard.counter
        val ratio = countQR / detectionsLength.toFloat()
        val factor = ratio / counter
        val newInterest = adFeature
            .map { factor * it }
            .mapIndexed { i, feature -> feature + interest[i] * ((counter - 1) / counter.toFloat()) }
        if (newInterest.size != 11 || newInterest.any { it.isNaN() }) {
            logger.atSevere().log("New interest is wrong!")
            return billboard.interest
        }
        return newInterest
    }

    fun updateBillboardPool(billboard: Billboard) {

        val iteratorAds = repositoryServiceAd.findAll().asSequence().toList()

        val similarities = TreeSet<Pair<AdWithFeature, Similarity>> { ad1, ad2 ->
            if (ad1.second > ad2.second) 1 else -1
        }

        iteratorAds.forEach { ad ->

            val sim = cosineSimilarity(ad.feature, billboard.interest) ?: {
                logger.atSevere()
                    .log(
                        "Inconsistent feature & interest vector: Ad: ${ad.id} has ${ad.feature.size} " +
                                "has elems but billboard has ${billboard.interest.size}"
                    )
                0f
            }()
            logger.atSevere().log("Sim between ${ad.title}  -> ${sim}")
            if (sim >= cfg.similarityThreshold) {
                similarities.add(ad to sim)
            }
        }

        val newPool = similarities.descendingIterator().asSequence().take(cfg.maxPoolSize).map { (ad, sim) ->
            Ad(ad.id, ad.content) to sim
        }.toCollection(mutableSetOf())

        billboard.pool = newPool
    }

    private fun cosineSimilarity(vec1: List<Float>, vec2: List<Float>): Float? {
        if (vec1.size != vec2.size) return null

        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0
        // TODO FIX THIS COEFF SHIT
        vec1.zip(vec2).forEachIndexed { i, (e1, e2) ->
            val ee1 = (if (i == 0) 0.6 else 0.4) * e1
            val ee2 = (if (i == 0) 0.6 else 0.4) * e2
            dotProduct += ee1 * ee2
            normA += pow(ee1.toDouble(), 2.0)
            normB += pow(ee2.toDouble(), 2.0)
        }
        val divider = (Math.sqrt(normA) * Math.sqrt(normB))
        return if (divider.isZero) 0f else (dotProduct / divider).toFloat()
    }

}