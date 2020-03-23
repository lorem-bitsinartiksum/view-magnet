package web.controllers

import model.AdDTO
import model.AdsDTO
import domain.Ad.service.AdService
import io.javalin.Context

class AdController(private val adService: AdService) {

    fun create(ctx: Context) {
        ctx.validatedBody<AdDTO>()
            .check({ !it.ad?.title.isNullOrBlank() })
            .check({ !it.ad?.description.isNullOrBlank() })
            .getOrThrow().ad?.also { ad ->
            adService.create(ctx.attribute("email"), ad).apply {
                ctx.json(AdDTO(this))
            }
        }
    }

    fun delete(ctx: Context) {
        ctx.validatedPathParam("id").getOrThrow().also { id ->
            adService.delete(ctx.attribute("email"), id)
        }
    }

    fun update(ctx: Context) {
        val id = ctx.validatedPathParam("id").getOrThrow()
        ctx.validatedBody<AdDTO>()
            .check({ !it.ad?.title.isNullOrBlank() })
            .getOrThrow().ad?.also { ad ->
            adService.update(ctx.attribute("email"),id, ad).apply {
                ctx.json(AdDTO(this))
            }
        }
    }

    fun get(ctx: Context) {
        ctx.validatedPathParam("id")
            .check({ it.isNotBlank() })
            .getOrThrow().also { id ->
                adService.findById(ctx.attribute("email"),id).apply {
                    ctx.json(AdDTO(this))
                }
            }
    }

    fun findBy(ctx: Context) {
        val title = ctx.queryParam("title")
        val email = ctx.queryParam("email")
        val targetAge = ctx.queryParam("targetAge")
        val targetGender = ctx.queryParam("targetGender")
        val targetWeather = ctx.queryParam("targetWeather")
        adService.findBy(title, email, targetAge, targetGender, targetWeather).also { ads ->
            ctx.json(AdsDTO(ads, ads.size))
        }
    }

}