package lorem.bitsinartiksum.manager

import java.util.*


fun main() {
    val config = Config()
    Objects.requireNonNull(System.getProperty("daemon")) { "Please set daemon path. -Ddaemon=<jarLocation>" }
    val poolManager = PoolManager(config, HealthChecker())
    val server = ApiServer(BillboardService(poolManager))
    server.start()

}
