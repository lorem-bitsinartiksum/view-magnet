package lorem.bitsinartiksum.ad


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
import model.Ad
import model.Similarity
import java.time.Duration
import kotlin.concurrent.timer

typealias AdPool = Set<Pair<Ad, Similarity>>

fun main() {
    for (i in 0..15) {
        println(i)
    }
}

class AdScheduler(private val changeAd: (Ad) -> Unit, val poolUpdates: ReceiveChannel<AdPool>, val period: Duration) {

    private var pool: AdPool = setOf(
        Ad(
            "t1",
            "https://images.unsplash.com/photo-1582740735409-d0ae8d48976e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80"
        ) to 0.5f,
        Ad(
            "t2",
            "https://images.unsplash.com/photo-1539006749419-f9a3eb2bf3fe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=701&q=80"
        ) to 0.2f,
        Ad(
            "t2",
            "https://images.unsplash.com/photo-1582999275987-a02e090da23b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=500&q=60"
        ) to 0.64f
    )

    private var schedule: List<AdTrack> = ArrayList()
    private val window = Duration.ofSeconds(20)

    init {
        runBlocking(Dispatchers.IO) {
            for (p in poolUpdates) {
                val totalSim = p.fold(0f) { total, (_, sim) -> total + sim }
                schedule = pool.map { (ad, sim) ->
                    AdTrack(ad, sim, Duration.ofMillis((window.toMillis() * sim / totalSim).toLong()))
                }
            }
        }
        var i = 0

        timer("ad-scheduler", false, 0, period.toMillis()) {
            val currentAd = schedule.getOrNull(i)
            currentAd?.remaining?.minus(period)
            val nextAd = schedule[i++ % schedule.size]
            changeAd(nextAd.ad)
        }
    }
}


//@ExperimentalCoroutinesApi
//fun main() = runBlocking<Unit> {
//
//    val ch = Channel<Int>()
//
//    launch {
//        for (x in 1..10) {
//            ch.send(x)
//            delay(500)
//        }
//    }
//    ABC(ch)
//    println("ASD")
//}

@ExperimentalCoroutinesApi
class ABC(val changes: ReceiveChannel<Int>) {
    init {
        runBlocking {
            for (x in changes)
                println("HERE $x")
        }
    }
}