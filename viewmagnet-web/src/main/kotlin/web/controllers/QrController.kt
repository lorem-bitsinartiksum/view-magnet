package web.controllers

import io.javalin.Context
import domain.Qr.service.QrService

class QrController(private val qrService: QrService) {
    fun increaseInteraction(ctx: Context) {
        val billboardId = ctx.queryParam("billboard")
        val adId = ctx.queryParam("ad")
        qrService.increaseInteraction(billboardId, adId).apply {
            ctx.json("link")
        }
    }
}