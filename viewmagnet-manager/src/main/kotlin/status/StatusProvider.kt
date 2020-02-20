package status

import model.BillboardEnvironment
import model.BillboardStatus
import model.Health
import model.Weather
import kotlin.concurrent.timer

object StatusProvider {

    private val mapBillboardStatus = mutableMapOf<String, BillboardStatus>()

    private val timeoutValue = 10000.toLong()

    private val timerPeriod = 500.toLong()

    private val mapTimeout = mutableMapOf<String, Long>()

    private val defaultBillboardStatus = BillboardStatus(Health.DOWN, "0", BillboardEnvironment(Weather.UNKNOWN,0,0))

    fun handleStatus(billboardId: String, billboardStatus: BillboardStatus) {
        mapBillboardStatus[billboardId] = billboardStatus
        mapTimeout[billboardId] = timeoutValue
    }

    fun getStatus(billboardId: String): BillboardStatus {
        val status = mapBillboardStatus.getOrDefault(billboardId, defaultBillboardStatus)
        return status
    }

    fun statusTimer() {
        timer("statusTimer",true, 0.toLong(), timerPeriod) {
            mapTimeout.keys.forEach {
                mapTimeout[it] = mapTimeout.getValue(it).toLong() - timerPeriod
                if (mapTimeout.getValue(it) == 0.toLong()) {
                    mapBillboardStatus[it] = defaultBillboardStatus
                    mapTimeout.remove(it)
                }
            }
        }
    }

}