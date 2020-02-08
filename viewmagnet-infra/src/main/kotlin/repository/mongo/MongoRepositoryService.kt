package repository.mongo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import model.Persistable
import org.bson.Document
import repository.Predicate
import repository.RepositoryService
import topic.Mode



internal class MongoRepositoryService<T : Persistable>(override val activeMode: Mode, private val clazz: Class<T>) :
    RepositoryService<T> {

    private val db = client.getDatabase("viewmagnet-${activeMode}")
    private val coll = db.getCollection(clazz.name)
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
                return convertToObj(next)
            }
        }
    }

    override fun findById(id: String): T? {
        val query = BasicDBObject("_id", id)
        val doc = coll.find(query).first()
        return if (doc != null) convertToObj(doc) else null
    }

    override fun find(predicate: Predicate<T>): T? {
        val cursor = coll.find().cursor()

        while (cursor.hasNext()) {
            val obj = convertToObj(cursor.next())
            if (predicate(obj))
                return obj
        }
        return null
    }

    override fun deleteById(id: String): T? {
        val query = BasicDBObject("_id", id)
        val doc = coll.findOneAndDelete(query)
        return if (doc != null) convertToObj(doc) else null
    }

    override fun clear() {
        val query = BasicDBObject()
        coll.deleteMany(query)
    }

    override fun save(obj: T) {
        val doc = convertToDoc(obj)
        coll.insertOne(doc)
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
                    val obj = convertToObj(doc)
                    if (predicate(obj)) {
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

    private fun convertToObj(doc: Document): T {
        doc.set("id", doc.getString("_id"))
        doc.remove("_id")
        val obj = jackson.readValue<T>(doc.toJson(), clazz)
        return obj
    }

    companion object {
        // TODO: Use cfg for address
        // Single client manages pooling itself
        private val client = MongoClient()
    }
}