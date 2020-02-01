package domain.Ad.repository

import com.github.slugify.Slugify
import com.mongodb.client.model.Filters
import domain.Ad.Ad
import org.litote.kmongo.*
import java.util.*


class AdRepository() {
    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("ViewMagnet") //normal java driver usage
    val col = database.getCollection<Ad>() //KMongo extension method

    fun create(ad: Ad): Ad? {
        val now = Date()
        col.insertOne(Ad(slug = ad.slug, user = ad.user, title = ad.title, description = ad.description, content = ad.content, targetGender = ad.targetGender, targetAgeRange = ad.targetAgeRange, targetWeather = ad.targetWeather, createdAt = now, updatedAt = now))
        return findBySlug(ad.slug!!)
    }

    fun delete(slug: String) {
        col.deleteOne(Filters.eq("slug", slug))
    }

    fun update(slug: String, ad: Ad): Ad? {
        if (ad.description != null)
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::description, ad.description))
        if (ad.content != null)
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::content, ad.content))
        if (ad.targetGender != null)
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::targetGender, ad.targetGender))
        if (ad.targetAgeRange != null)
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::targetAgeRange, ad.targetAgeRange))
        if (ad.targetWeather != null)
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::targetWeather, ad.targetWeather))
        if (ad.title != null){
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::title, ad.title))
            val now = Date()
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::updatedAt, now))
            val newSlug = Slugify().slugify(ad.title)
            col.updateMany(Filters.eq("slug", slug), SetTo(Ad::slug, newSlug))
            return newSlug?.let { findBySlug(it) }
        }
        val now = Date()
        col.updateMany(Filters.eq("slug", slug), SetTo(Ad::updatedAt, now))
        return findBySlug(slug)

    }

    fun findBySlug(slug: String): Ad? {
        return col.findOne("{slug:'$slug'}")
    }

    fun findByEmail(email: String): List<Ad> {
        return col.find("{'user.email':'$email'}").toList()
    }

    fun findByTitle(title: String): List<Ad> {
        return col.find("{title:'$title'}").toList()
    }

    fun findAll(): List<Ad> {
        return col.find().toList()
    }

}