package domain.User.repository

import com.mongodb.client.model.Filters
import domain.Ad.Ad
import model.User
import org.litote.kmongo.*
import java.util.*

class UserRepository() {
    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("ViewMagnet") //normal java driver usage
    val col = database.getCollection<User>() //KMongo extension method

    fun findByEmail(email: String): User? {
        return col.findOne("{email:'$email'}")
    }

    fun findByUsername(username: String): User? {
        return col.findOne("{username:'$username'}")
    }

    fun create(user: User) {
        col.insertOne(User(username = user.username, email = user.email, password = user.password, phone = user.phone, location = user.location))
    }

    fun delete(email: String){
        col.deleteOne(Filters.eq("email", email))
    }

    fun update(email: String, user: User): User?{
        val colAd = database.getCollection<Ad>() //KMongo extension method

        if (user.username != null){
            colAd.updateMany(Filters.eq("user.email", email), setValue(Ad::user / User::username, user.username))
            col.updateMany(Filters.eq("email", email), SetTo(User::username, user.username))
        }
        if (user.password != null){
            colAd.updateMany(Filters.eq("user.email", email), setValue(Ad::user / User::password, user.password))
            col.updateMany(Filters.eq("email", email), SetTo(User::password, user.password))
        }
        if (user.phone != null){
            colAd.updateMany(Filters.eq("user.email", email), setValue(Ad::user / User::phone, user.phone))
            col.updateMany(Filters.eq("email", email), SetTo(User::phone, user.phone))
        }
        if (user.location != null){
            colAd.updateMany(Filters.eq("user.email", email), setValue(Ad::user / User::location, user.location))
            col.updateMany(Filters.eq("email", email), SetTo(User::location, user.location))
        }
        if (user.email != null){
            val now = Date()
            colAd.updateMany(Filters.eq("user.email", email), setValue(Ad::updatedAt, now))
            colAd.updateMany(Filters.eq("user.email", email), setValue(Ad::user / User::email, user.email))
            col.updateMany(Filters.eq("email", email), SetTo(User::email, user.email))
            return findByEmail(user.email)
        }
        val now = Date()
        colAd.updateMany(Filters.eq("user.email", email), setValue(Ad::updatedAt, now))
        return findByEmail(email)
    }

}