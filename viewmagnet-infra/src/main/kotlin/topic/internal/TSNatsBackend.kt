package topic.internal

import com.google.common.flogger.FluentLogger
import io.nats.client.Nats
import topic.*

private val TopicContext.asSubject: String
    get() {
        val country = if (this.country == Country.ALL) "*" else this.city.toString()
        val city = if (this.city == City.ALL) "*" else this.city.toString()
        val dist = if (this.district == District.ALL) "*" else this.city.toString()
        val ind = if (this.individual == "ALL") "*" else "ALL"
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

        val subject = "$name.${context.asSubject}"
        val sub = nc.createDispatcher { consumer(it.data) }.subscribe(subject)
        logger.atInfo().log("Subscribed to $subject")
        return {
            logger.atInfo().log("Unsubscribed from $context")
            sub.unsubscribe(subject)
        }
    }
}