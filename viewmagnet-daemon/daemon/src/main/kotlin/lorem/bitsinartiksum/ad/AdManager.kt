package lorem.bitsinartiksum.ad

import model.Ad
import model.AdPoolChanged
import java.nio.file.Path
import java.util.concurrent.Executors

class AdManager {

    val currentAd: Ad = Ad("default", ByteArray(5))

    init {
        startWatching()
    }

    fun refreshPool(newPool: Set<String>) {

        println("REFRESHING POOL $newPool")
    }

    inline fun <reified T> handleCommand(cmd: T) {
        when (T::class.java) {
            AdPoolChanged::class.java -> refreshPool((cmd as AdPoolChanged).newPool)
        }
    }


    private fun startWatching() {
        runPythonScript("test.py") {
            println("READ: $it")
        }
    }

    private fun runPythonScript(name: String, handler: (String) -> Unit) {

        val scriptPath = Path.of(System.getProperty("user.dir"), "viewmagnet-daemon", name)

        val builder = ProcessBuilder("python3", scriptPath.toString()).redirectErrorStream(true)
        val proc = builder.start()
        // We wont be sending anything.
        proc.outputStream.close()

        Executors.newSingleThreadExecutor().execute {

            val out = proc.inputStream
            val reader = out.bufferedReader()
            while (proc.isAlive) {
                if (out.available() > 0)
                    handler(reader.readLine())
                else {
                    Thread.sleep(10)
                }
            }
        }
    }
}