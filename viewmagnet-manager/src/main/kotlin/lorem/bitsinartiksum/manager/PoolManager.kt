package lorem.bitsinartiksum.manager

import model.*
import repository.RepositoryService
import topic.TopicContext
import topic.TopicService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

data class Billboard(val id: String, var pool: Set<Pair<Ad, Similarity>>, var interest: List<Float> = emptyList())

class PoolManager(config: Config) {

    private val billboards = mutableMapOf<String, Billboard>()

    private val adChangedTs = TopicService.createFor(AdChanged::class.java, "pool-manager", TopicContext(mode = config.mode))

    private val adPoolChangedTs = TopicService.createFor(AdPoolChanged::class.java, "pool-manager", TopicContext())

    private val repositoryService = RepositoryService.createFor(AdWithFeature::class.java)

    init {
        adChangedTs.subscribe {
            val billboardId = it.header.source
            val featureVec = findFeatureVec(it.payload.ad.id, it.payload.detections, config.mode)
            updateBillboardPool(billboardId, featureVec)
        }

        startPoolUpdater()
    }

    private fun startPoolUpdater() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
            billboards.keys.forEach {
                adPoolChangedTs.publish(AdPoolChanged(billboards.get(it)!!.pool), TopicContext(individual = it))
            }
        }, 0, 5, TimeUnit.SECONDS)
    }

    private fun findFeatureVec(adId: String, detections: List<Person>, mode: Mode): List<Float> {
        val vector = mutableListOf<Float>()
        val iteratorAds = repositoryService.findAll()
        when(mode) {
            Mode.SIM -> {
                while(iteratorAds.hasNext()) {
                    val ad = iteratorAds.next()
                    //TODO; set vector
                }
            }
            Mode.REAL -> {
                while(iteratorAds.hasNext()) {
                    val ad = iteratorAds.next()
                    //TODO; set vector
                }
            }
        }
        return vector
    }

    fun updateBillboardPool(billboardId: String, feature: List<Float>) {
        val newPool = mutableSetOf<Pair<Ad, Similarity>>()
        billboards.get(billboardId)!!.interest = feature
        //TODO: set new pool
        billboards.get(billboardId)!!.pool = newPool
    }

}