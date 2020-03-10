package lorem.bitsinartiksum.ad

import model.Ad
import model.Similarity
import java.time.Duration
import kotlin.math.roundToLong

data class AdTrack(val ad: Ad, val similarity: Similarity, var remainingMs: Long)

class Schedule(ads: List<Pair<Ad, Similarity>>, window: Duration) {

    private var fetchedAt = -1L

    private var idx = 0
    private var ads = {
        val totalSim = ads.fold(0f) { total, ad -> total + ad.second }
        ads.map { (ad, sim) -> AdTrack(ad, sim, (window.toMillis() * sim / totalSim).roundToLong()) }.toMutableList()
    }()

    fun next(): Ad? {
        return runCatching {
            ads[idx % ads.size].ad
        }.onSuccess {
            if (idx > 0) {
                val prevAd = ads.getOrNull((idx - 1) % ads.size)
                if (prevAd != null) {
                    prevAd.remainingMs -= System.currentTimeMillis() - fetchedAt
                    if (prevAd.remainingMs <= 0)
                        ads.remove(prevAd)
                }
            }
            idx++
            fetchedAt = System.currentTimeMillis()
        }.getOrNull()
    }
}

