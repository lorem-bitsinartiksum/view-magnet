package domain.User.repository

import com.mongodb.client.model.Filters
import domain.User.User
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

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
        delete(email)
        create(user)
        return user
    }

}