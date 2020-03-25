package topic.internal

import com.google.common.flogger.FluentLogger
import io.nats.client.Nats
import topic.*

private val TopicContext.asSubject: String
    get() {
        val country = if (this.country == Country.ALL) "*" else this.city.toString()
        val city = if (this.city == City.ALL) "*" else this.city.toString()
        val dist = if (this.district == District.ALL) "*" else this.city.toString()
        val ind = individual
        return "$mode.$country.$city.$dist.$ind"
    }

internal class TSNatsBackend : TSBackend {

    private val nc = Nats.connect()
    private val logger = FluentLogger.forEnclosingClass()

    override fun publish(name: String, context: TopicContext, payload: ByteArray) {

        val subject =
            "$name.${context.mode}.${context.country}.${context.city}.${context.district}.${context.individual}"

        nc.publish(subject, payload)
        logger.atInfo().log("Published topic to $subject")
    }

    override fun subscribe(name: String, context: TopicContext, consumer: (ByteArray) -> Unit): UnsubscribeToken {

        val unsubTokens = mutableListOf<UnsubscribeToken>()
        val natsSub = { subject: String ->
            val sub = nc.createDispatcher {
                consumer(it.data)
            }.subscribe(subject)
            unsubTokens.add({ sub.unsubscribe(subject) })
        }

        if (context.individual != "ALL") {
            natsSub("${name}.${context.copy(individual = "ALL").asSubject}")
        }

        natsSub("${name}.${context.asSubject}")

        logger.atInfo().log("Subscribed to $name")
        return {
            logger.atInfo().log("Unsubscribed from $context")
            unsubTokens.forEach { it() }
        }
    }
}