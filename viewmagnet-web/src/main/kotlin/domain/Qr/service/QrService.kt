package domain.Qr.service

import io.javalin.BadRequestResponse
import model.*
import repository.RepositoryService.Companion.createFor
import java.util.*

private val repositoryService = createFor(QR::class.java)

class QrService() {
    fun increaseInteraction(billboardId: String?, adId: String?) {
        if (billboardId.isNullOrEmpty()){
            throw BadRequestResponse("invalid billboardId") as Throwable
        }
        if (adId.isNullOrEmpty()){
            throw BadRequestResponse("invalid adId") as Throwable
        }

        val qr =repositoryService.find { Qr ->  Qr.billboardId == billboardId}

        if(qr == null){
            val now = Date()
            val interactionList = ArrayList<String>()
            interactionList.add(now.time.toString())
            repositoryService.save(QR(id = UUID.randomUUID().toString(), billboardId=billboardId , adId = adId, interactionTimes =  interactionList))
        }
        else{
            val now = Date()
            val interactionList = qr.interactionTimes
            interactionList.add(now.time.toString())
            repositoryService.deleteById(qr.id)
            repositoryService.save(QR(id = UUID.randomUUID().toString(), billboardId=billboardId , adId = adId, interactionTimes = interactionList))
        }
    }
}
