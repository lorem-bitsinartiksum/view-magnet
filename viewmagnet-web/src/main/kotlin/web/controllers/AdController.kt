package web.controllers

import domain.Ad.AdDTO
import domain.Ad.AdsDTO
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
        ctx.validatedPathParam("slug").getOrThrow().also { slug ->
            adService.delete(ctx.attribute("email"), slug)
        }
    }

    fun update(ctx: Context) {
        val slug = ctx.validatedPathParam("slug").getOrThrow()
        ctx.validatedBody<AdDTO>()
            .check({ !it.ad?.title.isNullOrBlank() })
            .getOrThrow().ad?.also { ad ->
            adService.update(ctx.attribute("email"),slug, ad).apply {
                ctx.json(AdDTO(this))
            }
        }
    }

    fun get(ctx: Context) {
        ctx.validatedPathParam("slug")
            .check({ it.isNotBlank() })
            .getOrThrow().also { slug ->
                adService.findBySlug(ctx.attribute("email"),slug).apply {
                    ctx.json(AdDTO(this))
                }
            }
    }

    fun findBy(ctx: Context) {
        val title = ctx.queryParam("title")
        val email = ctx.queryParam("email")
        adService.findBy(title, email).also { ads ->
            ctx.json(AdsDTO(ads, ads.size))
        }
    }

}