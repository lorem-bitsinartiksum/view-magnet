package lorem.bitsinartiksum.ad

import model.Ad
import model.AdPoolChanged

class AdManager {

    val currentAd: Ad? = null

    fun refreshPool(newPool: Set<String>) {

        println("REFRESHING POOL")
    }

    inline fun <reified T> handleCommand(cmd: T) {
        when (T::class.java) {
            AdPoolChanged::class.java -> refreshPool((cmd as AdPoolChanged).newPool)
        }
    }

}