import lorem.bitsinartiksum.manager.Billboard
import lorem.bitsinartiksum.manager.Config
import lorem.bitsinartiksum.manager.HealthChecker
import lorem.bitsinartiksum.manager.PoolManager
import model.*
import org.junit.Test
import repository.RepositoryService
import kotlin.test.assertEquals

class PoolManagerTest {

    private val config = Config(similarityThreshold = 0.2f, mode = Mode.SIM)

    private val repositoryService = RepositoryService.createFor(AdWithFeature::class.java, config.mode)

    @Test
    fun `test1 updateBillboardPool`() {
        val ad1 = AdWithFeature("1", "qwe", listOf(0.9f, 0.2f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f))
        val ad2 = AdWithFeature("2", "asd", listOf(0.1f, 0.9f, 0.1f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f))
        val ad3 = AdWithFeature("3", "zxc", listOf(0.8f, 0.1f, 0.4f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f))
        repositoryService.save(ad1)
        repositoryService.save(ad2)
        repositoryService.save(ad3)
        val poolManager = PoolManager(config, HealthChecker())
        val billboard = Billboard(emptySet(), listOf(0.9f, 0.2f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), 1)
        poolManager.updateBillboardPool(billboard)
        assertEquals(billboard.pool.size, 3)
        assertEquals(billboard.pool.first().first, Ad(ad1.id, ad1.content))
        assertEquals(billboard.pool.last().first, Ad(ad2.id, ad2.content))
    }

    @Test
    fun `test2 calcNewInterest`() {
        repositoryService.save(AdWithFeature("4", "asd", listOf(0.7f, 0.7f, 0.7f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)))
        val poolManager = PoolManager(config, HealthChecker())
        val newInterest = poolManager.calcNewInterest("4", Billboard(emptySet(), listOf(0.3f, 0.2f, 0.4f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), 2), 100, 100)
        assertEquals(listOf(0.5f, 0.45f, 0.55f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), newInterest)
    }
}