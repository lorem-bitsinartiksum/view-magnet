package sample.rs


import model.Mode
import model.Persistable
import repository.RepositoryService
import java.util.*

data class Foob(override val id: String, val things: Set<Int>, val otherThings: IntRange) : Persistable

val rs = RepositoryService.createFor(Mode.SIM, Foob::class.java)
val f = Foob(UUID.randomUUID().toString(), setOf(2, 4, 5, 6, 7), 1..5)
fun main() {

    rs.filter {
        it.otherThings.contains(2)
    }.forEach {
        println(it)
    }

    deleteAll()
//
//    rs.save(f)
//    read()
}

fun save() {
    rs.save(f)
}

fun read() {
    rs.findAll().forEach {
        println(it)
    }
}

fun deleteAll() {
    val ids = rs.findAll().asSequence().map { it.id }
    ids.forEach { rs.deleteById(it) }
}