import io.javalin.Javalin

fun main() {
    val app = Javalin.create {
        it.addStaticFiles("/public")
    }.start(7000)

    app.get("/billboard/:billboard_id", { ctx ->
        ctx.html("Billboard " + ctx.pathParam("billboard_id") + " UP")
    })
}