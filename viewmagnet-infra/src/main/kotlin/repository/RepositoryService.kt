package repository

import model.Mode
import model.Persistable
import repository.mongo.MongoRepositoryService


typealias Predicate<T> = (T) -> Boolean


// TODO add a filter interface for querying db instead of preds.
interface RepositoryService<T : Persistable> {

    val activeMode: Mode

    fun findAll(): Iterator<T>

    fun findById(id: String): T?

    fun deleteById(id: String): T?

    fun find(predicate: Predicate<T>): T?

    fun clear()

    fun save(obj: T)

    fun save(obj: Iterable<T>)

    fun filter(predicate: Predicate<T>): Iterator<T>

    companion object {
        fun <T : Persistable> createFor(clazz: Class<T>): RepositoryService<T> {
            return MongoRepositoryService(Mode.SIM, clazz)
        }
    }
}



