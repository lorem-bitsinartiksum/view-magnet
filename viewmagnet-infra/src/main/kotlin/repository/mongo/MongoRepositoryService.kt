package repository.mongo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import org.bson.Document
import repository.Persistable
import repository.RepositoryService
import topic.Mode
import java.util.function.Predicate
import kotlin.reflect.KClass

internal class MongoRepositoryService<T : Persistable>(override val activeMode: Mode, private val clazz: KClass<T>) :
    RepositoryService<T> {

    private val db = client.getDatabase("viewmagnet-${activeMode}")
    private val coll = db.getCollection(clazz.qualifiedName)
    private val jackson = jacksonObjectMapper()

    override fun findAll(): Iterator<T> {
        return object : Iterator<T> {
            private val cursor = coll.find().cursor()

            override fun hasNext(): Boolean {
                if (cursor.hasNext())
                    return true
                cursor.close()
                return false
            }

            override fun next(): T {
                val next = cursor.next()
                return converToObj(next)
            }
        }
    }

    override fun findById(id: String): T? {
        val query = BasicDBObject("_id", id)
        val doc = coll.find(query).first()
        return if (doc != null) converToObj(doc) else null
    }

    override fun deleteById(id: String): T? {
        val query = BasicDBObject("_id", id)
        val doc = coll.findOneAndDelete(query)
        return if (doc != null) converToObj(doc) else null
    }

    override fun clear() {
        TODO("NOT implemented")
    }

    override fun save(obj: T): T? {
        val doc = convertToDoc(obj)
        coll.insertOne(doc)
        return obj
    }

    override fun save(obj: Iterable<T>) {
        coll.insertMany(obj.map(this::convertToDoc))
    }

    // Fine until hitting perf problems..
    override fun filter(predicate: Predicate<T>): Iterator<T> {
        return object : Iterator<T> {
            private val cursor = coll.find().cursor()
            private var next: T? = checkNext()

            private fun checkNext(): T? {
                while (cursor.hasNext()) {
                    val doc = cursor.next()
                    val obj = converToObj(doc)
                    if (predicate.test(obj)) {
                        return obj
                    }
                }
                return null
            }

            override fun hasNext(): Boolean {
                return next != null
            }

            override fun next(): T {
                val current = next!!
                next = checkNext()
                return current
            }
        }
    }

    private fun convertToDoc(obj: T): Document {
        val json = jackson.writeValueAsString(obj)
        val doc = Document.parse(json)
        doc.set("_id", obj.id)
        doc.remove("id")
        return doc
    }

    private fun converToObj(doc: Document): T {
        doc.set("id", doc.getString("_id"))
        doc.remove("_id")
        val obj = jackson.readValue<T>(doc.toJson(), clazz.java)
        return obj
    }

    companion object {
        // TODO: Use cfg for address
        // Single client manages pooling itself
        private val client = MongoClient()
    }
}