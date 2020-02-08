package sample.ts

import topic.TopicContext
import topic.TopicService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

data class Person(val name: String, val age: Int)

fun main() {
    subscribe()
    startPublisher()
}

fun subscribe() {
    val ts = TopicService.createFor(Person::class.java, "sample-source")
    ts.subscribe {
        println("RECEIVED ${it.payload.name}")
    }
}

fun startPublisher() {
    val ts = TopicService.createFor(Person::class.java, "sample-source")
    val count = AtomicInteger()
    Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({
        val p = Person("PERSON$count", count.get())
        println("Published $p")
        count.incrementAndGet()
        ts.publish(p)
    }, 0, 3, TimeUnit.SECONDS)
}