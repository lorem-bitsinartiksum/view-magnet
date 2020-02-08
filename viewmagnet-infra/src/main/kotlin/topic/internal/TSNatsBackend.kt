package topic.internal

import com.google.common.flogger.FluentLogger
import io.nats.client.Nats
import topic.*


internal class TSNatsBackend : TSBackend {

    private val nc = Nats.connect()
    private val logger = FluentLogger.forEnclosingClass()

    override fun publish(name: String, source: String, payload: ByteArray) {

        val subject =
            "$name.${source}"

        logger.atInfo().log("Published topic to $subject")

        nc.publish(subject, payload)
    }

    override fun subscribe(name: String, source: String, consumer: (ByteArray) -> Unit): UnsubscribeToken {

        val subject = "$name.${source}"
        val sub = nc.createDispatcher { consumer(it.data) }.subscribe(subject)
        logger.atInfo().log("Subscribed to $subject")
        return {
            logger.atInfo().log("Unsubscribed from $source")
            sub.unsubscribe(subject)
        }
    }
}