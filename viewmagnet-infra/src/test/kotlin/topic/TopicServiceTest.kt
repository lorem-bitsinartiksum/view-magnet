package topic


import junit.framework.Assert.fail
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

internal class TopicServiceTest {

    data class TestData(
        val i: Int = 2,
        val list: List<String> = listOf("foo", "bar", "weee"),
        val map: Map<String, Double> = mapOf("lat" to 12.24, "lon" to 23.43)
    )


    @Test
    @Timeout(3, unit = TimeUnit.SECONDS)
    fun `should receive`() {

        val ts = TopicService.createFor(TestData::class.java, "test-source")

        var ref: TestData? = null
        ts.subscribe {
            println(it.payload)
            ref = it.payload

        }
        ts.publish(TestData(2), "test-source")
        Thread.sleep(1000)
        Assertions.assertEquals(TestData(2), ref)
    }

    @Test
    fun `should'nt receive topic in another context`() {

        val tsTR = TopicService.createFor(TestData::class.java, "test-source-TR")
        val tsUK = TopicService.createFor(TestData::class.java, "test-source-UK")
        var ref: TestData? = null
        tsUK.subscribe {
            fail("Shoudlnt have receive")
        }
        tsTR.publish(TestData(2), "test-source")
        Thread.sleep(1000)

    }

    @Test
    fun subscribe() {
    }
}