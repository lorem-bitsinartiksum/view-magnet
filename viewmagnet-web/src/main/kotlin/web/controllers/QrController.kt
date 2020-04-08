package web.controllers

import io.javalin.Context
import domain.Qr.service.QrService
import model.Mode

class QrController(private val qrService: QrService) {
    fun increaseInteraction(ctx: Context) {
        val billboardId = ctx.queryParam("billboard")
        val adId = ctx.queryParam("ad")
        val mode = ctx.queryParam("mode")
        if(mode == "real"){
            qrService.increaseInteraction(Mode.REAL, billboardId, adId).apply {
                ctx.redirect(this.toString())
                //ctx.redirect("https://javalin.io/documentation#context")
            }
        }
        else if(mode == "sim"){
            qrService.increaseInteraction(Mode.SIM, billboardId, adId).apply {
                ctx.redirect(this.toString())
                //ctx.redirect("https://javalin.io/documentation#context")
            }
        }
    }
}