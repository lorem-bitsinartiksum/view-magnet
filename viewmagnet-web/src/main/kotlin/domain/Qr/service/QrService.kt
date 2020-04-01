package domain.Qr.service

import domain.Ad.repository.AdRepository
import io.javalin.BadRequestResponse
import model.*
import repository.RepositoryService.Companion.createFor
import java.util.*

private val repositoryService = createFor(QR::class.java)

class QrService(private val adRepository: AdRepository)  {
    fun increaseInteraction(billboardId: String?, adId: String?) : String? {
        if (billboardId.isNullOrEmpty()){
            throw BadRequestResponse("invalid billboardId") as Throwable
        }
        if (adId.isNullOrEmpty()){
            throw BadRequestResponse("invalid adId") as Throwable
        }

        val qr = repositoryService.find { Qr ->  Qr.billboardId == billboardId && Qr.adId == adId}

        if(qr == null){
            val interactionList = ArrayList<String>()
            val now = Date()
            interactionList.add(now.time.toString())
            repositoryService.save(QR(id = UUID.randomUUID().toString(), billboardId=billboardId , adId = adId, interactionTimes =  interactionList))
            val ad = adRepository.findById(adId)
            return ad?.description
        }
        else{
            val interactionList = qr.interactionTimes
            val now = Date()
            interactionList.add(now.time.toString())
            repositoryService.deleteById(qr.id)
            repositoryService.save(QR(id = UUID.randomUUID().toString(), billboardId=billboardId , adId = adId, interactionTimes = interactionList))
            val ad = adRepository.findById(adId)
            return ad?.description
        }
    }
}
