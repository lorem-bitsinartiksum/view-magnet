package status

import model.BillboardEnvironment
import model.BillboardStatus
import model.Health
import model.Weather

class StatusProvider {

    private val mapBillboardStatus = mutableMapOf<String, BillboardStatus>()

    fun handleStatus(billboardId: String, billboardStatus: BillboardStatus) {
        mapBillboardStatus.put(billboardId, billboardStatus)
    }

    fun getStatus(billboardId: String): BillboardStatus {
        val status = mapBillboardStatus.getOrDefault(billboardId, BillboardStatus(Health.DOWN, "0", BillboardEnvironment(Weather.UNKNOWN,0,0)))
        return status
    }

}