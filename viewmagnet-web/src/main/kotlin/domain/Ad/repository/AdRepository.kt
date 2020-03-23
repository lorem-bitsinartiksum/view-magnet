package domain.Ad.repository

import com.github.slugify.Slugify
import com.mongodb.client.model.Filters
import model.Ad
import model.AdReq
import org.litote.kmongo.*
import java.util.*


class AdRepository() {
    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("ViewMagnet") //normal java driver usage
    val col = database.getCollection<Ad>() //KMongo extension method

    fun create(ad: Ad): Ad? {
        val now = Date()
        col.insertOne(Ad(id = ad.id, user = ad.user, title = ad.title, description = ad.description, content = ad.content, targetGender = ad.targetGender, targetAge = ad.targetAge, targetWeather = ad.targetWeather, targetLowTemp = ad.targetLowTemp, targetHighTemp = ad.targetHighTemp, targetLowSoundLevel = ad.targetLowSoundLevel, targetHighSoundLevel = ad.targetHighSoundLevel, createdAt = now, updatedAt = now))
        return findById(ad.id!!)
    }

    fun delete(id: String) {
        col.deleteOne(Filters.eq("id", id))
    }

    fun update(id: String, ad: AdReq): Ad? {
        if (ad.description != null)
            col.updateMany(Filters.eq("id", id), SetTo(Ad::description, ad.description))
        if (ad.content != null)
            col.updateMany(Filters.eq("id", id), SetTo(Ad::content, ad.content))
        if (ad.targetGender != null)
            col.updateMany(Filters.eq("id", id), SetTo(Ad::targetGender, ad.targetGender))
        if (ad.targetAge != null)
            col.updateMany(Filters.eq("id", id), SetTo(Ad::targetAge, ad.targetAge))
        if (ad.targetWeather != null)
            col.updateMany(Filters.eq("id", id), SetTo(Ad::targetWeather, ad.targetWeather))
        if (ad.title != null){
            col.updateMany(Filters.eq("id", id), SetTo(Ad::title, ad.title))
            val now = Date()
            col.updateMany(Filters.eq("id", id), SetTo(Ad::updatedAt, now))
            val newId = Slugify().slugify(ad.title)
            col.updateMany(Filters.eq("id", id), SetTo(Ad::id, newId))
            return newId?.let { findById(it) }
        }
        val now = Date()
        col.updateMany(Filters.eq("id", id), SetTo(Ad::updatedAt, now))
        return findById(id)

    }

    fun findById(id: String): Ad? {
        return col.findOne("{id:'$id'}")
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

    fun findByAge(targetAge: String): List<Ad> {
        return col.find("{targetAge:'$targetAge'}").toList()
    }

    fun findByGender(targetGender: String): List<Ad> {
        return col.find("{targetGender:'$targetGender'}").toList()
    }

    fun findByWeather(targetWeather: String): List<Ad> {
        return col.find("{targetWeather:'$targetWeather'}").toList()
    }

    fun findByFilters(title: String?, email: String?, targetAge: String?, targetGender: String?, targetWeather: String?): List<Ad> {

        var filteredList = col.find().toList()
        if(!title.isNullOrBlank()){
            var titleFilter = "{title:'$title'}"
            filteredList = filteredList.intersect(col.find(titleFilter).toList()).toList()
        }
        if(!email.isNullOrBlank()){
            var emailFilter = "{'user.email':'$email'}"
            filteredList = filteredList.intersect(col.find(emailFilter).toList()).toList()
        }
        if(!targetAge.isNullOrBlank()){
            var targetAgeFilter = "{targetAge:'$targetAge'}"
            filteredList = filteredList.intersect(col.find(targetAgeFilter).toList()).toList()
        }
        if(!targetGender.isNullOrBlank()){
            var targetGenderFilter = "{targetGender:'$targetGender'}"
            filteredList = filteredList.intersect(col.find(targetGenderFilter).toList()).toList()
        }
        if(!targetWeather.isNullOrBlank()){
            var targetWeatherFilter = "{targetWeather:'$targetWeather'}"
            filteredList = filteredList.intersect(col.find(targetWeatherFilter).toList()).toList()
        }
        return filteredList
    }

}