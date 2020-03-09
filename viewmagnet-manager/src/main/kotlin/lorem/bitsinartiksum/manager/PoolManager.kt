package lorem.bitsinartiksum.manager

import model.*
import repository.RepositoryService
import topic.TopicContext
import topic.TopicService
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


data class Billboard(
    val id: String,
    var pool: Set<Pair<Ad, Similarity>>,
    var interest: List<Float> = emptyList(),
    var counter: Int = 0
)

class PoolManager(config: Config) {

    private val billboards = mutableMapOf<String, Billboard>()

    private val adChangedTs =
        TopicService.createFor(AdChanged::class.java, "pool-manager", TopicContext(mode = config.mode))

    private val adPoolChangedTs = TopicService.createFor(AdPoolChanged::class.java, "pool-manager", TopicContext())

    private val repositoryService = RepositoryService.createFor(AdWithFeature::class.java)

    private val mode = config.mode

    private val maxPoolSize = config.maxPoolSize

    init {
        adChangedTs.subscribe {
            val billboardId = it.header.source
            //TODO: Log null returns
            val billboard = billboards[billboardId] ?: return@subscribe
            //TODO: countQR should be set to count of QR interactions
            val countQR = 0
            val interest = calcNewInterest(it.payload.ad.id, billboard, it.payload.detections.size, countQR)
            billboard.counter++
            billboard.interest = interest
        }
        startPoolUpdater()
    }

    private fun startPoolUpdater() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            billboards.forEach {
                updateBillboardPool(it.value)
                adPoolChangedTs.publish(AdPoolChanged(it.value.pool), TopicContext(individual = it.key))
            }
        }, 0, 10, TimeUnit.SECONDS)
    }

    private fun calcNewInterest(adId: String, billboard: Billboard, detectionsLength: Int, countQR: Int): List<Float> {
        return when (mode) {
            Mode.SIM -> {
                val ad = repositoryService.findById(adId) ?: return emptyList()
                val adFeature = ad.feature
                val interest = billboard.interest
                val counter = billboard.counter
                val ratio = countQR / detectionsLength
                val factor = ratio / countQR
                val newInterest = adFeature
                    .map { factor * it }
                    .mapIndexed { i, feature -> feature + interest[i] * ((counter - 1) / counter) }
                newInterest
            }
            Mode.REAL -> {
                //TODO; handlethis
                return emptyList()
            }
        }
    }

    private fun updateBillboardPool(billboard: Billboard) {

        val iteratorAds = repositoryService.findAll()

        val similarities = TreeSet<Pair<AdWithFeature, Similarity>> { ad1, ad2 ->
            if (ad1.second > ad2.second) 1 else -1
        }

        iteratorAds.forEach { ad -> similarities.add(ad to cosineSimilarity(ad.feature, billboard.interest)) }

        val newPool = similarities.descendingIterator().asSequence().take(maxPoolSize).map {
            val adWithFeature = it.first
            val sim = it.second
            Ad(adWithFeature.id, adWithFeature.content) to sim
        }.toCollection(mutableSetOf())

        billboard.pool = newPool
    }

    private fun cosineSimilarity(vec1: List<Float>, vec2: List<Float>): Float {
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in vec1.indices) {
            dotProduct += vec1[i] * vec2[i]
            normA += Math.pow(vec1[i].toDouble(), 2.0)
            normB += Math.pow(vec2[i].toDouble(), 2.0)
        }
        return (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))).toFloat()
    }

}