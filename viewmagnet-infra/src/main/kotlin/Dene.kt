import topic.TopicContext
import topic.TopicService
import topic.internal.serde.JsonSerde

data class TestModel<T>(val i: T, val name: String)
data class Complex(val age: Int, val ss: Map<String, Int>)

fun main() {
    val l = TestModel(Complex(4, mapOf("ASD" to 2, "cvb" to 4)), "ZXC")
    val serde = JsonSerde<TestModel<*>>(l.javaClass)
    val ser = serde.serialize(l)
    println(ser)
    println(serde.deserialize(ser))

    val ts = TopicService.createFor(Complex::class.java, TopicContext())

}