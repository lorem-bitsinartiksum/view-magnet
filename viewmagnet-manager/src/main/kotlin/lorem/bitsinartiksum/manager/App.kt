package lorem.bitsinartiksum.manager


fun main() {
    val config = Config()
    val poolManager = PoolManager(config, HealthChecker())
}
