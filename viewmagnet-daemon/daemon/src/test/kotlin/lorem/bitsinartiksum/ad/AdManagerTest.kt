package lorem.bitsinartiksum.ad

import lorem.bitsinartiksum.config.Config
import lorem.bitsinartiksum.listener.CommandListener
import model.Ad
import model.AdPoolChanged
import org.junit.jupiter.api.Test
import topic.TopicContext
import topic.TopicService
import kotlin.test.assertEquals

internal class AdManagerTest {


    @Test
    fun refreshPool() {

        val ts = TopicService.createFor(AdPoolChanged::class.java, "test-pub", TopicContext())
        val testAd = Ad(
            "TESTAD",
            "https://images.unsplash.com/photo-1583073600538-f219abfb20bc?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
        )
        val adManager = AdManager({ println("Current AD: ${it.id}") }, Config())
        val cmdListener = CommandListener(Config(), adManager)
        cmdListener.start()
        adManager.start()

        ts.subscribe { println("RECEIVED $it") }
        val newPool = setOf(testAd)
        ts.publish(AdPoolChanged(newPool))
        Thread.sleep(1000)

        assertEquals(newPool, adManager.pool)
    }
}