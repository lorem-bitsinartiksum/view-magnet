package lorem.bitsinartiksum

import lorem.bitsinartiksum.ad.AdPool
import model.Ad

interface CommandHandler {

    fun showRelatedAd(detection: Detection)

    fun showAd(ad: Ad)

    fun changePool(newPool: AdPool)

    fun shutdown()
}