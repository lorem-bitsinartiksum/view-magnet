package lorem.bitsinartiksum

import kotlinx.coroutines.runBlocking
import java.util.logging.LogManager

fun main() = runBlocking<Unit> {
    LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"))
    val d = Daemon()
    d.start()
}
