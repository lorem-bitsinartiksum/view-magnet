package web.controllers

import io.javalin.Context
import domain.Qr.service.QrService
import model.Mode

class QrController(private val qrService: QrService) {
    fun increaseInteraction(ctx: Context) {
        val billboardId = ctx.queryParam("billboard")
        val adId = ctx.queryParam("ad")
        val mode = ctx.queryParam("mod")
        if(mode == "real"){
            qrService.increaseInteraction(Mode.REAL, billboardId, adId).apply {
                ctx.redirect(this.toString())
            }
        }
        else if(mode == "sim"){
            qrService.increaseInteraction(Mode.SIM, billboardId, adId).apply {
                ctx.redirect(this.toString())
            }
        }
    }
}