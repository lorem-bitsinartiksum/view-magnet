import lorem.bitsinartiksum.manager.Billboard
import lorem.bitsinartiksum.manager.Config
import lorem.bitsinartiksum.manager.PoolManager
import model.*
import org.junit.Test
import repository.RepositoryService
import kotlin.test.assertEquals

class PoolManagerTest {

    private val repositoryService = RepositoryService.createFor(AdWithFeature::class.java)

    @Test
    fun `test1 updateBillboardPool`() {
        val ad1 = AdWithFeature("1", "qwe", listOf(0.9f, 0.2f, 0.5f))
        val ad2 = AdWithFeature("2", "asd", listOf(0.1f, 0.9f, 0.1f))
        val ad3 = AdWithFeature("3", "zxc", listOf(0.8f, 0.1f, 0.4f))
        repositoryService.save(ad1)
        repositoryService.save(ad2)
        repositoryService.save(ad3)
        val config = Config()
        val poolManager = PoolManager(config)
        val billboard = Billboard(emptySet(), listOf(0.9f, 0.2f, 0.5f), 1)
        poolManager.updateBillboardPool(billboard)
        assertEquals(billboard.pool.size, 3)
        assertEquals(billboard.pool.first().first, Ad(ad1.id, ad1.content))
        assertEquals(billboard.pool.last().first, Ad(ad2.id, ad2.content))
    }

    @Test
    fun `test2 calcNewInterest`() {
        repositoryService.save(AdWithFeature("4", "asd", listOf(0.7f, 0.7f, 0.7f)))
        val config = Config()
        val poolManager = PoolManager(config)
        val newInterest = poolManager.calcNewInterest("4", Billboard(emptySet(), listOf(0.3f, 0.2f, 0.4f), 2), 2, 1)
        assertEquals(listOf(0.5f, 0.45f, 0.55f), newInterest)
    }
}