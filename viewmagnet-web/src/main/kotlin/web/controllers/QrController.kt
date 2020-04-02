package web.controllers

import io.javalin.Context
import domain.Qr.service.QrService

class QrController(private val qrService: QrService) {
    fun increaseInteraction(ctx: Context) {
        val billboardId = ctx.queryParam("billboard")
        val adId = ctx.queryParam("ad")
        qrService.increaseInteraction(billboardId, adId).apply {
            ctx.redirect(this.toString())
            //ctx.redirect("https://javalin.io/documentation#context")
        }
    }
}