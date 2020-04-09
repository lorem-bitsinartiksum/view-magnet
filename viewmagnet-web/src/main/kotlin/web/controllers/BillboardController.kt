package web.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.websocket.WsSession
import lorem.bitsinartiksum.manager.HealthChecker

class BillboardController(private val jackson: ObjectMapper) {

    companion object {
        private val sessions = mutableListOf<WsSession>()
        private val healthChecker = HealthChecker()
    }

    init {
        healthChecker.subscribe {billboardId, status -> sessions.forEach { it.send(jackson.writeValueAsString(status)) } }
    }

    fun subscribe(session: WsSession) {
        sessions.add(session)
    }

    fun unsubscribe(session: WsSession) {
        sessions.remove(session)
    }
}