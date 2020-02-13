package listener

import model.BillboardStatus

typealias StatusHandler = (BillboardStatus) -> Unit

class StatusListener {

    fun register(handler: StatusHandler) {
        TODO("NOT IMPLEMENTED")
    }

    fun unregister(handler: StatusHandler) {
        TODO("NOT IMPLEMENTED")
    }

}