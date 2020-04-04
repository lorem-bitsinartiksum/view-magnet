package lorem.bitsinartiksum.manager.sim

import model.AdWithFeature
import repository.RepositoryService

class AdService {

    private val rs = RepositoryService.createFor(AdWithFeature::class.java)

    fun addNewAd(color: List<Float>) {
        val formattedClr = color.joinToString(",")
        rs.save(AdWithFeature(formattedClr, formattedClr, feature = color))
    }
}