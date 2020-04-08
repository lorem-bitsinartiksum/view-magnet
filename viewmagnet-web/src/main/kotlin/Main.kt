import config.AppConfig
import lorem.bitsinartiksum.manager.Config
import lorem.bitsinartiksum.manager.HealthChecker
import lorem.bitsinartiksum.manager.PoolManager

fun main(args: Array<String>) {
    val config = Config()
    val poolManager = PoolManager(config, HealthChecker())
    AppConfig().setup().start()
}


