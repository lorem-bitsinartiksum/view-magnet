import io.javalin.Javalin
import status.StatusProvider
import status.StatusListener

fun main() {
    val app = Javalin.create {
        it.addStaticFiles("/public")
    }.start(7000)

    StatusProvider.statusTimer()

    StatusListener.subscribeBillboardStatus()

    app.get("/billboard/:billboard_id") { ctx ->
        val billboardId = ctx.pathParam("billboard_id")
        val status = StatusProvider.getStatus(billboardId)
        ctx.html("Billboard " + billboardId + " is " + status.health.toString())
    }
}