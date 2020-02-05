package repository

import repository.mongo.MongoRepositoryService
import topic.Mode
import java.util.function.Predicate
import kotlin.reflect.KClass


interface Persistable {
    val id: String
}

interface RepositoryService<T : Persistable> {

    val activeMode: Mode

    fun findAll(): Iterator<T>

    fun findById(id: String): T?

    fun deleteById(id: String): T?

    fun clear()

    fun save(obj: T): T?

    fun save(obj: Iterable<T>)

    fun filter(predicate: Predicate<T>): Iterator<T>

    companion object {
        fun <T : Persistable> createFor(clazz: KClass<T>): RepositoryService<T> {
            return MongoRepositoryService(Mode.SIM, clazz)
        }
    }
}



