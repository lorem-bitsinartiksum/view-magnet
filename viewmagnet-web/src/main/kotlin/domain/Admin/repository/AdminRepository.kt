package domain.Admin.repository

import com.mongodb.client.model.Filters
import domain.Admin.Admin
import org.litote.kmongo.*

class AdminRepository() {
    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("ViewMagnet") //normal java driver usage
    val col = database.getCollection<Admin>() //KMongo extension method

    fun findByEmail(email: String): Admin? {
        return col.findOne("{email:'$email'}")
    }

    fun findByUsername(username: String): Admin? {
        return col.findOne("{username:'$username'}")
    }

    fun create(admin: Admin) {
        col.insertOne(Admin(username = admin.username, email = admin.email, password = admin.password, phone = admin.phone))
    }

    fun delete(email: String){
        col.deleteOne(Filters.eq("email", email))
    }

    fun update(email: String, admin: Admin): Admin?{
        if (admin.username != null)
            col.updateMany(Filters.eq("email", email), SetTo(Admin::username, admin.username))
        if (admin.password != null)
            col.updateMany(Filters.eq("email", email), SetTo(Admin::password, admin.password))
        if (admin.phone != null)
            col.updateMany(Filters.eq("email", email), SetTo(Admin::phone, admin.phone))
        if (admin.email != null){
            col.updateMany(Filters.eq("email", email), SetTo(Admin::email, admin.email))
            return findByEmail(admin.email)
        }
        return findByEmail(email)
    }

}