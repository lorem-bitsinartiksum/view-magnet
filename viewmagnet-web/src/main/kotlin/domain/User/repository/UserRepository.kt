package domain.User.repository

import com.mongodb.client.model.Filters
import domain.User.User
import org.litote.kmongo.*

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
        if (user.username != null)
            col.updateMany(Filters.eq("email", email), SetTo(User::username, user.username))
        if (user.password != null)
            col.updateMany(Filters.eq("email", email), SetTo(User::password, user.password))
        if (user.phone != null)
            col.updateMany(Filters.eq("email", email), SetTo(User::phone, user.phone))
        if (user.location != null)
            col.updateMany(Filters.eq("email", email), SetTo(User::location, user.location))
        if (user.email != null){
            col.updateMany(Filters.eq("email", email), SetTo(User::email, user.email))
            return findByEmail(user.email)
        }
        return findByEmail(email)
    }

}